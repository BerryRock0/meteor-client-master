package baritone.api.process;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;

/** Compile-only stand-in for Baritone's mine process interface. */
public interface IMineProcess extends IBaritoneProcess {
    void mine(BlockPos pos);

    void mine(BlockPos[] positions);

    void mine(Block block);

    void mine(Block[] blocks);
}
