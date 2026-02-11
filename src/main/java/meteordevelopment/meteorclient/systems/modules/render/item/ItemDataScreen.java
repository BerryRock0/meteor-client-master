package meteordevelopment.meteorclient.systems.modules.render.item;

import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.WindowScreen;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
//import meteordevelopment.meteorclient.systems.modules.render.item.ItemData;
import net.minecraft.item.Item;
import org.jetbrains.annotations.Nullable;

public class ItemDataScreen extends WindowScreen
{
    private final ItemData itemData;
    private final Setting<?> setting;
    private final @Nullable Runnable firstChangeConsumer;

    public ItemDataScreen(GuiTheme theme, ItemData itemData, Item item, ItemDataSetting<ItemData> setting)
    {
        this(theme, itemData, setting, () -> setting.get().put(item, itemData));
    }


    public ItemDataScreen(GuiTheme theme, ItemData blockData, GenericSetting<ItemData> setting)
    {
        this(theme, blockData, setting, null);
    }


    private ItemDataScreen(GuiTheme theme, ItemData itemData, Setting<?> setting, @Nullable Runnable firstChangeConsumer)
    {
        super(theme, "Configure Item");

        this.itemData = itemData;
        this.setting = setting;
        this.firstChangeConsumer = firstChangeConsumer;
    }

    @Override
    public void initWidgets()
    {
        Settings settings = new Settings();
        SettingGroup sgGeneral = settings.getDefaultGroup();

        sgGeneral.add(new ColorSetting.Builder()
            .name("item-color")
            .description("Color of items.")
            .defaultValue(new SettingColor(0, 0, 0, 0))
            .onModuleActivated(settingColorSetting -> settingColorSetting.get().set(itemData.itemColor))
            .onChanged(settingColor ->
            {
                if (!itemData.itemColor.equals(settingColor))
                {
                    itemData.itemColor.set(settingColor);
                    onChanged();
                }
            })
            .build()
        );

        settings.onActivated();
        add(theme.settings(settings)).expandX();
    }

        private void onChanged()
        {
            if (!itemData.isChanged() && firstChangeConsumer != null)
            {
                firstChangeConsumer.run();
            }

            setting.onChanged();
            itemData.changed();
        }
}

