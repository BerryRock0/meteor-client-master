package baritone.api.pathing.goals;

/** Compile-only stand-in for Baritone's GoalBlock. */
public class GoalBlock implements Goal {
    private final int x, y, z;

    public GoalBlock(int x, int z) {
        this(x, 0, z);
    }

    public GoalBlock(net.minecraft.util.math.BlockPos pos) {
        this(pos.getX(), pos.getY(), pos.getZ());
    }

    public GoalBlock(int x, int y, int z) {
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
        return "GoalBlock{x=" + x + ",z=" + z + "}";
    }
}
