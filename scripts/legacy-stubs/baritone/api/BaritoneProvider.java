package baritone.api;

/** Compile-only stand-in for Baritone's provider. */
public class BaritoneProvider {
    private final IBaritone primary = new IBaritone();
    private final WorldScanner worldScanner = new WorldScanner();

    public IBaritone getPrimaryBaritone() {
        return primary;
    }

    public WorldScanner getWorldScanner() {
        return worldScanner;
    }
}
