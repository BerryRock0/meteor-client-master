package baritone.api.process;

import baritone.api.pathing.goals.Goal;

/** Compile-only stand-in for Baritone's custom goal process interface. */
public interface ICustomGoalProcess extends IBaritoneProcess {
    void setGoalAndPath(Goal goal);

    Goal getGoal();
}
