package baritone.api.pathing.goals;

import net.minecraft.util.math.BlockPos;

/** Compile-only stand-in for Baritone's goal interface. */
public interface Goal {
    double heuristic(int x, int y, int z);

    default boolean isInGoal(BlockPos pos) {
        return false;
    }

    default double heuristic(BlockPos pos) {
        return heuristic(pos.getX(), pos.getY(), pos.getZ());
    }
}
