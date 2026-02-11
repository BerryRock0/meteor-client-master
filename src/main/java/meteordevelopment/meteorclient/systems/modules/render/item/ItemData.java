package meteordevelopment.meteorclient.systems.modules.render.item;

import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.WidgetScreen;
import meteordevelopment.meteorclient.gui.screens.settings.ItemDataSetting;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.utils.misc.IChangeable;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;

public class ItemData implements IGeneric<ItemData>, IChangeable, IItemData<ItemData>
{
    public ColorSetting itemColor;
    public Item item;
    private boolean changed;

    public ItemData(ColorSetting color)
    {
        this.itemColor = itemColor;
    }

    @Override
    public WidgetScreen createScreen(GuiTheme theme, Item Item, ItemDataSetting<ItemData> setting)
    {
        return new ItemDataScreen(theme, this, item, setting);
    }

    
    @Override
    public WidgetScreen createScreen(GuiTheme theme, GenericSetting<ItemData> setting) {
        return new ItemDataScreen(theme, this, setting);
    }

    @Override
    public boolean isChanged()
    {
        return changed;
    }

    public void changed()
    {
        changed = true;
    }

    @Override
    public ItemData set(ItemData value)
    {
        itemColor.set(value.itemColor);
        changed = value.changed;

        return this;
    }

    @Override
    public ItemData copy()
    {
        return new ItemData(new SettingColor(itemColor));
    }

    @Override
    public NbtCompound toTag()
    {
        NbtCompound tag = new NbtCompound();
        tag.put("itemColor", itemColor.toTag());
        tag.putBoolean("changed", changed);

        return tag;
    }

    @Override
    public ItemData fromTag(NbtCompound tag)
    {
        //tracerColor.fromTag(tag.getCompoundOrEmpty("tracerColor"));
        itemColor.fromTag(tag.getCompoundOrEmpty("itemColor"));
        changed = tag.getBoolean("changed", false);
        return this;
    }
}
