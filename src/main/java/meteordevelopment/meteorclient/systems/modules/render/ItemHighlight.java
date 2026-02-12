/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.systems.modules.render;

import meteordevelopment.meteorclient.settings.ColorListSetting;
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

    public final Setting<List<SettingColor>> colors = sgGeneral.add(new ColorListSetting.Builder()
        .name("colors")
        .description("Colors used for the Text element.")
        .defaultValue(List.of(new SettingColor(0,0,0,0)))
        .build()
    );

    public int a;

    public ItemHighlight() {
        super(Categories.Render, "item-highlight", "Highlights selected items when in guis");
    }

    public int getColor(ItemStack stack)
    {
        if(colors.get().size() != null && items.get().size() != null)
        if (a != colors.get().size()-1) a++; else a = 0;

        if (isActive() && stack != null)
        if(colors.get().size() == items.get().size())
        if(items.get().contains(stack.getItem()))
            return colors.get().get(a).getPacked();
        
        return -1;
    }
}
