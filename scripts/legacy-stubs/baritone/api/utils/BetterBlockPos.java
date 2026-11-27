package baritone.api.utils;

import net.minecraft.util.math.BlockPos;

/** Compile-only stand-in for Baritone's block position. */
public class BetterBlockPos extends BlockPos {
    public static BetterBlockPos from(BlockPos pos) {
        return new BetterBlockPos(pos.getX(), pos.getY(), pos.getZ());
    }

    public BetterBlockPos(int x, int y, int z) {
        super(x, y, z);
    }
}
