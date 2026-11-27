/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.settings;

import meteordevelopment.meteorclient.mc.Nbt;

import meteordevelopment.meteorclient.mc.Vanilla;

import com.mojang.serialization.Lifecycle;
import it.unimi.dsi.fastutil.objects.ObjectIterators;
import meteordevelopment.meteorclient.MeteorClient;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.entity.BlockEntityType;
import meteordevelopment.meteorclient.mc.BlockEntityTypes;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class StorageBlockListSetting extends Setting<List<BlockEntityType<?>>> {
    public static final BlockEntityType<?>[] STORAGE_BLOCKS = new BlockEntityType[]{
        BlockEntityTypes.BARREL,
        BlockEntityTypes.BLAST_FURNACE,
        BlockEntityTypes.BREWING_STAND,
        BlockEntityTypes.CAMPFIRE,
        BlockEntityTypes.CHEST,
        BlockEntityTypes.CHISELED_BOOKSHELF,
        BlockEntityTypes.CRAFTER,
        BlockEntityTypes.DISPENSER,
        BlockEntityTypes.DECORATED_POT,
        BlockEntityTypes.DROPPER,
        BlockEntityTypes.ENDER_CHEST,
        BlockEntityTypes.FURNACE,
        BlockEntityTypes.HOPPER,
        BlockEntityTypes.SHULKER_BOX,
        BlockEntityTypes.SMOKER,
        BlockEntityTypes.TRAPPED_CHEST,
    };

    public static final Iterable<BlockEntityType<?>> REGISTRY = java.util.Arrays.asList(STORAGE_BLOCKS);

    public StorageBlockListSetting(String name, String description, List<BlockEntityType<?>> defaultValue, Consumer<List<BlockEntityType<?>>> onChanged, Consumer<Setting<List<BlockEntityType<?>>>> onModuleActivated, IVisible visible) {
        super(name, description, defaultValue, onChanged, onModuleActivated, visible);
    }

    @Override
    public void resetImpl() {
        value = new ArrayList<>(defaultValue);
    }

    @Override
    protected List<BlockEntityType<?>> parseImpl(String str) {
        String[] values = str.split(",");
        List<BlockEntityType<?>> blocks = new ArrayList<>(values.length);

        try {
            for (String value : values) {
                BlockEntityType<?> block = parseId(BuiltInRegistries.BLOCK_ENTITY_TYPE, value);
                if (block != null) blocks.add(block);
            }
        } catch (Exception _) {
        }

        return blocks;
    }

    @Override
    protected boolean isValueValid(List<BlockEntityType<?>> value) {
        return true;
    }

    @Override
    public Iterable<Identifier> getIdentifierSuggestions() {
        return Vanilla.identifiers(BuiltInRegistries.BLOCK_ENTITY_TYPE.keySet());
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        ListTag valueTag = new ListTag();
        for (BlockEntityType<?> type : get()) {
            Identifier id = Vanilla.identifier(BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(type));
            if (id != null) valueTag.add(StringTag.valueOf(id.toString()));
        }
        tag.put("value", valueTag);

        return tag;
    }

    @Override
    public List<BlockEntityType<?>> load(CompoundTag tag) {
        get().clear();

        ListTag valueTag = Nbt.getListOrEmpty(tag, "value");
        for (Tag tagI : valueTag) {
            BlockEntityType<?> type = BuiltInRegistries.BLOCK_ENTITY_TYPE.getValue(Identifier.parse(Nbt.asString(tagI).orElse("")));
            if (type != null) get().add(type);
        }

        return get();
    }

    public static class Builder extends SettingBuilder<Builder, List<BlockEntityType<?>>, StorageBlockListSetting> {
        public Builder() {
            super(new ArrayList<>(0));
        }

        public Builder defaultValue(BlockEntityType<?>... defaults) {
            return defaultValue(defaults != null ? Arrays.asList(defaults) : new ArrayList<>());
        }

        @Override
        public StorageBlockListSetting build() {
            return new StorageBlockListSetting(name, description, defaultValue, onChanged, onModuleActivated, visible);
        }
    }

}
