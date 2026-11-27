package baritone.api;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

/** Compile-only stand-in for Baritone's world scanner. */
public class WorldScanner {
    public List<BlockPos> scanChunkRadius(Object context, List<Block> blocks, int chunkRadius, int maxCount, int yLevel) {
        return new ArrayList<>();
    }
}
