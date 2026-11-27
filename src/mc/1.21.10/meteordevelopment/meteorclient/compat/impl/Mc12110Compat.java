/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.compat.impl;

import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.compat.ICompat;
import meteordevelopment.meteorclient.compat.McVersion;
import meteordevelopment.meteorclient.compat.ScreenKind;
import org.joml.Matrix4f;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Compatibility implementation for Minecraft 1.21.10.
 *
 * <p>Every Minecraft version exposes the same concepts under different names. Instead of
 * referencing version-specific classes, this implementation discovers them at runtime. A concept
 * that does not exist in a version returns a neutral value.</p>
 */
public final class Mc12110Compat implements ICompat {
    @Override
    public McVersion version() { return McVersion.parse(versionName()); }

    @Override
    public String versionName() {
        try {
            Class<?> c = Class.forName("net.minecraft.SharedConstants");
            Method m = c.getMethod("getCurrentVersion");
            Object v = m.invoke(null);
            String name = invokeString(v, "name");
            if (!name.isEmpty()) return name;
        } catch (Throwable ignored) { }
        return "unknown";
    }

    @Override
    public int protocolVersion() {
        try {
            Class<?> c = Class.forName("net.minecraft.SharedConstants");
            for (String name : new String[] { "getProtocolVersion", "getCurrentVersion" }) {
                try {
                    Object r = c.getMethod(name).invoke(null);
                    if (r instanceof Number n) return n.intValue();
                    String pv = invokeString(r, "getProtocolVersion");
                    if (!pv.isEmpty()) return Integer.parseInt(pv);
                } catch (Throwable ignored) { }
            }
        } catch (Throwable ignored) { }
        return 0;
    }

    @Override public int getWindowWidth() { return objectInt(field(MeteorClient.mc, "getWindow"), "getWidth"); }
    @Override public int getWindowHeight() { return objectInt(field(MeteorClient.mc, "getWindow"), "getHeight"); }
    @Override public double getGuiScale() { return objectDouble(field(MeteorClient.mc, "getWindow"), "getGuiScale"); }

    @Override
    public String getWorldName() {
        try {
            Object level = field(MeteorClient.mc, "level");
            Object conn = field(MeteorClient.mc, "connection");
            if (conn != null) {
                Object info = call(conn, "getServerData");
                String ip = invokeString(info, "ip");
                if (!ip.isEmpty()) return ip;
            }
            if (level != null) {
                Object server = call(MeteorClient.mc, "getSingleplayerServer");
                if (server != null) {
                    Object storage = firstField(server, "storageSource", "worldStorage") ;
                    if (storage != null) {
                        Object dimension = call(level, "dimension");
                        Object path = callAny(storage, "getDimensionPath", dimension);
                        if (path instanceof File folder) return folder.getName();
                    }
                }
            }
        } catch (Throwable ignored) { }
        return "";
    }

    @Override
    public String getWorldTime() {
        Object level = field(MeteorClient.mc, "level");
        if (level == null) return "00:00";
        long ticks = objectLong(level, "getGameTime") + 6000;
        ticks %= 24000;
        return String.format("%02d:%02d", ticks / 1000, (ticks % 1000) * 60 / 1000);
    }

    @Override public int getRenderDistance() { return objectInt(field(MeteorClient.mc, "options"), "renderDistance"); }
    @Override public boolean isInGame() { return field(MeteorClient.mc, "player") != null && field(MeteorClient.mc, "level") != null; }
    @Override public boolean isHudHidden() { return fieldBoolean(field(MeteorClient.mc, "options"), "hideGui"); }
    @Override public void setHudHidden(boolean hidden) { setFieldBoolean(field(MeteorClient.mc, "options"), "hideGui", hidden); }
    @Override public boolean isLoading() { return false; }

    @Override
    public Matrix4f applyProjection(boolean scaled) {
        Object window = field(MeteorClient.mc, "getWindow");
        float width = objectInt(window, "getWidth");
        float height = objectInt(window, "getHeight");
        if (scaled) {
            double gs = objectDouble(window, "getGuiScale");
            if (gs > 0) { width /= gs; height /= gs; }
        }
        return new Matrix4f().ortho(0, width, height, 0, -1000, 1000);
    }

    @Override
    public ScreenKind currentScreenKind() {
        Object screen = field(MeteorClient.mc, "screen");
        if (screen == null) return ScreenKind.NONE;
        String n = screen.getClass().getName();
        if (n.contains("TitleScreen")) return ScreenKind.TITLE;
        if (n.contains("JoinMultiplayerScreen") || n.contains("MultiplayerScreen")) return ScreenKind.MULTIPLAYER;
        if (n.contains("SelectWorldScreen") || n.contains("WorldSelection")) return ScreenKind.WORLD_SELECTION;
        return ScreenKind.OTHER;
    }

    @Override
    public boolean closeCurrentScreen() {
        Object screen = field(MeteorClient.mc, "screen");
        if (screen == null) return false;
        try { screen.getClass().getMethod("onClose").invoke(screen); return true; }
        catch (Throwable t) { return false; }
    }

    // --- reflection helpers -------------------------------------------------

    private static Object field(Object target, String name) {
        if (target == null) return null;
        try {
            Class<?> c = target.getClass();
            while (c != null) {
                try { Field f = c.getDeclaredField(name); f.setAccessible(true); return f.get(target); }
                catch (NoSuchFieldException e) { c = c.getSuperclass(); }
            }
        } catch (Throwable ignored) { }
        return null;
    }

    private static Object firstField(Object target, String... names) {
        for (String n : names) { Object v = field(target, n); if (v != null) return v; }
        return null;
    }

    private static Object call(Object target, String name) {
        if (target == null) return null;
        try {
            Class<?> c = target.getClass();
            while (c != null) {
                try { Method m = c.getDeclaredMethod(name); m.setAccessible(true); return m.invoke(target); }
                catch (NoSuchMethodException e) { c = c.getSuperclass(); }
            }
        } catch (Throwable ignored) { }
        return null;
    }

    private static Object callAny(Object target, String name, Object arg) {
        if (target == null) return null;
        try {
            for (Method m : target.getClass().getMethods()) {
                if (m.getName().equals(name) && m.getParameterCount() == 1) {
                    Object r = m.invoke(target, arg);
                    if (r != null) return r;
                }
            }
        } catch (Throwable ignored) { }
        return null;
    }

    private static String invokeString(Object target, String name) {
        Object v = call(target, name);
        return v == null ? "" : v.toString();
    }

    private static int objectInt(Object target, String name) {
        Object v = call(target, name);
        return v instanceof Number n ? n.intValue() : 0;
    }

    private static long objectLong(Object target, String name) {
        Object v = call(target, name);
        return v instanceof Number n ? n.longValue() : 0;
    }

    private static double objectDouble(Object target, String name) {
        Object v = call(target, name);
        return v instanceof Number n ? n.doubleValue() : 0;
    }

    private static boolean fieldBoolean(Object target, String name) {
        Object v = field(target, name);
        return v instanceof Boolean b ? b : false;
    }

    private static void setFieldBoolean(Object target, String name, boolean value) {
        if (target == null) return;
        try {
            Class<?> c = target.getClass();
            while (c != null) {
                try { Field f = c.getDeclaredField(name); f.setAccessible(true); f.set(target, value); return; }
                catch (NoSuchFieldException e) { c = c.getSuperclass(); }
            }
        } catch (Throwable ignored) { }
    }
}
