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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class ItemHighlight extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    // General

    public final Setting<List<Item>> items = sgGeneral.add(new ItemListSetting.Builder()
        .name("items")
        .description("Items to highlight.")
        .build()
    );

    // Colors

    public final Setting<SettingColor> color = sgGeneral.add(new ColorSetting.Builder()
        .name("color")
        .description("The color to highlight the items with.")
        .defaultValue(new SettingColor(225, 25, 255, 50))
        .build()
    );

    public ItemHighlight() {
        super(Categories.Render, "item-highlight", "Highlights selected items when in guis");
    }

    public boolean shouldHighlight(ItemStack stack) {
        return stack != null && items.get().contains(stack.getItem()) && isActive();
    }

    public int getColor(ItemStack stack) {
        return shouldHighlight(stack) ? color.get().getPacked() : -1;
    }
}
