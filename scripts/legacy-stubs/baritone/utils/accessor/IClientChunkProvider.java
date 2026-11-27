package baritone.utils.accessor;

import net.minecraft.client.world.ClientChunkManager;

/** Compile-only stand-in for Baritone's client chunk provider accessor. */
public interface IClientChunkProvider {
    ClientChunkManager createThreadSafeCopy();

    IChunkArray extractReferenceArray();
}
