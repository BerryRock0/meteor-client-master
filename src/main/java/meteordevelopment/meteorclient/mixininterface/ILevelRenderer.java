/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.mixininterface;

import com.mojang.blaze3d.pipeline.RenderTarget;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import net.minecraft.server.level.BlockDestructionProgress;

import java.util.SortedSet;

public interface ILevelRenderer {
    void meteor$pushEntityOutlineFramebuffer(RenderTarget framebuffer);

    void meteor$popEntityOutlineFramebuffer();

    /** 26.1 keeps the block destruction progress on the level renderer, 26.2 moved it onto the level. */
    Long2ObjectMap<SortedSet<BlockDestructionProgress>> meteor$destructionProgress();
}
