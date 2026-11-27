package baritone.api;

import baritone.api.pathing.goals.Goal;

import java.util.Optional;

/** Compile-only stand-in for Baritone's pathing behavior. */
public class PathingBehavior {
    public boolean isPathing() {
        return false;
    }

    public void cancelEverything() {}

    public Optional<Double> estimatedTicksToGoal() {
        return Optional.empty();
    }

    public Goal getGoal() {
        return null;
    }
}
