package meteordevelopment.meteorclient.settings;

import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.WidgetScreen;
import meteordevelopment.meteorclient.utils.misc.IChangeable;
import meteordevelopment.meteorclient.utils.misc.ICopyable;
import meteordevelopment.meteorclient.utils.misc.ISerializable;
import meteordevelopment.meteorclient.gui.screens.settings.ItemDataSetting;
import net.minecraft.item.Item;

public interface IItemData<T extends ICopyable<T> & ISerializable<T> & IChangeable & IItemData<T>>
{
    WidgetScreen createScreen(GuiTheme theme, Item item, ItemDataSetting<T> setting);
}
