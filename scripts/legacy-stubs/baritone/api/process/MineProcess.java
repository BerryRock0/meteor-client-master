package baritone.api.process;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;

/** Compile-only stand-in for Baritone's mine process. */
public class MineProcess implements IMineProcess {
    @Override
    public void mine(BlockPos pos) {}

    @Override
    public void mine(BlockPos[] positions) {}

    @Override
    public void mine(Block block) {}

    @Override
    public void mine(Block[] blocks) {}

    @Override
    public boolean isActive() {
        return false;
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
    public void onLostControl() {}

    @Override
    public double priority() {
        return 0;
    }

    @Override
    public String displayName0() {
        return "Mining";
    }
}
