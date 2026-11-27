package baritone.api;

/** Compile-only stand-in for Baritone's static entry point. */
public final class BaritoneAPI {
    private static final BaritoneProvider PROVIDER = new BaritoneProvider();
    private static final Settings SETTINGS = new Settings();

    public static BaritoneProvider getProvider() {
        return PROVIDER;
    }

    public static Settings getSettings() {
        return SETTINGS;
    }
}
