import java.util.Properties

plugins {
    // Remapping plugin; settings.gradle.kts enables no-remap mode for unobfuscated 26.x jars.
    alias(libs.plugins.fabric.loom.remap)
    id("maven-publish")
}

// Multi-version build.
//
// Every supported Minecraft version is described by gradle/mc-versions/<version>.properties
// and may ship its own sources in src/mc/<version>/. The shared code in src/main/java stays
// version-agnostic and programs against the compatibility layer (see COMPAT.md).
//
// Select a version with -PminecraftVersion=<version>; the default is taken from libs.versions.toml.
val defaultMcVersion = libs.versions.minecraft.get()
val mcVersion = providers.gradleProperty("minecraftVersion").getOrElse(defaultMcVersion)
val mcVersionsDir = file("gradle/mc-versions")
val mcDescriptor = File(mcVersionsDir, "$mcVersion.properties")

require(mcDescriptor.isFile) {
    buildString {
        append("Unknown Minecraft version '").append(mcVersion).append("'. Expected a descriptor at ").append(mcDescriptor).append(". ")
        val available = mcVersionsDir.listFiles()?.filter { it.name.endsWith(".properties") }
            ?.map { it.name.removeSuffix(".properties") }?.sorted().orEmpty()
        append(if (available.isEmpty()) "No version descriptors found." else "Available versions: ${available.joinToString(", ")}.")
    }
}

val mcProperties = Properties().apply { mcDescriptor.inputStream().use { load(it) } }

fun mc(key: String): String = mcProperties.getProperty(key)
    ?: error("Missing property '$key' in $mcDescriptor")

logger.lifecycle("Building Meteor Client for Minecraft $mcVersion")

val archivesBaseName = providers.gradleProperty("archives_base_name").get()
val mavenGroup = providers.gradleProperty("maven_group").get()

base {
    archivesName = archivesBaseName
    group = mavenGroup

    val suffix = providers.gradleProperty("build_number").getOrElse("local")
    version = "${mc("minecraft")}-$suffix"
}

repositories {
    maven {
        name = "meteor-maven"
        url = uri("https://maven.meteordev.org/releases")
    }
    maven {
        name = "meteor-maven-snapshots"
        url = uri("https://maven.meteordev.org/snapshots")
    }
    maven {
        name = "ViaVersion"
        url = uri("https://repo.viaversion.com")
    }
    maven {
        name = "jitpack"
        url = uri("https://jitpack.io")
    }
    mavenCentral()

    exclusiveContent {
        forRepository {
            maven {
                name = "modrinth"
                url = uri("https://api.modrinth.com/maven")
            }
        }
        filter {
            includeGroup("maven.modrinth")
        }
    }
}

