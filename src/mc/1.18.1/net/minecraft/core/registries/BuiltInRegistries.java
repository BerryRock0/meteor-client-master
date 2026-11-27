/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package net.minecraft.core.registries;

import net.minecraft.core.Registry;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.stats.Stat;

import java.lang.reflect.Field;

/**
 * Compatibility shim: 1.19.3+ moved the built-in registries from {@code Registry} static fields
 * to {@code BuiltInRegistries}. Fields are resolved reflectively so a registry that did not exist
 * in a given version is {@code null} rather than a compile error.
 */
public final class BuiltInRegistries {
    private BuiltInRegistries() {}

    public static final Registry<Block> BLOCK = get("BLOCK");
    public static final Registry<BlockEntityType<?>> BLOCK_ENTITY_TYPE = get("BLOCK_ENTITY_TYPE");
    public static final Registry<EntityType<?>> ENTITY_TYPE = get("ENTITY_TYPE");
    public static final Registry<Item> ITEM = get("ITEM");
    public static final Registry<MenuType<?>> MENU = get("MENU");
    public static final Registry<MobEffect> MOB_EFFECT = get("MOB_EFFECT");
    public static final Registry<ParticleType<?>> PARTICLE_TYPE = get("PARTICLE_TYPE");
    public static final Registry<SoundEvent> SOUND_EVENT = get("SOUND_EVENT");
    public static final Registry<Stat<?>> CUSTOM_STAT = get("CUSTOM_STAT");

    @SuppressWarnings("unchecked")
    private static <T> Registry<T> get(String name) {
        try {
            Field field = Registry.class.getField(name);
            return (Registry<T>) field.get(null);
        } catch (Throwable t) {
            return null;
        }
    }
}
