pluginManagement {
    repositories {
        maven {
            name = "Fabric"
            url = uri("https://maven.fabricmc.net/")
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "meteor-client"

// Loom's remap plugin supports both obfuscated (1.x) and unobfuscated (26.x) jars,
// but the no-remap mode has to be enabled before the plugin is applied.
val mcVersionForSettings = gradle.startParameter.projectProperties["minecraftVersion"]
    ?: providers.gradleProperty("minecraftVersion").orNull
    ?: "26.2"
val noRemap = !mcVersionForSettings.startsWith("1.")
gradle.projectsLoaded {
    rootProject.extra["fabric.loom.disableObfuscation"] = noRemap.toString()
}
