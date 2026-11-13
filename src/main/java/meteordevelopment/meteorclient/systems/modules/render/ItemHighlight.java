/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.systems.modules.render;

import java.util.List;

import net.minecraft.world.item.*;

import meteordevelopment.meteorclient.mc.Vanilla;

import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.screens.EditSystemScreen;
import meteordevelopment.meteorclient.gui.widgets.WLabel;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.gui.widgets.containers.WTable;
import meteordevelopment.meteorclient.gui.widgets.pressable.WButton;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.systems.templates.highlights.SlotColor;
import meteordevelopment.meteorclient.systems.templates.highlights.SlotColors;

public class ItemHighlight extends Module
{
    public ItemHighlight()
    {
        super(Categories.Render, "item-highlight", "Highlights selected items when in guis");
    }
    
    @Override
    public WWidget getWidget(GuiTheme theme)
    {
        if (!Utils.canUpdate())
            return theme.label("You need to be in a world.");

        WTable table = theme.table();
        initTable(theme, table);
        return table;
    }
    
    private void initTable(GuiTheme theme, WTable table)
    {
        table.clear();

        for (SlotColor unit : SlotColors.get())
        {
            WButton edit = table.add(theme.button("Edit")).widget(); edit.action = () -> Vanilla.setScreen(new EditSlotColorScreen(theme, unit, () -> initTable(theme, table)));
            WButton delete = table.add(theme.button("Delete")).widget(); delete.action = () -> {SlotColors.get().remove(unit); initTable(theme, table);};

            table.row();
        }

        table.add(theme.horizontalSeparator()).expandX();
        table.row();

        WButton create = table.add(theme.button("Create")).expandX().widget(); create.action = () -> Vanilla.setScreen(new EditSlotColorScreen(theme, null, () -> initTable(theme, table)));
    }
    
    private static class EditSlotColorScreen extends EditSystemScreen<SlotColor>
	{
        public EditSlotColorScreen(GuiTheme theme, SlotColor value, Runnable reload)
        {
            super(theme, value, reload);
        }

        @Override
        public SlotColor create()
        {
            return new SlotColor.Builder().build();
        }

        @Override
        public boolean save()
        {
            SlotColors.get().add(value);
			return true;
        }

        @Override
        public Settings getSettings()
        {
            return value.settings;
        }
	}

    public int getColor(ItemStack stack)
    {
		for(SlotColor unit: SlotColors.get())
			if (unit.items.get().contains(stack.getItem()) && stack != null && isActive())
				return unit.color.get().getPacked();

        return -1;
    }
}