// Releases predating the universal compatibility layer use a source snapshot from the
// matching upstream update commit. This keeps old mappings intact instead of forcing
// 26.x-shaped shared code onto them.
val legacySourceCommit = mcProperties.getProperty("legacy-source")
val legacySourceDir = if (legacySourceCommit != null) {
    val dir = layout.buildDirectory.dir("legacy-sources/$mcVersion").get().asFile
    if (!File(dir, "src/main/java").isDirectory || !File(dir, "gradle.properties").isFile) {
        dir.mkdirs()
        val archive = File(dir, "snapshot.tar")
        val archiveStatus = ProcessBuilder("git", "archive", legacySourceCommit, "src/main/java", "src/main/resources", "gradle.properties")
            .directory(rootDir).redirectOutput(archive).redirectErrorStream(true).start().waitFor()
        check(archiveStatus == 0) {
            "Failed to extract legacy snapshot $legacySourceCommit. Ensure the full Git history is available (fetch-depth: 0 in CI)."
        }
        val extractStatus = ProcessBuilder("tar", "-xf", archive.name).directory(dir).start().waitFor()
        check(extractStatus == 0) { "Failed to unpack legacy snapshot $legacySourceCommit." }
        archive.delete()

        // Historical snapshots predate Meteor's access widener. Use an empty named-space
        // widener so Loom's validation does not reject the shared 26.x file on these versions.
        if (!File(mcVersionsDir, "$mcVersion.classtweaker").isFile &&
            !File(dir, "src/main/resources/meteor-client.accesswidener").isFile) {
            val widener = File(dir, "src/main/resources/meteor-client.accesswidener")
            widener.parentFile.mkdirs()
            widener.writeText("accessWidener\tv2\tnamed\n")
        }

        val legacyYarnVersion = File(dir, "gradle.properties").readLines().firstNotNullOfOrNull { line ->
            val trimmed = line.trim()
            if (trimmed.startsWith("yarn_mappings=") || trimmed.startsWith("yarn_version=")) {
                trimmed.substringAfter('=').trim()
            } else null
        } ?: ""
        val legacyJava = File(dir, "src/main/java")
        val pkgDir = legacyJava.walkTopDown().filter { it.isFile && it.name == "MeteorClient.java" }.firstOrNull()?.parentFile ?: legacyJava
        val pkg = pkgDir.relativeTo(legacyJava).path.replace(File.separatorChar, '.')

        // Starscript moved from meteordevelopment to org.meteordev; the local jar only has
        // the new coordinates, so rewrite the imports in the historical snapshots.
        legacyJava.walkTopDown().filter { it.isFile && it.name.endsWith(".java") }.forEach { file ->
            val text = file.readText()
            val rewritten = text
                .replace("meteordevelopment.starscript", "org.meteordev.starscript")
                .replace("minegame159.starscript", "org.meteordev.starscript")
            if (rewritten != text) file.writeText(rewritten)
        }

        // Baritone is an optional runtime mod; compile the legacy sources against a small
        // stub instead of dropping the modules that integrate with it.
        val usesFabricApi = legacyJava.walkTopDown().filter { it.isFile && it.name.endsWith(".java") }
            .any { "net.fabricmc.fabric.api" in it.readText() }
        File(rootDir, "scripts/legacy-stubs/baritone").copyRecursively(File(legacyJava, "baritone"), overwrite = true)
        if (usesFabricApi) {
            File(rootDir, "scripts/legacy-stubs/net").copyRecursively(File(legacyJava, "net"), overwrite = true)
            if (legacyYarnVersion.startsWith("1.14") || legacyYarnVersion.startsWith("1.15")) {
                legacyJava.walkTopDown().filter { it.isFile && it.path.contains("fabricmc/fabric/api/client/keybinding") }.forEach { file ->
                    file.writeText(file.readText().replace("InputUtil.Key", "InputUtil.KeyCode"))
                }
            }
        }

        fun replaceMethod(text: String, signature: String, replacement: String, next: String): String {
            val start = text.indexOf(signature)
            if (start < 0) return text
            val end = text.indexOf(next, start)
            return if (end < 0) text else text.substring(0, start) + replacement + text.substring(end)
        }

        val discord = File(pkgDir, "systems/modules/misc/DiscordPresence.java")
        if (discord.isFile) {
            discord.writeText(discord.readText().replace("me.jellysquid.mods.sodium.client", "sodium.client"))
        }

        listOf("systems/modules/render/Xray.java", "systems/modules/render/WallHack.java").forEach { rel ->
            val file = File(pkgDir, rel)
            if (file.isFile) {
                file.writeText(file.readText()
                    .replace("import net.irisshaders.iris.api.v0.IrisApi;\n", "")
                    .replace("IrisApi.getInstance().isShaderPackInUse()", "false"))
            }
        }

        val optionalMarkers = listOf(
            "de.florianmichael.waybackauthlib", "net.caffeinemc.mods.sodium",
            "net.caffeinemc.mods.lithium", "me.jellysquid.mods.sodium",
            "me.jellysquid.mods.lithium", "link.infra.indium",
            "com.viaversion.viafabricplus", "de.florianmichael.viafabricplus"
        )
        val keepPatched = setOf("DiscordPresence.java")
        val optionalFiles = legacyJava.walkTopDown().filter { file ->
            file.isFile && file.name.endsWith(".java") && file.name !in keepPatched && file.readText().let { text -> optionalMarkers.any { it in text } }
        }.toList().toMutableList()
        val legacyOptionalNames = setOf(
            "TheAlteningAccount.java", "AddAlteningAccountScreen.java", "AddTheAlteningAccountScreen.java",
            "AbstractBlockRenderContextMixin.java", "AbstractQuadRendererMixin.java",
            "CanvasWorldRendererMixin.java", "TopBarBaritone.java", "BaritoneTab.java",
            "TerrainRenderContextMixin.java", "AoCalculatorMixin.java"
        )
        legacyJava.walkTopDown().filter { it.isFile && it.name in legacyOptionalNames }.forEach { optionalFiles.add(it) }
        // 1.17+ snapshots use the same package as the launcher Main class; keep only the launcher one.
        if (pkg == "meteordevelopment.meteorclient") {
            val mainSource = File(pkgDir, "Main.java")
            if (mainSource.isFile) optionalFiles.add(mainSource)
        }
        val optionalMixins = optionalFiles.map { it.name.removeSuffix(".java") }.toSet()
        optionalFiles.forEach { it.delete() }

        val accounts = File(pkgDir, "systems/accounts/Accounts.java")
        if (accounts.isFile) {
            accounts.writeText(accounts.readText()
                .replace("import $pkg.systems.accounts.types.TheAlteningAccount;\n", "")
                .replace("                    case TheAltening -> new TheAlteningAccount(null).fromTag(t);\n", "                    case TheAltening -> null;\n")
                .replace("account = new TheAlteningAccount(null).fromTag(t);", "account = null;"))
        }
        legacyJava.walkTopDown().filter { it.isFile && it.name == "AccountsScreen.java" }.forEach { screen ->
            screen.writeText(screen.readText().replace(Regex(".*AddAlteningAccountScreen.*\n"), ""))
        }
        legacyJava.walkTopDown().filter { it.isFile && it.name.endsWith(".java") }.forEach { file ->
            val text = file.readText()
            if ("TheAlteningAccount" in text) {
                file.writeText(text.replace(Regex(".*TheAlteningAccount.*\n"), ""))
            }
        }
        legacyJava.walkTopDown().filter { it.isFile && it.name.endsWith(".java") }.forEach { file ->
            val text = file.readText()
            if ("BaritoneUtils.getPrefix()" in text) file.writeText(text.replace("BaritoneUtils.getPrefix()", "\"baritone\""))
        }
        if (legacyYarnVersion.startsWith("1.15")) {
            legacyJava.walkTopDown().filter { it.isFile && it.name == "Capes.java" }.forEach { file ->
                file.writeText(file.readText().replace(
                    "MinecraftClient.getInstance().getTextureManager().registerTexture(this, new NativeImageBackedTexture(img));",
                    "// Cape texture registration is disabled in this compatibility snapshot."))
            }
            legacyJava.walkTopDown().filter { it.isFile && it.name == "MyRenderLayer.java" }.forEach { file ->
                file.writeText(file.readText().replace("RenderLayer.OutlineMode.IS_OUTLINE", "true"))
            }
        }
        legacyJava.walkTopDown().filter { it.isFile && it.name == "TopBarType.java" }.forEach { file ->
            file.writeText(file.readText().replace(Regex(".*TopBarBaritone.*\n"), ""))
        }
        val tabs = File(pkgDir, "gui/tabs/Tabs.java")
        if (tabs.isFile) {
            tabs.writeText(tabs.readText().replace("        add(new BaritoneTab());\n", ""))
        }
        val oldStarscript = legacyYarnVersion.startsWith("1.14") || legacyYarnVersion.startsWith("1.15") ||
            legacyYarnVersion.startsWith("1.16") || legacyYarnVersion.startsWith("1.17") ||
            legacyYarnVersion.startsWith("1.18")
        if (oldStarscript) {
            listOf("line1Scripts.get(i)", "line2Scripts.get(i)", "script").forEach { arg ->
                legacyJava.walkTopDown().filter { it.isFile && it.name.endsWith(".java") }.forEach { file ->
                    val text = file.readText()
                    val call = "ss.run($arg)"
                    if (call in text) {
                        file.writeText(text.replace(call, "ss.run($arg).toString()")
                            .replace("ss.run($arg).toString().toString()", "ss.run($arg).toString()"))
                    }
                }
            }
        }
        val asm = File(pkgDir, "asm/Asm.java")
        if (asm.isFile) {
            val text = asm.readText()
            val stray = "\n\n    @Override\n    public boolean couldTransformClass(org.spongepowered.asm.mixin.MixinEnvironment environment, String className) { return false; }\n"
            val cleaned = text.replace(stray, "")
            asm.writeText(cleaned.replace(
                "        public IExtensionRegistry getExtensions() {\n            return delegate.getExtensions();\n        }\n",
                "        public IExtensionRegistry getExtensions() {\n            return delegate.getExtensions();\n        }\n\n" +
                "        @Override\n        public boolean couldTransformClass(MixinEnvironment environment, String className) { return false; }\n"))
        }
        val mixinJson = File(dir, "src/main/resources/meteor-client.mixins.json")
        if (mixinJson.isFile && optionalMixins.isNotEmpty()) {
            val filtered = mixinJson.readLines().filterNot { line ->
                optionalMixins.any { name -> line.trim().trim(',').trim('"').endsWith(name) }
            }.joinToString("\n")
            mixinJson.writeText(filtered.replace(Regex(",\\s*([\\]}])"), "$1"))
        }
    }
    dir
} else null

