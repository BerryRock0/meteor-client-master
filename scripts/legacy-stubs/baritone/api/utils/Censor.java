package baritone.api.utils;

/** Compile-only stand-in for Baritone's censor helper. */
public final class Censor {
    public static String maybeCensor(Object value) {
        return String.valueOf(value);
    }
}
