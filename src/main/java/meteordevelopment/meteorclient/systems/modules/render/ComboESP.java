/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.systems.modules.render;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.entity.EntityUtils;
import meteordevelopment.meteorclient.utils.render.WireframeEntityRenderer;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.minecraft.util.Mth;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ComboESP extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final SettingGroup sgBlocks = settings.createGroup("Blocks");
    private final SettingGroup sgEntities = settings.createGroup("Entities");
    private final SettingGroup sgColors = settings.createGroup("Colors");

    // General

    public final Setting<Mode> mode = sgGeneral.add(new EnumSetting.Builder<Mode>()
        .name("mode")
        .description("Rendering mode.")
        .defaultValue(Mode.Box)
        .build());

    public final Setting<Double> fillOpacity = sgGeneral.add(new DoubleSetting.Builder()
        .name("fill-opacity")
        .description("The opacity of the shape fill.")
        .visible(() -> mode.get() == Mode.Box)
        .defaultValue(0.3)
        .range(0, 1)
        .sliderMax(1)
        .build());

    // Blocks

    public final Setting<List<String>> blocks = sgBlocks.add(new StringListSetting.Builder()
        .name("blocks")
        .description("IDs of blocks to render.")
        .defaultValue("minecraft:stone")
        .build());

    // Entities

    public final Setting<List<String>> entities = sgEntities.add(new StringListSetting.Builder()
        .name("entities")
        .description("IDs of entities to render.")
        .defaultValue("minecraft:zombie")
        .build());

    // Colors

    public final Setting<SettingColor> color = sgColors.add(new ColorSetting.Builder()
        .name("color")
        .description("Color of the rendered blocks and entities.")
        .defaultValue(new SettingColor(0, 255, 200))
        .build());

    private final Set<Block> selectedBlocks = new HashSet<>();
    private final Set<EntityType<?>> selectedEntities = new ObjectOpenHashSet<>();

    public ComboESP() {
        super(Categories.Render, "combo-esp", "Renders selected blocks and entities.");
    }

    @Override
    public void onActivate() {
        updateSelected();
    }

    @Override
    public void onDeactivate() {
        selectedBlocks.clear();
        selectedEntities.clear();
    }

    private void updateSelected() {
        selectedBlocks.clear();

        for (String blockId : blocks.get()) {
            Block block = Setting.parseId(BuiltInRegistries.BLOCK, blockId);
            if (block != null) selectedBlocks.add(block);
        }

        selectedEntities.clear();

        for (String entityId : entities.get()) {
            EntityType<?> entityType = Setting.parseId(BuiltInRegistries.ENTITY_TYPE, entityId);
            if (entityType != null) selectedEntities.add(entityType);
        }
    }

    @EventHandler
    private void onRender3D(Render3DEvent event) {
        if (!isActive()) return;

        Color color = this.color.get();
        Color lineColor = color;
        Color sideColor = color;
        ShapeMode shapeMode = mode.get() == Mode.Wireframe ? ShapeMode.Lines : ShapeMode.Both;
        if (mode.get() == Mode.Box) {
            sideColor = color.a((int) (color.a * fillOpacity.get()));
        }

        // Entities

        if (!selectedEntities.isEmpty()) {
            for (Entity entity : mc.level.entitiesForRendering()) {
                if (entity == mc.player) continue;
                if (!EntityUtils.isInRenderDistance(entity)) continue;
                if (!selectedEntities.contains(entity.getType())) continue;

                if (mode.get() == Mode.Wireframe) {
                    WireframeEntityRenderer.render(event, entity, 1, sideColor, lineColor, ShapeMode.Lines);
                    continue;
                }

                double x = Mth.lerp(event.tickDelta, entity.xOld, entity.getX()) - entity.getX();
                double y = Mth.lerp(event.tickDelta, entity.yOld, entity.getY()) - entity.getY();
                double z = Mth.lerp(event.tickDelta, entity.zOld, entity.getZ()) - entity.getZ();
                AABB box = entity.getBoundingBox();
                event.renderer.box(x + box.minX, y + box.minY, z + box.minZ, x + box.maxX, y + box.maxY, z + box.maxZ, sideColor, lineColor, ShapeMode.Both, 0);
            }
        }

        // Blocks

        for (ChunkAccess chunk : Utils.chunks()) {
            if (chunk == null) continue;

            BlockPos.MutableBlockPos blockPos = new BlockPos.MutableBlockPos();

            for (int x = chunk.getPos().getMinBlockX(); x <= chunk.getPos().getMaxBlockX(); x++) {
                for (int z = chunk.getPos().getMinBlockZ(); z <= chunk.getPos().getMaxBlockZ(); z++) {
                    int height = chunk.getOrCreateHeightmapUnprimed(Heightmap.Types.WORLD_SURFACE).getFirstAvailable(x - chunk.getPos().getMinBlockX(), z - chunk.getPos().getMinBlockZ());

                    for (int y = 0; y < height; y++) {
                        blockPos.set(x, y, z);
                        BlockState blockState = chunk.getBlockState(blockPos);
                        if (selectedBlocks.contains(blockState.getBlock())) {
                            event.renderer.box(blockPos, sideColor, lineColor, shapeMode, 0);
                        }
                    }
                }
            }
        }
    }

    public enum Mode {
        Box,
        Wireframe;

        @Override
        public String toString() {
            return this == Box ? "Box" : "Wireframe";
        }
    }
}
