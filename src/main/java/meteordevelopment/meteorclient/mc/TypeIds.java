/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.mc;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Version-neutral entity and block type identifiers.
 *
 * <p>Constants such as {@code EntityType.WARDEN} or {@code Blocks.OXIDIZED_COPPER} come and go
 * between Minecraft versions. Meteor stores the identifiers as strings instead and resolves
 * them against the registry of the version it is running in. Unknown identifiers are simply
 * absent: an entity type that does not exist in a version cannot be matched there, and a block
 * that does not exist cannot be placed.</p>
 *
 * <p>Identifiers are namespaced resource locations ({@code minecraft:creeper}). The
 * {@code minecraft:} prefix may be omitted in the constant lists below.</p>
 */
public final class TypeIds {
    private TypeIds() {}

    public static final Set<String> ATTACKABLE_BLACKLIST = unmodifiable(
        "area_effect_cloud", "arrow", "falling_block", "firework_rocket", "item", "llama_spit",
        "spectral_arrow", "ender_pearl", "experience_bottle", "splash_potion", "lingering_potion",
        "trident", "lightning_bolt", "fishing_bobber", "experience_orb", "egg"
    );

    public static final Set<String> RIDEABLE = unmodifiable(
        "pig", "strider", "horse", "donkey", "mule", "skeleton_horse", "zombie_horse",
        "llama", "trader_llama", "camel", "camel_husk", "minecart",
        "oak_boat", "spruce_boat", "birch_boat", "jungle_boat", "acacia_boat", "cherry_boat",
        "dark_oak_boat", "pale_oak_boat", "mangrove_boat", "bamboo_raft",
        "acacia_chest_boat", "birch_chest_boat", "cherry_chest_boat", "dark_oak_chest_boat",
        "jungle_chest_boat", "mangrove_chest_boat", "oak_chest_boat", "pale_oak_chest_boat",
        "spruce_chest_boat", "bamboo_chest_raft", "nautilus", "zombie_nautilus", "happy_ghast"
    );

    /** Weapon item identifiers used by combat modules. The mace only exists in newer versions. */
    public static final Set<String> WEAPONS = unmodifiable(
        "diamond_sword", "diamond_axe", "diamond_pickaxe", "diamond_shovel", "diamond_hoe",
        "diamond_spear", "trident", "mace"
    );

    /** Shulker box item identifiers, including all 16 dye colours. */
    public static final Set<String> SHULKER_BOXES = unmodifiable(
        "shulker_box", "white_shulker_box", "orange_shulker_box", "magenta_shulker_box",
        "light_blue_shulker_box", "yellow_shulker_box", "lime_shulker_box", "pink_shulker_box",
        "gray_shulker_box", "light_gray_shulker_box", "cyan_shulker_box", "purple_shulker_box",
        "blue_shulker_box", "brown_shulker_box", "green_shulker_box", "red_shulker_box",
        "black_shulker_box"
    );

    /** Block entity type identifiers used by the block-entity modules. */
    public static final Set<String> CONTAINERS = unmodifiable(
        "barrel", "blast_furnace", "brewing_stand", "campfire", "chest", "chiseled_bookshelf",
        "crafter", "decorated_pot", "dispenser", "dropper", "ender_chest", "furnace", "hopper",
        "shulker_box", "smoker", "trapped_chest"
    );

    private static Set<String> unmodifiable(String... ids) {
        return Collections.unmodifiableSet(new LinkedHashSet<>(Arrays.asList(ids)));
    }
}
