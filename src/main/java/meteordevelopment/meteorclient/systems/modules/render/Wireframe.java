/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.systems.modules.render;

import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.List;

public class Wireframe extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<List<Block>> blocks = sgGeneral.add(new BlockListSetting.Builder()
        .name("blocks")
        .description("Blocks to render as wireframes.")
        .build());

    private final Setting<SettingColor> color = sgGeneral.add(new ColorSetting.Builder()
        .name("color")
        .description("Color of the block wireframes.")
        .defaultValue(new SettingColor(0, 255, 200))
        .build());

    public Wireframe() {
        super(Categories.Render, "wireframe", "Renders blocks as wireframes.");
    }

    @Override
    public void onActivate() {
    }

    @Override
    public void onDeactivate() {
    }

    @EventHandler
    private void onRender(Render3DEvent event) {
        if (!isActive()) return;

        Color lineColor = color.get();

        for (ChunkAccess chunk : Utils.chunks()) {
            if (chunk == null) continue;

            BlockPos.MutableBlockPos blockPos = new BlockPos.MutableBlockPos();

            for (int x = chunk.getPos().getMinBlockX(); x <= chunk.getPos().getMaxBlockX(); x++) {
                for (int z = chunk.getPos().getMinBlockZ(); z <= chunk.getPos().getMaxBlockZ(); z++) {
                    int height = chunk.getOrCreateHeightmapUnprimed(Heightmap.Types.WORLD_SURFACE).getFirstAvailable(x - chunk.getPos().getMinBlockX(), z - chunk.getPos().getMinBlockZ());

                    for (int y = 0; y < height; y++) {
                        blockPos.set(x, y, z);
                        BlockState bs = chunk.getBlockState(blockPos);
                        if (blocks.get().contains(bs.getBlock())) {
                            event.renderer.blockLines(x, y, z, lineColor, 0);
                        }
                    }
                }
            }
        }
    }
}
