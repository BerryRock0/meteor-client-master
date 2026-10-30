/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.mc;

import net.minecraft.world.level.block.entity.BlockEntityType;

/**
 * Version-neutral access to the block entity type constants: 26.1 keeps them on
 * {@code BlockEntityType}, 26.2 moved them to {@code BlockEntityTypes}. Shared code uses
 * this facade instead of either.
 */
public final class BlockEntityTypes {
    private BlockEntityTypes() {}
    public static final BlockEntityType<?> BARREL = net.minecraft.world.level.block.entity.BlockEntityTypes.BARREL;
    public static final BlockEntityType<?> BLAST_FURNACE = net.minecraft.world.level.block.entity.BlockEntityTypes.BLAST_FURNACE;
    public static final BlockEntityType<?> BREWING_STAND = net.minecraft.world.level.block.entity.BlockEntityTypes.BREWING_STAND;
    public static final BlockEntityType<?> CAMPFIRE = net.minecraft.world.level.block.entity.BlockEntityTypes.CAMPFIRE;
    public static final BlockEntityType<?> CHEST = net.minecraft.world.level.block.entity.BlockEntityTypes.CHEST;
    public static final BlockEntityType<?> CHISELED_BOOKSHELF = net.minecraft.world.level.block.entity.BlockEntityTypes.CHISELED_BOOKSHELF;
    public static final BlockEntityType<?> CRAFTER = net.minecraft.world.level.block.entity.BlockEntityTypes.CRAFTER;
    public static final BlockEntityType<?> DECORATED_POT = net.minecraft.world.level.block.entity.BlockEntityTypes.DECORATED_POT;
    public static final BlockEntityType<?> DISPENSER = net.minecraft.world.level.block.entity.BlockEntityTypes.DISPENSER;
    public static final BlockEntityType<?> DROPPER = net.minecraft.world.level.block.entity.BlockEntityTypes.DROPPER;
    public static final BlockEntityType<?> ENDER_CHEST = net.minecraft.world.level.block.entity.BlockEntityTypes.ENDER_CHEST;
    public static final BlockEntityType<?> FURNACE = net.minecraft.world.level.block.entity.BlockEntityTypes.FURNACE;
    public static final BlockEntityType<?> HOPPER = net.minecraft.world.level.block.entity.BlockEntityTypes.HOPPER;
    public static final BlockEntityType<?> SHULKER_BOX = net.minecraft.world.level.block.entity.BlockEntityTypes.SHULKER_BOX;
    public static final BlockEntityType<?> SMOKER = net.minecraft.world.level.block.entity.BlockEntityTypes.SMOKER;
    public static final BlockEntityType<?> TRAPPED_CHEST = net.minecraft.world.level.block.entity.BlockEntityTypes.TRAPPED_CHEST;
}