val legacyProps = if (legacySourceDir != null) Properties().apply {
    File(legacySourceDir, "gradle.properties").inputStream().use { load(it) }
} else null

val modInclude = configurations.create("modInclude")
val jij = configurations.create("jij")
val launcher = sourceSets.create("launcher") {
    java.srcDir("src/launcher/java")
}

// Version-specific sources. Only the selected version's directory is compiled, so every
// supported version can use its own mappings for the version-fragile parts of the mod.
sourceSets.named("main") {
    if (legacySourceDir != null) {
        java.setSrcDirs(listOf(File(legacySourceDir, "src/main/java")))
        resources.setSrcDirs(listOf(File(legacySourceDir, "src/main/resources")))
    } else {
        java.srcDir("src/mc/$mcVersion")

        // Vendored, Minecraft-independent third-party sources (ViaVersion engine, ViaNBT,
        // snakeyaml). Kept in their own source root so the translated code stays isolated
        // from Meteor's own sources and can be updated independently.
        java.srcDir("src/via/java")
        resources.srcDir("src/via/resources")

        // The version tree is a source root of the java sources only. Registering the same directory
        // as a resources root as well would make every file of it appear twice in the source set, and
        // a task that merges the sources would then fail with a duplicate entry. Resources that are
        // specific to a version therefore live in src/mc/<version>/resources.
        resources.srcDir("src/mc/$mcVersion/resources")
    }
}

