/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.mc;

import net.minecraft.world.item.Item;

/**
 * Version-neutral access to item constants.
 *
 * <p>Constants are resolved reflectively. An item that does not exist in the running version
 * resolves to {@code null} instead of breaking compilation. Shared code programs against this
 * facade and never references version-specific {@code net.minecraft.world.item.Items} fields.</p>
 */
public final class Items {
    private Items() {}

    public static final Item AIR = resolve("AIR");
    public static final Item ARMOR_STAND = resolve("ARMOR_STAND");
    public static final Item ARROW = resolve("ARROW");
    public static final Item BARREL = resolve("BARREL");
    public static final Item BARRIER = resolve("BARRIER");
    public static final Item BASALT = resolve("BASALT");
    public static final Item BLACKSTONE = resolve("BLACKSTONE");
    public static final Item BLACK_SHULKER_BOX = resolve("BLACK_SHULKER_BOX");
    public static final Item BLAST_FURNACE = resolve("BLAST_FURNACE");
    public static final Item BLAZE_POWDER = resolve("BLAZE_POWDER");
    public static final Item BLUE_SHULKER_BOX = resolve("BLUE_SHULKER_BOX");
    public static final Item BOW = resolve("BOW");
    public static final Item BREWING_STAND = resolve("BREWING_STAND");
    public static final Item BROWN_SHULKER_BOX = resolve("BROWN_SHULKER_BOX");
    public static final Item BUCKET = resolve("BUCKET");
    public static final Item CAMPFIRE = resolve("CAMPFIRE");
    public static final Item CHARCOAL = resolve("CHARCOAL");
    public static final Item CHEST = resolve("CHEST");
    public static final Item CHICKEN = resolve("CHICKEN");
    public static final Item CHORUS_FRUIT = resolve("CHORUS_FRUIT");
    public static final Item COAL = resolve("COAL");
    public static final Item COBWEB = resolve("COBWEB");
    public static final Item COMPASS = resolve("COMPASS");
    public static final Item COPPER_ORE = resolve("COPPER_ORE");
    public static final Item CROSSBOW = resolve("CROSSBOW");
    public static final Item CRYING_OBSIDIAN = resolve("CRYING_OBSIDIAN");
    public static final Item CYAN_SHULKER_BOX = resolve("CYAN_SHULKER_BOX");
    public static final Item DIAMOND_AXE = resolve("DIAMOND_AXE");
    public static final Item DIAMOND_BOOTS = resolve("DIAMOND_BOOTS");
    public static final Item DIAMOND_CHESTPLATE = resolve("DIAMOND_CHESTPLATE");
    public static final Item DIAMOND_HOE = resolve("DIAMOND_HOE");
    public static final Item DIAMOND_PICKAXE = resolve("DIAMOND_PICKAXE");
    public static final Item DIAMOND_SHOVEL = resolve("DIAMOND_SHOVEL");
    public static final Item DIAMOND_SPEAR = resolve("DIAMOND_SPEAR");
    public static final Item DIAMOND_SWORD = resolve("DIAMOND_SWORD");
    public static final Item DISPENSER = resolve("DISPENSER");
    public static final Item DROPPER = resolve("DROPPER");
    public static final Item ELYTRA = resolve("ELYTRA");
    public static final Item ENCHANTED_BOOK = resolve("ENCHANTED_BOOK");
    public static final Item ENCHANTED_GOLDEN_APPLE = resolve("ENCHANTED_GOLDEN_APPLE");
    public static final Item ENDER_CHEST = resolve("ENDER_CHEST");
    public static final Item ENDER_EYE = resolve("ENDER_EYE");
    public static final Item ENDER_PEARL = resolve("ENDER_PEARL");
    public static final Item END_CRYSTAL = resolve("END_CRYSTAL");
    public static final Item EXPERIENCE_BOTTLE = resolve("EXPERIENCE_BOTTLE");
    public static final Item FERMENTED_SPIDER_EYE = resolve("FERMENTED_SPIDER_EYE");
    public static final Item FILLED_MAP = resolve("FILLED_MAP");
    public static final Item FIREWORK_ROCKET = resolve("FIREWORK_ROCKET");
    public static final Item FIRE_CHARGE = resolve("FIRE_CHARGE");
    public static final Item FLINT_AND_STEEL = resolve("FLINT_AND_STEEL");
    public static final Item FURNACE = resolve("FURNACE");
    public static final Item GHAST_TEAR = resolve("GHAST_TEAR");
    public static final Item GLASS = resolve("GLASS");
    public static final Item GLISTERING_MELON_SLICE = resolve("GLISTERING_MELON_SLICE");
    public static final Item GLOWSTONE = resolve("GLOWSTONE");
    public static final Item GLOWSTONE_DUST = resolve("GLOWSTONE_DUST");
    public static final Item GOLDEN_APPLE = resolve("GOLDEN_APPLE");
    public static final Item GOLDEN_CARROT = resolve("GOLDEN_CARROT");
    public static final Item GOLDEN_SWORD = resolve("GOLDEN_SWORD");
    public static final Item GOLD_NUGGET = resolve("GOLD_NUGGET");
    public static final Item GOLD_ORE = resolve("GOLD_ORE");
    public static final Item GRASS_BLOCK = resolve("GRASS_BLOCK");
    public static final Item GRAY_SHULKER_BOX = resolve("GRAY_SHULKER_BOX");
    public static final Item GREEN_SHULKER_BOX = resolve("GREEN_SHULKER_BOX");
    public static final Item HAY_BLOCK = resolve("HAY_BLOCK");
    public static final Item HOPPER = resolve("HOPPER");
    public static final Item IRON_ORE = resolve("IRON_ORE");
    public static final Item LAVA_BUCKET = resolve("LAVA_BUCKET");
    public static final Item LIGHT_BLUE_SHULKER_BOX = resolve("LIGHT_BLUE_SHULKER_BOX");
    public static final Item LIGHT_GRAY_SHULKER_BOX = resolve("LIGHT_GRAY_SHULKER_BOX");
    public static final Item LIME_SHULKER_BOX = resolve("LIME_SHULKER_BOX");
    public static final Item MAGENTA_SHULKER_BOX = resolve("MAGENTA_SHULKER_BOX");
    public static final Item MAGMA_BLOCK = resolve("MAGMA_BLOCK");
    public static final Item MAGMA_CREAM = resolve("MAGMA_CREAM");
    public static final Item NAME_TAG = resolve("NAME_TAG");
    public static final Item NETHERITE_BOOTS = resolve("NETHERITE_BOOTS");
    public static final Item NETHERITE_CHESTPLATE = resolve("NETHERITE_CHESTPLATE");
    public static final Item NETHERITE_HELMET = resolve("NETHERITE_HELMET");
    public static final Item NETHERITE_LEGGINGS = resolve("NETHERITE_LEGGINGS");
    public static final Item NETHERITE_PICKAXE = resolve("NETHERITE_PICKAXE");
    public static final Item NETHERRACK = resolve("NETHERRACK");
    public static final Item NETHER_STAR = resolve("NETHER_STAR");
    public static final Item NETHER_WART = resolve("NETHER_WART");
    public static final Item OBSIDIAN = resolve("OBSIDIAN");
    public static final Item ORANGE_SHULKER_BOX = resolve("ORANGE_SHULKER_BOX");
    public static final Item PHANTOM_MEMBRANE = resolve("PHANTOM_MEMBRANE");
    public static final Item PINK_SHULKER_BOX = resolve("PINK_SHULKER_BOX");
    public static final Item POISONOUS_POTATO = resolve("POISONOUS_POTATO");
    public static final Item POTION = resolve("POTION");
    public static final Item POWDER_SNOW_BUCKET = resolve("POWDER_SNOW_BUCKET");
    public static final Item PUFFERFISH = resolve("PUFFERFISH");
    public static final Item PURPLE_SHULKER_BOX = resolve("PURPLE_SHULKER_BOX");
    public static final Item QUARTZ = resolve("QUARTZ");
    public static final Item RABBIT_FOOT = resolve("RABBIT_FOOT");
    public static final Item RAW_COPPER = resolve("RAW_COPPER");
    public static final Item RAW_GOLD = resolve("RAW_GOLD");
    public static final Item RAW_IRON = resolve("RAW_IRON");
    public static final Item REDSTONE = resolve("REDSTONE");
    public static final Item RED_SHULKER_BOX = resolve("RED_SHULKER_BOX");
    public static final Item RESPAWN_ANCHOR = resolve("RESPAWN_ANCHOR");
    public static final Item ROTTEN_FLESH = resolve("ROTTEN_FLESH");
    public static final Item SHEARS = resolve("SHEARS");
    public static final Item SHIELD = resolve("SHIELD");
    public static final Item SHULKER_BOX = resolve("SHULKER_BOX");
    public static final Item SLIME_BLOCK = resolve("SLIME_BLOCK");
    public static final Item SMOKER = resolve("SMOKER");
    public static final Item SOUL_SAND = resolve("SOUL_SAND");
    public static final Item SOUL_SOIL = resolve("SOUL_SOIL");
    public static final Item SPIDER_EYE = resolve("SPIDER_EYE");
    public static final Item STRING = resolve("STRING");
    public static final Item SUGAR = resolve("SUGAR");
    public static final Item SUSPICIOUS_STEW = resolve("SUSPICIOUS_STEW");
    public static final Item TIPPED_ARROW = resolve("TIPPED_ARROW");
    public static final Item TOTEM_OF_UNDYING = resolve("TOTEM_OF_UNDYING");
    public static final Item TRAPPED_CHEST = resolve("TRAPPED_CHEST");
    public static final Item TRIDENT = resolve("TRIDENT");
    public static final Item TURTLE_HELMET = resolve("TURTLE_HELMET");
    public static final Item WATER_BUCKET = resolve("WATER_BUCKET");
    public static final Item WHITE_SHULKER_BOX = resolve("WHITE_SHULKER_BOX");
    public static final Item WIND_CHARGE = resolve("WIND_CHARGE");
    public static final Item WRITABLE_BOOK = resolve("WRITABLE_BOOK");
    public static final Item WRITTEN_BOOK = resolve("WRITTEN_BOOK");
    public static final Item YELLOW_SHULKER_BOX = resolve("YELLOW_SHULKER_BOX");

    private static Item resolve(String name) {
        try {
            Class<?> cls = Class.forName("net.minecraft.world.item.Items");
            Object value = cls.getField(name).get(null);
            return value instanceof Item item ? item : null;
        } catch (Throwable t) {
            return null;
        }
    }
}
