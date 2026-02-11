/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.systems.modules.render;

import meteordevelopment.meteorclient.settings.ItemListSetting;
import meteordevelopment.meteorclient.gui.screens.settings.ItemDataSetting;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.render.item.*;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.List;
import java.util.Map;

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
        .defaultValue(new ItemData(new SettingColor(0, 0, 0, 0)))
        .build()
    );

    private final Setting<Map<Item, ItemData>> itemConfigs = sgGeneral.add(new ItemDataSetting.Builder<ItemData>()
        .name("item-configs")
        .description("Config for each block.")
        .defaultData(defaultItemConfig)
        .build()
    );

    Color color;
    
    public ItemHighlight() {
        super(Categories.Render, "item-highlight", "Highlights selected items when in guis");
    }

    public int getColor(ItemStack stack)
    {
        ItemData data = itemConfigs.get().get(stack.getItem()); //data.itemColor;

        if (data != null)
        if (stack != null && items.get().contains(stack.getItem()) && isActive())
            return color.fromRGBA(data.itemColor.r,data.itemColor.g,data.itemColor.b,data.itemColor.a);
        return -1;
    }
}