configurations {
    // include mods
    implementation.configure {
        extendsFrom(modInclude)
    }
    include.configure {
        extendsFrom(modInclude)
    }

    // include libraries (jar-in-jar)
    implementation.configure {
        extendsFrom(jij)
    }
    include.configure {
        extendsFrom(jij)
    }
}

dependencies {
    // Fabric. Obfuscated 1.x jars need named mappings for the compile classpath.
    // Older Minecraft manifests also omit libraries that modern ones bundle, so add
    // the ones the shared sources compile against explicitly.
    if (mcVersion.startsWith("1.")) {
        val legacyMappings = mcProperties.getProperty("legacy-mappings") ?: (legacyProps?.getProperty("yarn_mappings") ?: legacyProps?.getProperty("yarn_version"))
        if (legacyMappings != null) {
            "mappings"("net.fabricmc:yarn:$legacyMappings:v2")
        } else {
            "mappings"(loom.officialMojangMappings())
        }
        compileOnly("net.fabricmc:sponge-mixin:0.17.3+mixin.0.8.7")
        compileOnly("io.github.llamalad7:mixinextras-fabric:0.5.4")
        compileOnly("org.slf4j:slf4j-api:2.0.17")
        compileOnly("org.jspecify:jspecify:1.0.0")
        compileOnly("org.joml:joml:1.10.8")
        compileOnly("org.apache.commons:commons-lang3:3.19.0")
    }
    minecraft("com.mojang:minecraft:${mc("minecraft")}")
    implementation(libs.fabric.loader)

    val fapiVersion = mc("fabric-api")
    // Fabric API before 1.19.2 is one monolithic jar; later versions split into modules and
    // fabric-resource-loader-v0 was renamed to -v1 in 1.21.9. The descriptor selects which shape
    // to use: no `fabric-api-modules` means the current module layout, an empty value means the
    // monolithic jar, anything else is the comma separated list of modules to include.
    val fapiModulesProperty = mcProperties.getProperty("fabric-api-modules")
    if (legacySourceCommit == null) {
        if (fapiModulesProperty == null) {
            modInclude(fabricApi.module("fabric-api-base", fapiVersion))
            modInclude(fabricApi.module("fabric-resource-loader-v1", fapiVersion))
            compileOnly(fabricApi.module("fabric-renderer-indigo", fapiVersion))
        } else {
            val modules = fapiModulesProperty.split(",").map { it.trim() }.filter { it.isNotEmpty() }
            if (modules.isEmpty()) {
                modInclude("net.fabricmc.fabric-api:fabric-api:$fapiVersion")
            } else {
                modules.forEach { modInclude(fabricApi.module(it, fapiVersion)) }
                // Indigo is compile-only for every modular Fabric API release.
                compileOnly(fabricApi.module("fabric-renderer-indigo", fapiVersion))
            }
        }
    }


    // Libraries (JAR-in-JAR)
    jij(libs.orbit)
    jij(libs.starscript)
    jij(libs.minecraft.auth) {
        exclude("com.google.code.gson")
        exclude("com.google.errorprone")
    }

    if (legacyProps != null) {
        val orbit = legacyProps.getProperty("orbit_version") ?: libs.versions.orbit.get()
        jij("meteordevelopment:orbit:$orbit")

        val discord = legacyProps.getProperty("discordipc_version")
        if (discord != null) {
            jij("meteordevelopment:discord-ipc:$discord")
        } else {
            val oldDiscord = legacyProps.getProperty("discordrpc_version")
            if (oldDiscord != null) jij("club.minnced:java-discord-rpc:$oldDiscord")
        }
        val reflections = legacyProps.getProperty("reflections_version")
        if (reflections != null) implementation("org.reflections:reflections:$reflections")
        val netty = legacyProps.getProperty("netty_version") ?: "4.1.90.Final"
        implementation("io.netty:netty-handler-proxy:$netty") { isTransitive = false }
        val versionCompare = legacyProps.getProperty("versioncompare_version") ?: "1.3.7"
        implementation("com.github.G00fY2:version-compare:$versionCompare")
        compileOnly("club.minnced:java-discord-rpc:2.0.2")
        compileOnly("com.google.code.findbugs:jsr305:3.0.2")

        // Pre-1.17 snapshots use Alpine, typetools and the Vatuu Discord RPC.
        if (legacyProps.getProperty("baritone_version") == null) {
            compileOnly("com.github.ZeroMemes:Alpine:1.9")
            compileOnly("net.jodah:typetools:0.5.0")
            compileOnly("com.github.Vatuu:discord-rpc:1.6.2")
        }
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(libs.versions.jdk.get().toInt()))
    }

    if (System.getenv("CI")?.toBoolean() == true) {
        // Version-specific source trees are not Javadoc-clean (they intentionally reference
        // APIs from a single Minecraft release), so only publish sources in CI builds.
        withSourcesJar()
    }
}

