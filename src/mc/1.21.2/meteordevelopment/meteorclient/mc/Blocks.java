/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.mc;

import net.minecraft.world.level.block.Block;

/**
 * Version-neutral access to block constants.
 *
 * <p>Constants are resolved reflectively. A block that does not exist in the running version
 * resolves to {@code null} instead of breaking compilation.</p>
 */
public final class Blocks {
    private Blocks() {}

    public static final Block AIR = resolve("AIR");
    public static final Block ANCIENT_DEBRIS = resolve("ANCIENT_DEBRIS");
    public static final Block ANDESITE = resolve("ANDESITE");
    public static final Block ANVIL = resolve("ANVIL");
    public static final Block BARREL = resolve("BARREL");
    public static final Block BARRIER = resolve("BARRIER");
    public static final Block BEDROCK = resolve("BEDROCK");
    public static final Block CHEST = resolve("CHEST");
    public static final Block COAL_ORE = resolve("COAL_ORE");
    public static final Block COBBLESTONE = resolve("COBBLESTONE");
    public static final Block COBWEB = resolve("COBWEB");
    public static final Block COPPER_ORE = resolve("COPPER_ORE");
    public static final Block CRYING_OBSIDIAN = resolve("CRYING_OBSIDIAN");
    public static final Block DARK_PRISMARINE = resolve("DARK_PRISMARINE");
    public static final Block DEEPSLATE_COAL_ORE = resolve("DEEPSLATE_COAL_ORE");
    public static final Block DEEPSLATE_COPPER_ORE = resolve("DEEPSLATE_COPPER_ORE");
    public static final Block DEEPSLATE_DIAMOND_ORE = resolve("DEEPSLATE_DIAMOND_ORE");
    public static final Block DEEPSLATE_EMERALD_ORE = resolve("DEEPSLATE_EMERALD_ORE");
    public static final Block DEEPSLATE_GOLD_ORE = resolve("DEEPSLATE_GOLD_ORE");
    public static final Block DEEPSLATE_IRON_ORE = resolve("DEEPSLATE_IRON_ORE");
    public static final Block DEEPSLATE_LAPIS_ORE = resolve("DEEPSLATE_LAPIS_ORE");
    public static final Block DEEPSLATE_REDSTONE_ORE = resolve("DEEPSLATE_REDSTONE_ORE");
    public static final Block DIAMOND_ORE = resolve("DIAMOND_ORE");
    public static final Block DIORITE = resolve("DIORITE");
    public static final Block DIRT = resolve("DIRT");
    public static final Block DIRT_PATH = resolve("DIRT_PATH");
    public static final Block EMERALD_ORE = resolve("EMERALD_ORE");
    public static final Block ENDER_CHEST = resolve("ENDER_CHEST");
    public static final Block END_PORTAL_FRAME = resolve("END_PORTAL_FRAME");
    public static final Block END_ROD = resolve("END_ROD");
    public static final Block END_STONE_BRICKS = resolve("END_STONE_BRICKS");
    public static final Block GOLD_ORE = resolve("GOLD_ORE");
    public static final Block GRANITE = resolve("GRANITE");
    public static final Block GRASS_BLOCK = resolve("GRASS_BLOCK");
    public static final Block HAY_BLOCK = resolve("HAY_BLOCK");
    public static final Block HONEY_BLOCK = resolve("HONEY_BLOCK");
    public static final Block IRON_ORE = resolve("IRON_ORE");
    public static final Block LAPIS_ORE = resolve("LAPIS_ORE");
    public static final Block LAVA = resolve("LAVA");
    public static final Block MUD = resolve("MUD");
    public static final Block NETHERITE_BLOCK = resolve("NETHERITE_BLOCK");
    public static final Block NETHERRACK = resolve("NETHERRACK");
    public static final Block NETHER_BRICKS = resolve("NETHER_BRICKS");
    public static final Block NETHER_BRICK_FENCE = resolve("NETHER_BRICK_FENCE");
    public static final Block NETHER_GOLD_ORE = resolve("NETHER_GOLD_ORE");
    public static final Block NETHER_QUARTZ_ORE = resolve("NETHER_QUARTZ_ORE");
    public static final Block NETHER_WART = resolve("NETHER_WART");
    public static final Block NOTE_BLOCK = resolve("NOTE_BLOCK");
    public static final Block OBSIDIAN = resolve("OBSIDIAN");
    public static final Block OXIDIZED_COPPER = resolve("OXIDIZED_COPPER");
    public static final Block OXIDIZED_CUT_COPPER = resolve("OXIDIZED_CUT_COPPER");
    public static final Block POWDER_SNOW = resolve("POWDER_SNOW");
    public static final Block PRISMARINE_BRICKS = resolve("PRISMARINE_BRICKS");
    public static final Block PURPUR_BLOCK = resolve("PURPUR_BLOCK");
    public static final Block PURPUR_PILLAR = resolve("PURPUR_PILLAR");
    public static final Block PURPUR_SLAB = resolve("PURPUR_SLAB");
    public static final Block PURPUR_STAIRS = resolve("PURPUR_STAIRS");
    public static final Block REDSTONE_ORE = resolve("REDSTONE_ORE");
    public static final Block REINFORCED_DEEPSLATE = resolve("REINFORCED_DEEPSLATE");
    public static final Block RESPAWN_ANCHOR = resolve("RESPAWN_ANCHOR");
    public static final Block SEA_LANTERN = resolve("SEA_LANTERN");
    public static final Block SHULKER_BOX = resolve("SHULKER_BOX");
    public static final Block SLIME_BLOCK = resolve("SLIME_BLOCK");
    public static final Block SOUL_SAND = resolve("SOUL_SAND");
    public static final Block STONE = resolve("STONE");
    public static final Block STONE_BUTTON = resolve("STONE_BUTTON");
    public static final Block STONE_SLAB = resolve("STONE_SLAB");
    public static final Block TORCH = resolve("TORCH");
    public static final Block TRAPPED_CHEST = resolve("TRAPPED_CHEST");
    public static final Block TUFF_BRICKS = resolve("TUFF_BRICKS");
    public static final Block VOID_AIR = resolve("VOID_AIR");
    public static final Block WATER = resolve("WATER");
    public static final Block WAXED_COPPER_BLOCK = resolve("WAXED_COPPER_BLOCK");
    public static final Block WAXED_COPPER_BULB = resolve("WAXED_COPPER_BULB");
    public static final Block WAXED_OXIDIZED_COPPER = resolve("WAXED_OXIDIZED_COPPER");
    public static final Block WAXED_OXIDIZED_CUT_COPPER = resolve("WAXED_OXIDIZED_CUT_COPPER");

    private static Block resolve(String name) {
        try {
            Class<?> cls = Class.forName("net.minecraft.world.level.block.Blocks");
            Object value = cls.getField(name).get(null);
            return value instanceof Block block ? block : null;
        } catch (Throwable t) {
            return null;
        }
    }
}
