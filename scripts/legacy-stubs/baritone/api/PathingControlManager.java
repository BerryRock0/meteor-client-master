package baritone.api;

import baritone.api.process.IBaritoneProcess;

import java.util.Optional;

/** Compile-only stand-in for Baritone's pathing control manager. */
public class PathingControlManager {
    public Optional<IBaritoneProcess> mostRecentInControl() {
        return Optional.empty();
    }

    public void registerProcess(IBaritoneProcess process) {}
}