// Handle transitive dependencies for jar-in-jar
// Based on implementation from BaseProject by florianreuth/EnZaXD
// Source: https://github.com/florianreuth/BaseProject/blob/main/src/main/kotlin/de/florianreuth/baseproject/Fabric.kt
// Licensed under Apache License 2.0
val jijExcluded = setOf("org.slf4j", "jsr305")
listOf("api", "implementation", "include").forEach { configName ->
    configurations.named(configName).configure {
        defaultDependencies {
            configurations.getByName("jij").incoming.resolutionResult.allComponents
                .mapNotNull { it.id as? ModuleComponentIdentifier }
                .forEach { id ->
                    val notation = "${id.group}:${id.module}:${id.version}"
                    if (jijExcluded.none { notation.contains(it) }) {
                        add(project.dependencies.create(notation) {
                            isTransitive = false
                        })
                    }
                }
        }
    }
}

// The access widener is version specific too: prefer gradle/mc-versions/<version>.classtweaker
// and fall back to the shared one in src/main/resources.
val selectedAccessWidener = sequenceOf(
    *(if (legacySourceDir != null) arrayOf(File(legacySourceDir, "src/main/resources/meteor-client.accesswidener")) else emptyArray()),
    File(mcVersionsDir, "$mcVersion.classtweaker"),
    file("src/main/resources/meteor-client.classtweaker")
).first { it.isFile }

// 1.x jars are remapped at build time and Loom expects the widener in the named namespace.
// The checked-in files use the older "official" header even though their entries are Yarn names.
val mcAccessWidener = if (mcVersion.startsWith("1.")) {
    val generated = layout.buildDirectory.file("generated/accesswidener-$mcVersion.classtweaker").get().asFile
    generated.parentFile.mkdirs()
    var body = selectedAccessWidener.readText().substringAfter('\n')
    if (mcVersion == "1.21.11") {
        body = body.lines().filterNot { "ResourceLocation" in it }.joinToString("\n", postfix = "\n")
    }
    if (mcVersion.startsWith("1.15")) {
        body = body.lines().filterNot { "RenderLayer\$OutlineMode" in it }.joinToString("\n", postfix = "\n")
    }
    generated.writeText("accessWidener\tv2\tnamed\n" + body)
    generated
} else selectedAccessWidener

