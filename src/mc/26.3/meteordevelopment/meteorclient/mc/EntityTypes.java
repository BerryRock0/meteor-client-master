/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.mc;

import net.minecraft.world.entity.EntityType;

/**
 * Version-neutral access to the entity type constants: 26.1 keeps them on {@code EntityType},
 * 26.2 moved them to {@code EntityTypes}. Shared code uses this facade instead of either.
 */
public final class EntityTypes {
    private EntityTypes() {}
    public static final EntityType<?> ACACIA_BOAT = net.minecraft.world.entity.EntityTypes.ACACIA_BOAT;
    public static final EntityType<?> ACACIA_CHEST_BOAT = net.minecraft.world.entity.EntityTypes.ACACIA_CHEST_BOAT;
    public static final EntityType<?> AREA_EFFECT_CLOUD = net.minecraft.world.entity.EntityTypes.AREA_EFFECT_CLOUD;
    public static final EntityType<?> ARMOR_STAND = net.minecraft.world.entity.EntityTypes.ARMOR_STAND;
    public static final EntityType<?> ARROW = net.minecraft.world.entity.EntityTypes.ARROW;
    public static final EntityType<?> BAMBOO_CHEST_RAFT = net.minecraft.world.entity.EntityTypes.BAMBOO_CHEST_RAFT;
    public static final EntityType<?> BAMBOO_RAFT = net.minecraft.world.entity.EntityTypes.BAMBOO_RAFT;
    public static final EntityType<?> BEE = net.minecraft.world.entity.EntityTypes.BEE;
    public static final EntityType<?> BIRCH_BOAT = net.minecraft.world.entity.EntityTypes.BIRCH_BOAT;
    public static final EntityType<?> BIRCH_CHEST_BOAT = net.minecraft.world.entity.EntityTypes.BIRCH_CHEST_BOAT;
    public static final EntityType<?> CAMEL = net.minecraft.world.entity.EntityTypes.CAMEL;
    public static final EntityType<?> CAMEL_HUSK = net.minecraft.world.entity.EntityTypes.CAMEL_HUSK;
    public static final EntityType<?> CAT = net.minecraft.world.entity.EntityTypes.CAT;
    public static final EntityType<?> CHERRY_BOAT = net.minecraft.world.entity.EntityTypes.CHERRY_BOAT;
    public static final EntityType<?> CHERRY_CHEST_BOAT = net.minecraft.world.entity.EntityTypes.CHERRY_CHEST_BOAT;
    public static final EntityType<?> CHICKEN = net.minecraft.world.entity.EntityTypes.CHICKEN;
    public static final EntityType<?> COW = net.minecraft.world.entity.EntityTypes.COW;
    public static final EntityType<?> DARK_OAK_BOAT = net.minecraft.world.entity.EntityTypes.DARK_OAK_BOAT;
    public static final EntityType<?> DARK_OAK_CHEST_BOAT = net.minecraft.world.entity.EntityTypes.DARK_OAK_CHEST_BOAT;
    public static final EntityType<?> DONKEY = net.minecraft.world.entity.EntityTypes.DONKEY;
    public static final EntityType<?> EGG = net.minecraft.world.entity.EntityTypes.EGG;
    public static final EntityType<?> ENDER_PEARL = net.minecraft.world.entity.EntityTypes.ENDER_PEARL;
    public static final EntityType<?> END_CRYSTAL = net.minecraft.world.entity.EntityTypes.END_CRYSTAL;
    public static final EntityType<?> EXPERIENCE_BOTTLE = net.minecraft.world.entity.EntityTypes.EXPERIENCE_BOTTLE;
    public static final EntityType<?> EXPERIENCE_ORB = net.minecraft.world.entity.EntityTypes.EXPERIENCE_ORB;
    public static final EntityType<?> EYE_OF_ENDER = net.minecraft.world.entity.EntityTypes.EYE_OF_ENDER;
    public static final EntityType<?> FALLING_BLOCK = net.minecraft.world.entity.EntityTypes.FALLING_BLOCK;
    public static final EntityType<?> FIREWORK_ROCKET = net.minecraft.world.entity.EntityTypes.FIREWORK_ROCKET;
    public static final EntityType<?> FISHING_BOBBER = net.minecraft.world.entity.EntityTypes.FISHING_BOBBER;
    public static final EntityType<?> FOX = net.minecraft.world.entity.EntityTypes.FOX;
    public static final EntityType<?> GLOW_ITEM_FRAME = net.minecraft.world.entity.EntityTypes.GLOW_ITEM_FRAME;
    public static final EntityType<?> HAPPY_GHAST = net.minecraft.world.entity.EntityTypes.HAPPY_GHAST;
    public static final EntityType<?> HOGLIN = net.minecraft.world.entity.EntityTypes.HOGLIN;
    public static final EntityType<?> HORSE = net.minecraft.world.entity.EntityTypes.HORSE;
    public static final EntityType<?> ITEM = net.minecraft.world.entity.EntityTypes.ITEM;
    public static final EntityType<?> ITEM_FRAME = net.minecraft.world.entity.EntityTypes.ITEM_FRAME;
    public static final EntityType<?> JUNGLE_BOAT = net.minecraft.world.entity.EntityTypes.JUNGLE_BOAT;
    public static final EntityType<?> JUNGLE_CHEST_BOAT = net.minecraft.world.entity.EntityTypes.JUNGLE_CHEST_BOAT;
    public static final EntityType<?> LIGHTNING_BOLT = net.minecraft.world.entity.EntityTypes.LIGHTNING_BOLT;
    public static final EntityType<?> LINGERING_POTION = net.minecraft.world.entity.EntityTypes.LINGERING_POTION;
    public static final EntityType<?> LLAMA = net.minecraft.world.entity.EntityTypes.LLAMA;
    public static final EntityType<?> LLAMA_SPIT = net.minecraft.world.entity.EntityTypes.LLAMA_SPIT;
    public static final EntityType<?> MANGROVE_BOAT = net.minecraft.world.entity.EntityTypes.MANGROVE_BOAT;
    public static final EntityType<?> MANGROVE_CHEST_BOAT = net.minecraft.world.entity.EntityTypes.MANGROVE_CHEST_BOAT;
    public static final EntityType<?> MINECART = net.minecraft.world.entity.EntityTypes.MINECART;
    public static final EntityType<?> MOOSHROOM = net.minecraft.world.entity.EntityTypes.MOOSHROOM;
    public static final EntityType<?> MULE = net.minecraft.world.entity.EntityTypes.MULE;
    public static final EntityType<?> NAUTILUS = net.minecraft.world.entity.EntityTypes.NAUTILUS;
    public static final EntityType<?> OAK_BOAT = net.minecraft.world.entity.EntityTypes.OAK_BOAT;
    public static final EntityType<?> OAK_CHEST_BOAT = net.minecraft.world.entity.EntityTypes.OAK_CHEST_BOAT;
    public static final EntityType<?> OCELOT = net.minecraft.world.entity.EntityTypes.OCELOT;
    public static final EntityType<?> PALE_OAK_BOAT = net.minecraft.world.entity.EntityTypes.PALE_OAK_BOAT;
    public static final EntityType<?> PALE_OAK_CHEST_BOAT = net.minecraft.world.entity.EntityTypes.PALE_OAK_CHEST_BOAT;
    public static final EntityType<?> PANDA = net.minecraft.world.entity.EntityTypes.PANDA;
    public static final EntityType<?> PIG = net.minecraft.world.entity.EntityTypes.PIG;
    public static final EntityType<?> PLAYER = net.minecraft.world.entity.EntityTypes.PLAYER;
    public static final EntityType<?> RABBIT = net.minecraft.world.entity.EntityTypes.RABBIT;
    public static final EntityType<?> SHEEP = net.minecraft.world.entity.EntityTypes.SHEEP;
    public static final EntityType<?> SKELETON_HORSE = net.minecraft.world.entity.EntityTypes.SKELETON_HORSE;
    public static final EntityType<?> SNOWBALL = net.minecraft.world.entity.EntityTypes.SNOWBALL;
    public static final EntityType<?> SPECTRAL_ARROW = net.minecraft.world.entity.EntityTypes.SPECTRAL_ARROW;
    public static final EntityType<?> SPLASH_POTION = net.minecraft.world.entity.EntityTypes.SPLASH_POTION;
    public static final EntityType<?> SPRUCE_BOAT = net.minecraft.world.entity.EntityTypes.SPRUCE_BOAT;
    public static final EntityType<?> SPRUCE_CHEST_BOAT = net.minecraft.world.entity.EntityTypes.SPRUCE_CHEST_BOAT;
    public static final EntityType<?> STRIDER = net.minecraft.world.entity.EntityTypes.STRIDER;
    public static final EntityType<?> TRADER_LLAMA = net.minecraft.world.entity.EntityTypes.TRADER_LLAMA;
    public static final EntityType<?> TRIDENT = net.minecraft.world.entity.EntityTypes.TRIDENT;
    public static final EntityType<?> TURTLE = net.minecraft.world.entity.EntityTypes.TURTLE;
    public static final EntityType<?> WARDEN = net.minecraft.world.entity.EntityTypes.WARDEN;
    public static final EntityType<?> WIND_CHARGE = net.minecraft.world.entity.EntityTypes.WIND_CHARGE;
    public static final EntityType<?> WITHER = net.minecraft.world.entity.EntityTypes.WITHER;
    public static final EntityType<?> WOLF = net.minecraft.world.entity.EntityTypes.WOLF;
    public static final EntityType<?> ZOMBIE_HORSE = net.minecraft.world.entity.EntityTypes.ZOMBIE_HORSE;
    public static final EntityType<?> ZOMBIE_NAUTILUS = net.minecraft.world.entity.EntityTypes.ZOMBIE_NAUTILUS;
}
