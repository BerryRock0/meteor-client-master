/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.utils;

import meteordevelopment.meteorclient.addons.AddonManager;
import meteordevelopment.meteorclient.addons.MeteorAddon;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Runs the static methods annotated with {@link PreInit} / {@link PostInit} in the packages of
 * Meteor and of every installed addon, resolving the declared annotation dependencies first.
 *
 * <p>This used to be built on the reflections library; it now walks the code source (the mod jar
 * or, in a development environment, the classes directory) of each addon itself. Class files are
 * pre-filtered by a raw search for the annotation descriptor, so no class is loaded before it is
 * known to hold an init task, and the execution order matches the old implementation.</p>
 */
public class ReflectInit {
    private record Target(String pkg, Path location) {}

    private static final List<Target> targets = new ArrayList<>();

    private ReflectInit() {
    }

    public static void registerPackages() {
        targets.clear();

        for (MeteorAddon addon : AddonManager.ADDONS) {
            try {
                add(addon);
            } catch (AbstractMethodError e) {
                throw new RuntimeException("Addon \"%s\" is too old and cannot be ran.".formatted(addon.name), e);
            }
        }
    }

    private static void add(MeteorAddon addon) {
        String pkg = addon.getPackage();
        if (pkg == null || pkg.isBlank()) return;

        var source = addon.getClass().getProtectionDomain().getCodeSource();
        if (source == null || source.getLocation() == null) {
            throw new RuntimeException("Cannot locate the code of addon \"%s\" for its init annotations".formatted(addon.name));
        }

        try {
            targets.add(new Target(pkg, Path.of(source.getLocation().toURI())));
        } catch (URISyntaxException e) {
            throw new RuntimeException("Cannot locate the code of addon \"%s\" for its init annotations".formatted(addon.name), e);
        }
    }

    public static void init(Class<? extends Annotation> annotation) {
        for (Target target : targets) {
            Set<Method> initTasks = findTasks(target, annotation);
            if (initTasks.isEmpty()) continue;

            Map<Class<?>, List<Method>> byClass = initTasks.stream().collect(Collectors.groupingBy(Method::getDeclaringClass));
            Set<Method> left = new HashSet<>(initTasks);

            for (Method m; (m = left.stream().findAny().orElse(null)) != null; ) {
                reflectInit(m, annotation, left, byClass);
            }
        }
    }

    private static void reflectInit(Method task, Class<? extends Annotation> annotation, Set<Method> left, Map<Class<?>, List<Method>> byClass) {
        left.remove(task);

        for (Class<?> clazz : getDependencies(task, annotation)) {
            for (Method m : byClass.getOrDefault(clazz, Collections.emptyList())) {
                if (left.contains(m)) {
                    reflectInit(m, annotation, left, byClass);
                }
            }
        }

        try {
            task.invoke(null);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException("Error running @%s task '%s.%s'".formatted(annotation.getSimpleName(), task.getDeclaringClass().getSimpleName(), task.getName()), e);
        } catch (NullPointerException e) {
            throw new RuntimeException("Method \"%s\" using Init annotations from non-static context".formatted(task.getName()), e);
        }
    }

    private static <T extends Annotation> Class<?>[] getDependencies(Method task, Class<T> annotation) {
        T init = task.getAnnotation(annotation);

        return switch (init) {
            case PreInit pre -> pre.dependencies();
            case PostInit post -> post.dependencies();
            default -> new Class<?>[]{};
        };
    }

    /** Finds the annotated static methods of {@code annotation} in every class file of {@code target}'s package. */
    private static Set<Method> findTasks(Target target, Class<? extends Annotation> annotation) {
        Set<Method> tasks = new HashSet<>();
        String needle = "L" + annotation.getName().replace('.', '/') + ";";
        String prefix = target.pkg().replace('.', '/') + "/";

        try {
            if (Files.isDirectory(target.location())) {
                Path root = target.location().resolve(prefix);

                if (Files.isDirectory(root)) {
                    try (Stream<Path> files = Files.walk(root)) {
                        for (Path file : (Iterable<Path>) files.filter(ReflectInit::isClassFile)::iterator) {
                            String name = target.location().relativize(file).toString().replace('\\', '/');
                            collectTasks(needle, annotation, name, Files.readAllBytes(file), tasks);
                        }
                    }
                }
            } else {
                try (JarFile jar = new JarFile(target.location().toFile())) {
                    for (JarEntry entry : Collections.list(jar.entries())) {
                        String name = entry.getName();
                        if (entry.isDirectory() || !name.startsWith(prefix) || !isClassFile(name)) continue;

                        try (InputStream in = jar.getInputStream(entry)) {
                            collectTasks(needle, annotation, name, in.readAllBytes(), tasks);
                        }
                    }
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException("Error scanning the code of \"%s\" for @%s tasks".formatted(target.pkg(), annotation.getSimpleName()), e);
        }

        return tasks;
    }

    /** Loads the class of the class file {@code name} and collects its annotated methods, if it holds any. */
    private static void collectTasks(String needle, Class<? extends Annotation> annotation, String name, byte[] bytes, Set<Method> tasks) {
        // The annotation descriptor only appears in a class file that declares the annotation, so this
        // cheap check keeps us from loading every class of the mod just to look at its methods.
        if (!new String(bytes, StandardCharsets.ISO_8859_1).contains(needle)) return;

        String className = name.substring(0, name.length() - ".class".length()).replace('/', '.');

        Class<?> clazz;
        try {
            clazz = Class.forName(className, false, ReflectInit.class.getClassLoader());
        } catch (ClassNotFoundException | NoClassDefFoundError e) {
            throw new IllegalStateException("Cannot load class \"%s\" while running @%s tasks".formatted(className, annotation.getSimpleName()), e);
        }

        for (Method method : clazz.getDeclaredMethods()) {
            if (!method.isAnnotationPresent(annotation)) continue;

            method.setAccessible(true);
            tasks.add(method);
        }
    }

    private static boolean isClassFile(Path file) {
        return isClassFile(file.getFileName().toString());
    }

    private static boolean isClassFile(String name) {
        String fileName = name.substring(name.lastIndexOf('/') + 1);
        return fileName.endsWith(".class") && !fileName.equals("package-info.class") && !fileName.equals("module-info.class");
    }
}
