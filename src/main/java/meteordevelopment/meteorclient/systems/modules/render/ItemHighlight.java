/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.systems.modules.render;

import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.ItemListSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.List;

public class ItemHighlight extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<List<Item>> items = sgGeneral.add(new ItemListSetting.Builder()
        .name("items")
        .description("Items to highlight.")
        .build()
    );

    private final Setting<ItemData> defaultItemConfig = sgGeneral.add(new GenericSetting.Builder<ItemData>()
        .name("default-item-config")
        .description("Default item config.")
        .defaultValue(
            new ESPBlockData(
                new SettingColor(0, 0, 0, 0),
            )
        )
        .build()
    );

    private final Setting<Map<Item, ItemData>> itemConfigs = sgGeneral.add(new ItemDataSetting.Builder<ItemData>()
        .name("item-configs")
        .description("Config for each block.")
        .defaultData(defaultBlockConfig)
        .build()
    );

    ItemData data;
    Color color;
    
    public ItemHighlight() {
        super(Categories.Render, "item-highlight", "Highlights selected items when in guis");
    }

    public int getColor(ItemStack stack) {

        data = getItemData(stack.getItem());
        color = data.itemColor;
        if (stack != null && items.get().contains(stack.getItem()) && isActive())
            return color;
        return -1;
    }
    
    ItemData getItemData(Item item)
    {
        ItemData itemData = itemConfigs.get().get(item);
        return itemData == null ? defaultItemConfig.get() : itemData;
    }
}
