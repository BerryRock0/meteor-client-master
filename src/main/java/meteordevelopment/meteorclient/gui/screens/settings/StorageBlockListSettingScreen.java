/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.gui.screens.settings;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.screens.settings.base.CollectionListSettingScreen;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.StorageBlockListSetting;
import meteordevelopment.meteorclient.mc.Vanilla;
import meteordevelopment.meteorclient.utils.render.DisplayItemUtils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import meteordevelopment.meteorclient.mc.Items;
import net.minecraft.world.level.block.entity.BlockEntityType;
import meteordevelopment.meteorclient.mc.BlockEntityTypes;

import java.util.List;
import java.util.Map;

public class StorageBlockListSettingScreen extends CollectionListSettingScreen<BlockEntityType<?>> {
    private static final Map<BlockEntityType<?>, BlockEntityTypeInfo> BLOCK_ENTITY_TYPE_INFO_MAP = new Object2ObjectOpenHashMap<>();
    private static final BlockEntityTypeInfo UNKNOWN = new BlockEntityTypeInfo(Items.BARRIER, "Unknown");

    static {
        // Map of storage blocks. Types and items that do not exist in the running version are null.
        put(BlockEntityTypes.BARREL, Items.BARREL, "Barrel");
        put(BlockEntityTypes.BLAST_FURNACE, Items.BLAST_FURNACE, "Blast Furnace");
        put(BlockEntityTypes.BREWING_STAND, Items.BREWING_STAND, "Brewing Stand");
        put(BlockEntityTypes.CAMPFIRE, Items.CAMPFIRE, "Campfire");
        put(BlockEntityTypes.CHEST, Items.CHEST, "Chest");
        put(BlockEntityTypes.CHISELED_BOOKSHELF, Vanilla.item("chiseled_bookshelf"), "Chiseled Bookshelf");
        put(BlockEntityTypes.CRAFTER, Vanilla.item("crafter"), "Crafter");
        put(BlockEntityTypes.DISPENSER, Items.DISPENSER, "Dispenser");
        put(BlockEntityTypes.DECORATED_POT, Vanilla.item("decorated_pot"), "Decorated Pot");
        put(BlockEntityTypes.DROPPER, Items.DROPPER, "Dropper");
        put(BlockEntityTypes.ENDER_CHEST, Items.ENDER_CHEST, "Ender Chest");
        put(BlockEntityTypes.FURNACE, Items.FURNACE, "Furnace");
        put(BlockEntityTypes.HOPPER, Items.HOPPER, "Hopper");
        put(BlockEntityTypes.SHULKER_BOX, Items.SHULKER_BOX, "Shulker Box");
        put(BlockEntityTypes.SMOKER, Items.SMOKER, "Smoker");
        put(BlockEntityTypes.TRAPPED_CHEST, Items.TRAPPED_CHEST, "Trapped Chest");
    }

    private static void put(BlockEntityType<?> type, Item item, String name) {
        if (type != null && item != null) BLOCK_ENTITY_TYPE_INFO_MAP.put(type, new BlockEntityTypeInfo(item, name));
    }

    public StorageBlockListSettingScreen(GuiTheme theme, Setting<List<BlockEntityType<?>>> setting) {
        super(theme, "Select Storage Blocks", setting, setting.get(), StorageBlockListSetting.REGISTRY);
    }

    @Override
    protected WWidget getValueWidget(BlockEntityType<?> value) {
        BlockEntityTypeInfo info = BLOCK_ENTITY_TYPE_INFO_MAP.getOrDefault(value, UNKNOWN);
        return theme.itemWithLabel(DisplayItemUtils.toStack(info.item()), info.name());
    }

    @Override
    protected String[] getValueNames(BlockEntityType<?> value) {
        return new String[]{
            BLOCK_ENTITY_TYPE_INFO_MAP.getOrDefault(value, UNKNOWN).name(),
            BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(value).toString()
        };
    }

    private record BlockEntityTypeInfo(Item item, String name) {
    }
}
