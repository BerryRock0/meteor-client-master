package baritone.api.pathing.goals;

/** Compile-only stand-in for Baritone's GoalGetToBlock. */
public class GoalGetToBlock implements Goal {
    private final int x, y, z;

    public GoalGetToBlock(int x, int z) {
        this(x, 0, z);
    }

    public GoalGetToBlock(net.minecraft.util.math.BlockPos pos) {
        this(pos.getX(), pos.getY(), pos.getZ());
    }

    public GoalGetToBlock(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public double heuristic(int x, int y, int z) {
        return Math.hypot(this.x - x, Math.hypot(this.y - y, this.z - z));
    }

    @Override
    public String toString() {
        return "GoalGetToBlock{x=" + x + ",z=" + z + "}";
    }
}
