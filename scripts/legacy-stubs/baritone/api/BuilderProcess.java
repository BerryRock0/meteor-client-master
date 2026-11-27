package baritone.api;

import baritone.api.utils.BetterBlockPos;

/** Compile-only stand-in for Baritone's builder process. */
public class BuilderProcess {
    public boolean isActive() {
        return false;
    }

    public void clearArea(BetterBlockPos start, BetterBlockPos end) {}
}
