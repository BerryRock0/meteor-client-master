package meteordevelopment.meteorclient.gui.screens.settings;

import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.screens.settings.ItemDataSetting;
import meteordevelopment.meteorclient.gui.screens.settings.base.CollectionMapSettingScreen;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.gui.widgets.pressable.WButton;
import meteordevelopment.meteorclient.settings.IItemData;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.IVisible;
import meteordevelopment.meteorclient.utils.misc.IGetter;
import meteordevelopment.meteorclient.utils.misc.IChangeable;
import meteordevelopment.meteorclient.utils.misc.ICopyable;
import meteordevelopment.meteorclient.utils.misc.ISerializable;
import meteordevelopment.meteorclient.utils.misc.Names;
import net.minecraft.item.Item;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.registry.Registries;
import org.jetbrains.annotations.Nullable;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class ItemDataSettingScreen<T extends ICopyable<T> & ISerializable<T> & IChangeable & IItemData<T>> extends CollectionMapSettingScreen<Item, T> {
    private final ItemDataSetting<T> setting;
    private boolean invalidate;

    public ItemDataSettingScreen(GuiTheme theme, ItemDataSetting<T> setting) {
        super(theme, "Configure Items", setting, setting.get(), Registries.ITEM);

        this.setting = setting;
    }

    @Override
    protected boolean includeValue(Item value) {
        return value != null;
    }

    @Override
    protected WWidget getValueWidget(Item item) {
        return theme.itemWithLabel(item.getDefaultStack(), Names.get(item));
    }

    @Override
    protected WWidget getDataWidget(Item item, @Nullable T itemData) {
        WButton edit = theme.button(GuiRenderer.EDIT);
        edit.action = () -> {
            T data = itemData;
            if (data == null) data = setting.defaultData.get().copy();

            mc.setScreen(data.createScreen(theme, item, setting));
            invalidate = true;
        };
        return edit;
    }

    @Override
    protected void onRenderBefore(DrawContext drawContext, float delta) {
        if (invalidate) {
            this.invalidateTable();
            invalidate = false;
        }
    }

    @Override
    protected String[] getValueNames(Item item) {
        return new String[]{
            Names.get(item),
            Registries.ITEM.getId(item).toString()
        };
    }
}
