/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.mc;

import net.minecraft.world.level.block.entity.BlockEntityType;

/**
 * Version-neutral access to block entity type constants.
 *
 * <p>Constants are resolved reflectively at runtime. A constant that does not exist in the
 * running version resolves to {@code null} instead of breaking compilation.</p>
 */
public final class BlockEntityTypes {
    private BlockEntityTypes() {}

    public static final BlockEntityType<?> BARREL = resolve("BARREL");
    public static final BlockEntityType<?> BLAST_FURNACE = resolve("BLAST_FURNACE");
    public static final BlockEntityType<?> BREWING_STAND = resolve("BREWING_STAND");
    public static final BlockEntityType<?> CAMPFIRE = resolve("CAMPFIRE");
    public static final BlockEntityType<?> CHEST = resolve("CHEST");
    public static final BlockEntityType<?> CHISELED_BOOKSHELF = resolve("CHISELED_BOOKSHELF");
    public static final BlockEntityType<?> CRAFTER = resolve("CRAFTER");
    public static final BlockEntityType<?> DECORATED_POT = resolve("DECORATED_POT");
    public static final BlockEntityType<?> DISPENSER = resolve("DISPENSER");
    public static final BlockEntityType<?> DROPPER = resolve("DROPPER");
    public static final BlockEntityType<?> ENDER_CHEST = resolve("ENDER_CHEST");
    public static final BlockEntityType<?> FURNACE = resolve("FURNACE");
    public static final BlockEntityType<?> HOPPER = resolve("HOPPER");
    public static final BlockEntityType<?> SHULKER_BOX = resolve("SHULKER_BOX");
    public static final BlockEntityType<?> SMOKER = resolve("SMOKER");
    public static final BlockEntityType<?> TRAPPED_CHEST = resolve("TRAPPED_CHEST");

    private static BlockEntityType<?> resolve(String name) {
        try {
            Class<?> cls;
            try {
                cls = Class.forName("net.minecraft.world.level.block.entity.BlockEntityTypes");
            } catch (ClassNotFoundException e) {
                cls = BlockEntityType.class;
            }
            Object value = cls.getField(name).get(null);
            return value instanceof BlockEntityType<?> type ? type : null;
        } catch (Throwable t) {
            return null;
        }
    }
}
