/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.mc;

import net.minecraft.world.entity.EntityType;

/**
 * Version-neutral access to entity type constants.
 *
 * <p>Constants are resolved reflectively at runtime. A constant that does not exist in the
 * running version resolves to {@code null} instead of breaking compilation. Shared code
 * programs against this facade and never references version-specific fields directly.</p>
 */
public final class EntityTypes {
    private EntityTypes() {}

    public static final EntityType<?> ACACIA_BOAT = resolve("ACACIA_BOAT");
    public static final EntityType<?> ACACIA_CHEST_BOAT = resolve("ACACIA_CHEST_BOAT");
    public static final EntityType<?> AREA_EFFECT_CLOUD = resolve("AREA_EFFECT_CLOUD");
    public static final EntityType<?> ARMOR_STAND = resolve("ARMOR_STAND");
    public static final EntityType<?> ARROW = resolve("ARROW");
    public static final EntityType<?> BAMBOO_CHEST_RAFT = resolve("BAMBOO_CHEST_RAFT");
    public static final EntityType<?> BAMBOO_RAFT = resolve("BAMBOO_RAFT");
    public static final EntityType<?> BEE = resolve("BEE");
    public static final EntityType<?> BIRCH_BOAT = resolve("BIRCH_BOAT");
    public static final EntityType<?> BIRCH_CHEST_BOAT = resolve("BIRCH_CHEST_BOAT");
    public static final EntityType<?> CAMEL = resolve("CAMEL");
    public static final EntityType<?> CAMEL_HUSK = resolve("CAMEL_HUSK");
    public static final EntityType<?> CAT = resolve("CAT");
    public static final EntityType<?> CHERRY_BOAT = resolve("CHERRY_BOAT");
    public static final EntityType<?> CHERRY_CHEST_BOAT = resolve("CHERRY_CHEST_BOAT");
    public static final EntityType<?> CHICKEN = resolve("CHICKEN");
    public static final EntityType<?> COW = resolve("COW");
    public static final EntityType<?> DARK_OAK_BOAT = resolve("DARK_OAK_BOAT");
    public static final EntityType<?> DARK_OAK_CHEST_BOAT = resolve("DARK_OAK_CHEST_BOAT");
    public static final EntityType<?> DONKEY = resolve("DONKEY");
    public static final EntityType<?> EGG = resolve("EGG");
    public static final EntityType<?> ENDER_PEARL = resolve("ENDER_PEARL");
    public static final EntityType<?> END_CRYSTAL = resolve("END_CRYSTAL");
    public static final EntityType<?> EXPERIENCE_BOTTLE = resolve("EXPERIENCE_BOTTLE");
    public static final EntityType<?> EXPERIENCE_ORB = resolve("EXPERIENCE_ORB");
    public static final EntityType<?> EYE_OF_ENDER = resolve("EYE_OF_ENDER");
    public static final EntityType<?> FALLING_BLOCK = resolve("FALLING_BLOCK");
    public static final EntityType<?> FIREWORK_ROCKET = resolve("FIREWORK_ROCKET");
    public static final EntityType<?> FISHING_BOBBER = resolve("FISHING_BOBBER");
    public static final EntityType<?> FOX = resolve("FOX");
    public static final EntityType<?> GLOW_ITEM_FRAME = resolve("GLOW_ITEM_FRAME");
    public static final EntityType<?> HAPPY_GHAST = resolve("HAPPY_GHAST");
    public static final EntityType<?> HOGLIN = resolve("HOGLIN");
    public static final EntityType<?> HORSE = resolve("HORSE");
    public static final EntityType<?> ITEM = resolve("ITEM");
    public static final EntityType<?> ITEM_FRAME = resolve("ITEM_FRAME");
    public static final EntityType<?> JUNGLE_BOAT = resolve("JUNGLE_BOAT");
    public static final EntityType<?> JUNGLE_CHEST_BOAT = resolve("JUNGLE_CHEST_BOAT");
    public static final EntityType<?> LIGHTNING_BOLT = resolve("LIGHTNING_BOLT");
    public static final EntityType<?> LINGERING_POTION = resolve("LINGERING_POTION");
    public static final EntityType<?> LLAMA = resolve("LLAMA");
    public static final EntityType<?> LLAMA_SPIT = resolve("LLAMA_SPIT");
    public static final EntityType<?> MANGROVE_BOAT = resolve("MANGROVE_BOAT");
    public static final EntityType<?> MANGROVE_CHEST_BOAT = resolve("MANGROVE_CHEST_BOAT");
    public static final EntityType<?> MINECART = resolve("MINECART");
    public static final EntityType<?> MOOSHROOM = resolve("MOOSHROOM");
    public static final EntityType<?> MULE = resolve("MULE");
    public static final EntityType<?> NAUTILUS = resolve("NAUTILUS");
    public static final EntityType<?> OAK_BOAT = resolve("OAK_BOAT");
    public static final EntityType<?> OAK_CHEST_BOAT = resolve("OAK_CHEST_BOAT");
    public static final EntityType<?> OCELOT = resolve("OCELOT");
    public static final EntityType<?> PALE_OAK_BOAT = resolve("PALE_OAK_BOAT");
    public static final EntityType<?> PALE_OAK_CHEST_BOAT = resolve("PALE_OAK_CHEST_BOAT");
    public static final EntityType<?> PANDA = resolve("PANDA");
    public static final EntityType<?> PIG = resolve("PIG");
    public static final EntityType<?> PLAYER = resolve("PLAYER");
    public static final EntityType<?> RABBIT = resolve("RABBIT");
    public static final EntityType<?> SHEEP = resolve("SHEEP");
    public static final EntityType<?> SKELETON_HORSE = resolve("SKELETON_HORSE");
    public static final EntityType<?> SNOWBALL = resolve("SNOWBALL");
    public static final EntityType<?> SPECTRAL_ARROW = resolve("SPECTRAL_ARROW");
    public static final EntityType<?> SPLASH_POTION = resolve("SPLASH_POTION");
    public static final EntityType<?> SPRUCE_BOAT = resolve("SPRUCE_BOAT");
    public static final EntityType<?> SPRUCE_CHEST_BOAT = resolve("SPRUCE_CHEST_BOAT");
    public static final EntityType<?> STRIDER = resolve("STRIDER");
    public static final EntityType<?> TRADER_LLAMA = resolve("TRADER_LLAMA");
    public static final EntityType<?> TRIDENT = resolve("TRIDENT");
    public static final EntityType<?> TURTLE = resolve("TURTLE");
    public static final EntityType<?> WARDEN = resolve("WARDEN");
    public static final EntityType<?> WIND_CHARGE = resolve("WIND_CHARGE");
    public static final EntityType<?> WITHER = resolve("WITHER");
    public static final EntityType<?> WOLF = resolve("WOLF");
    public static final EntityType<?> ZOMBIE_HORSE = resolve("ZOMBIE_HORSE");
    public static final EntityType<?> ZOMBIE_NAUTILUS = resolve("ZOMBIE_NAUTILUS");

    private static EntityType<?> resolve(String name) {
        try {
            Class<?> cls;
            try {
                cls = Class.forName("net.minecraft.world.entity.EntityTypes");
            } catch (ClassNotFoundException e) {
                cls = EntityType.class;
            }
            Object value = cls.getField(name).get(null);
            return value instanceof EntityType<?> type ? type : null;
        } catch (Throwable t) {
            return null;
        }
    }
}
