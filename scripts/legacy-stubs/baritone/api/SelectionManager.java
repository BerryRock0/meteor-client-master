package baritone.api;

import baritone.api.utils.BetterBlockPos;

/** Compile-only stand-in for Baritone's selection manager. */
public class SelectionManager {
    public void addSelection(BetterBlockPos start, BetterBlockPos end) {}

    public Object getLastSelection() {
        return null;
    }

    public void removeSelection(Object selection) {}
}
