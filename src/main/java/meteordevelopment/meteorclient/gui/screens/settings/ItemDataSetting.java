package meteordevelopment.meteorclient.gui.screens.settings;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
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
import meteordevelopment.meteorclient.utils.misc.NbtUtils;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;
import net.minecraft.registry.Registries;
import org.jetbrains.annotations.Nullable;

public class ItemDataSetting<T extends ICopyable<T> & ISerializable<T> & IChangeable & IItemData<T>> extends Setting<Map<Item, T>> 
{
    public final IGetter<T> defaultData;

    public ItemDataSetting(String name, String description, Map<Item, T> defaultValue, Consumer<Map<Item, T>> onChanged, Consumer<Setting<Map<Item, T>>> onModuleActivated, IGetter<T> defaultData, IVisible visible)
    {
        super(name, description, defaultValue, onChanged, onModuleActivated, visible);
        this.defaultData = defaultData;
    }


    @Override
    public void resetImpl()
    {
        value = new HashMap<>(defaultValue);
    }

    @Override
    protected Map<Item, T> parseImpl(String str)
    {
        return new HashMap<>(0);
    }

    @Override
    protected boolean isValueValid(Map<Item, T> value)
    {
        return true;
    }

    @Override
    protected NbtCompound save(NbtCompound tag) {
        NbtCompound valueTag = new NbtCompound();
        for (Item item : get().keySet())
        {
            valueTag.put(Registries.ITEM.getRawId(item).toString(), get().get(item).toTag());
        }
        tag.put("value", valueTag);

        return tag;
    }

    @Override
    protected Map<Item, T> load(NbtCompound tag)
    {
        get().clear();

        NbtCompound valueTag = tag.getCompoundOrEmpty("value");
        for (String key : valueTag.getKeys())
        {
            get().put(Registries.ITEM.get(Identifier.of(key)), defaultData.get().copy().fromTag(valueTag.getCompoundOrEmpty(key)));
        }

        return get();
    }


    public static class Builder<T extends ICopyable<T> & ISerializable<T> & IChangeable & IItemData<T>> extends SettingBuilder<Builder<T>, Map<Item, T>, ItemDataSetting<T>>
    {
        private IGetter<T> defaultData;

        public Builder()
        {
            super(new HashMap<>(0));
        }

        public Builder<T> defaultData(IGetter<T> defaultData)
        {
            this.defaultData = defaultData;
            return this;
        }

        @Override
        public ItemDataSetting<T> build() {
            return new ItemDataSetting<>(name, description, defaultValue, onChanged, onModuleActivated, defaultData, visible);
        }
    }
}
