package baritone.command.defaults;

import baritone.api.BaritoneAPI;
import baritone.api.pathing.goals.GoalBlock;
import net.minecraft.util.math.BlockPos;

/** Compile-only stand-in for Baritone's come command. */
public class ComeCommand {
    public void execute(BlockPos pos) {
        BaritoneAPI.getProvider().getPrimaryBaritone().getCustomGoalProcess().setGoalAndPath(new GoalBlock(pos));
    }
}