// Some version-tree sources name Minecraft classes that do not exist in the selected
// version (e.g. 26.x GPU render mixins on 1.14.4). The descriptor lists them explicitly
// so the remaining sources can still be compiled.
val excludedSources = if (legacySourceCommit != null) emptyList() else mcProperties.getProperty("exclude-sources")?.split(',')?.map { it.trim() }?.filter { it.isNotEmpty() } ?: emptyList()
// Plain paths exclude the shared source file; "mc:" prefixed paths exclude the version-tree copy.
val excludedSharedSources = excludedSources.filter { !it.startsWith("mc:") }.toSet()
val excludedMcSources = excludedSources.filter { it.startsWith("mc:") }.map { it.removePrefix("mc:") }.toSet()
val sharedSourceRoot = file("src/main/java").canonicalPath
val mcSourceRoot = file("src/mc/$mcVersion").canonicalPath
tasks.named<JavaCompile>("compileJava") {
    source = source.matching {
        exclude { details ->
            val path = details.file.canonicalPath
            when {
                path.startsWith(sharedSourceRoot + File.separator) -> {
                    val rel = path.substring(sharedSourceRoot.length + 1).replace('\\', '/')
                    rel in excludedSharedSources
                }
                path.startsWith(mcSourceRoot + File.separator) -> {
                    val rel = path.substring(mcSourceRoot.length + 1).replace('\\', '/')
                    rel in excludedMcSources
                }
                else -> false
            }
        }
    }
}

// Keep the mixin config in sync: drop entries whose implementation was excluded.
tasks.named<ProcessResources>("processResources") {
    if (mcVersion.startsWith("1.")) {
        // The checked-in widener is in the official namespace; the generated one is named.
        exclude("meteor-client.classtweaker")
        from(mcAccessWidener) { rename { "meteor-client.classtweaker" } }
    }
    val excludedMixinNames = (excludedSharedSources + excludedMcSources).mapNotNull {
        it.substringAfterLast('/').takeIf { _ -> it.contains("/mixin/") }?.removeSuffix(".java")
    }.toSet()
    if (excludedMixinNames.isNotEmpty()) {
        filesMatching("meteor-client.mixins.json") {
            filter { line ->
                val name = line.trim().trim(',').trim('"')
                if (name in excludedMixinNames) "" else line
            }
        }
        doLast {
            val mixinJson = destinationDir.resolve("meteor-client.mixins.json")
            if (mixinJson.exists()) {
                val text = mixinJson.readText()
                // Remove trailing commas before closing brackets left by the line filter.
                mixinJson.writeText(text.replace(Regex(",\\s*([\\]}])"), "$1"))
            }
        }
    }
}

loom {
    accessWidenerPath = mcAccessWidener

    // Fabric API before ~1.21.9 ships class tweakers in the intermediary namespace that Loom
    // 1.x refuses to read during Minecraft setup. They are only needed at runtime; Meteor's own
    // access widener (above) is the one that has to be applied at compile time.
    if (mcProperties.getProperty("fabric-api-modules") != null) {
        enableTransitiveAccessWideners = false
    }
}

// Architecture guard: the version-agnostic part of the compatibility layer must never
// import Minecraft directly. Everything Minecraft-specific belongs in a compat/impl package.
val compatApiDirs = buildList {
    file("src/main/java/meteordevelopment/meteorclient/compat")
        .takeIf { it.isDirectory }?.let { add(it) }

    file("src/mc").listFiles()?.filter { it.isDirectory }?.forEach { versionDir ->
        File(versionDir, "meteordevelopment/meteorclient/compat")
            .takeIf { it.isDirectory }?.let { add(it) }
    }
}

