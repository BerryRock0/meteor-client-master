package baritone.api.pathing.goals;

/** Compile-only stand-in for Baritone's GoalXZ. */
public class GoalXZ implements Goal {
    private final int x, y, z;

    public GoalXZ(int x, int z) {
        this(x, 0, z);
    }

    public GoalXZ(net.minecraft.util.math.BlockPos pos) {
        this(pos.getX(), pos.getY(), pos.getZ());
    }

    public GoalXZ(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public double heuristic(int x, int y, int z) {
        return Math.hypot(x - this.x, z - this.z);
    }

    @Override
    public String toString() {
        return "GoalXZ{x=" + x + ",z=" + z + "}";
    }
}
