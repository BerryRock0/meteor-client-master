package baritone.api;

import baritone.api.process.CustomGoalProcess;
import baritone.api.process.MineProcess;

/** Compile-only stand-in for Baritone's primary API. */
public class IBaritone {
    public CommandManager getCommandManager() {
        return new CommandManager();
    }

    public PathingBehavior getPathingBehavior() {
        return new PathingBehavior();
    }

    public PathingControlManager getPathingControlManager() {
        return new PathingControlManager();
    }

    public CustomGoalProcess getCustomGoalProcess() {
        return new CustomGoalProcess();
    }

    public MineProcess getMineProcess() {
        return new MineProcess();
    }

    public FollowProcess getFollowProcess() {
        return new FollowProcess();
    }

    public SelectionManager getSelectionManager() {
        return new SelectionManager();
    }

    public BuilderProcess getBuilderProcess() {
        return new BuilderProcess();
    }

    public LookBehavior getLookBehavior() {
        return new LookBehavior();
    }

    public InputOverrideHandler getInputOverrideHandler() {
        return new InputOverrideHandler();
    }

    public Object getPlayerContext() {
        return new Object();
    }

    public Settings getSettings() {
        return BaritoneAPI.getSettings();
    }
}
