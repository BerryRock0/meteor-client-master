package baritone.api.utils;

import baritone.api.Settings;

/** Compile-only stand-in for Baritone's settings persistence helper. */
public final class SettingsUtil {
    public static void save(Settings settings) {}

    public static String maybeCensor(Object value) {
        return String.valueOf(value);
    }
}
