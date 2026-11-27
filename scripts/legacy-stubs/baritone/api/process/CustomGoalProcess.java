package baritone.api.process;

import baritone.api.pathing.goals.Goal;

/** Compile-only stand-in for Baritone's custom goal process. */
public class CustomGoalProcess implements ICustomGoalProcess {
    private Goal goal;

    @Override
    public void setGoalAndPath(Goal goal) {
        this.goal = goal;
    }

    @Override
    public Goal getGoal() {
        return goal;
    }

    @Override
    public boolean isActive() {
        return goal != null;
    }

    @Override
    public PathingCommand onTick(boolean safe, boolean dangerous) {
        return new PathingCommand(this, PathingCommandType.REQUEST_PAUSE);
    }

    @Override
    public boolean isTemporary() {
        return false;
    }

    @Override
    public void onLostControl() {
        goal = null;
    }

    @Override
    public double priority() {
        return 0;
    }

    @Override
    public String displayName0() {
        return "Custom Goal";
    }
}