val checkCompatLayer = tasks.register("checkCompatLayer") {
    group = "verification"
    description = "Checks that the version-agnostic compatibility API does not depend on Minecraft."

    inputs.files(compatApiDirs).withPropertyName("compatApi")
    outputs.upToDateWhen { false }

    doLast {
        val offenders = compatApiDirs.asSequence()
            .flatMap { dir -> dir.walkTopDown().filter { it.isFile && it.name.endsWith(".java") } }
            .filter { it.parentFile.name != "impl" }
            .filter { file -> file.readLines().any { line -> line.trimStart().startsWith("import net.minecraft") } }
            .map { it.path }
            .toList()

        if (offenders.isNotEmpty()) {
            throw GradleException(
                "The compatibility API must stay version-agnostic, but net.minecraft imports were found in:\n" +
                    offenders.joinToString("\n") { "  - $it" } +
                    "\nMove version-specific code into a ...compat.impl package (see COMPAT.md)."
            )
        }

        logger.lifecycle("Compatibility API is version-agnostic (${compatApiDirs.size} source roots checked).")
    }
}

tasks.named("check") {
    dependsOn(checkCompatLayer)
}

fun toMinecraftCompat(version: String): String {
    // Stable release
    val stable = Regex("""^(\d{2})\.([1-9]\d*)(?:\.(\d+))?$""")

    stable.matchEntire(version)?.let {
        val (year, drop, _) = it.destructured
        return "~$year.$drop"
    }

    // Prerelease
    val pre = Regex("""^(\d{2})\.([1-9]\d*)-pre[-.](\d+)$""")
    pre.matchEntire(version)?.let {
        return version.replace("-pre-", "-pre.")
    }

    // Release Candidate
    val rc = Regex("""^(\d{2})\.([1-9]\d*)-rc[-.](\d+)$""")
    rc.matchEntire(version)?.let {
        return version.replace("-rc-", "-rc.")
    }

    // fallback
    return version
}

tasks {
    processResources {
        val buildNumber = providers.gradleProperty("build_number").getOrElse("")
        val commit = providers.gradleProperty("commit").getOrElse("")

        val propertyMap = mapOf(
            "version" to project.version,
            "build_number" to buildNumber,
            "commit" to commit,
            "jdk_version" to libs.versions.jdk.get(),
            "minecraft_version" to toMinecraftCompat(mc("minecraft")),
            "loader_version" to libs.versions.fabric.loader.get(),
            "compat_impl" to mc("compat-impl"),
            "devbuild" to ""
        )

        inputs.properties(propertyMap)
        filesMatching(listOf("fabric.mod.json", "meteor-compat.properties")) {
            expand(propertyMap)
        }
    }

    // Compile launcher with Java 8 for backwards compatibility
    named<JavaCompile>("compileLauncherJava").configure {
        sourceCompatibility = JavaVersion.VERSION_1_8.toString()
        targetCompatibility = JavaVersion.VERSION_1_8.toString()
        options.compilerArgs.add("-Xlint:-options")
    }

    jar {
        inputs.property("archivesName", archivesBaseName)

        from("LICENSE") {
            rename { "${it}_$archivesBaseName" }
        }

        // Include launcher classes
        from(launcher.output)

        manifest {
            attributes["Main-Class"] = "meteordevelopment.meteorclient.Main"
        }
    }

    withType<JavaCompile>().configureEach {
        options.compilerArgs.addAll(
            listOf(
                "-Xlint:deprecation",
                "-Xlint:unchecked"
            )
        )
        // Keep diagnostic output bounded by default so old versions with many missing APIs
        // do not exhaust the compiler daemon. Override with -PjavacMaxErrs=N when auditing.
        val maxErrs = providers.gradleProperty("javacMaxErrs").getOrElse("200")
        options.compilerArgs.addAll(listOf("-Xmaxerrs", maxErrs))
    }

    named<Jar>("sourcesJar") {
        duplicatesStrategy = org.gradle.api.file.DuplicatesStrategy.EXCLUDE
    }

    javadoc {
        with(options as StandardJavadocDocletOptions) {
            addStringOption("Xdoclint:none", "-quiet")
            addStringOption("encoding", "UTF-8")
            addStringOption("charSet", "UTF-8")
        }
    }

}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            artifactId = "meteor-client"

            version = "${mc("minecraft")}-SNAPSHOT"
        }
    }

    repositories {
        maven("https://maven.meteordev.org/snapshots") {
            name = "meteor-maven"

            credentials {
                username = System.getenv("MAVEN_METEOR_ALIAS")
                password = System.getenv("MAVEN_METEOR_TOKEN")
            }

            authentication {
                create<BasicAuthentication>("basic")
            }
        }
    }
}
