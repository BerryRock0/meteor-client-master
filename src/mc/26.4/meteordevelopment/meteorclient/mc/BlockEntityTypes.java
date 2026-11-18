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
public final class BlockEntityTypes
{
    private BlockEntityTypes()
    {}

    public static final BlockEntityType<?> BARREL = BlockEntityTypes.BARREL;
    public static final BlockEntityType<?> BLAST_FURNACE = BlockEntityTypes.BLAST_FURNACE;
    public static final BlockEntityType<?> BREWING_STAND = BlockEntityTypes.BREWING_STAND;
    public static final BlockEntityType<?> CAMPFIRE = BlockEntityTypes.CAMPFIRE;
    public static final BlockEntityType<?> CHEST = BlockEntityTypes.CHEST;
    public static final BlockEntityType<?> CHISELED_BOOKSHELF = BlockEntityTypes.CHISELED_BOOKSHELF;
    public static final BlockEntityType<?> CRAFTER = BlockEntityTypes.CRAFTER;
    public static final BlockEntityType<?> DECORATED_POT = BlockEntityTypes.DECORATED_POT;
    public static final BlockEntityType<?> DISPENSER = BlockEntityTypes.DISPENSER;
    public static final BlockEntityType<?> DROPPER = BlockEntityTypes.DROPPER;
    public static final BlockEntityType<?> ENDER_CHEST = BlockEntityTypes.ENDER_CHEST;
    public static final BlockEntityType<?> FURNACE = BlockEntityTypes.FURNACE;
    public static final BlockEntityType<?> HOPPER = BlockEntityTypes.HOPPER;
    public static final BlockEntityType<?> SHULKER_BOX = BlockEntityTypes.SHULKER_BOX;
    public static final BlockEntityType<?> SMOKER = BlockEntityTypes.SMOKER;
    public static final BlockEntityType<?> TRAPPED_CHEST = BlockEntityTypes.TRAPPED_CHEST;
}
