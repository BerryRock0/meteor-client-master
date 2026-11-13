package meteordevelopment.meteorclient.systems.templates.highlights;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.utils.misc.ISerializable;
import meteordevelopment.meteorclient.systems.modules.player.Clones;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.Item;

public class SlotColor implements ISerializable<SlotColor>
{
	public final Settings settings = new Settings();
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
        .defaultValue(new SettingColor(0, 0, 0, 0))
        .build()
    );

	public final UUID uuid;
	
	private SlotColor()
    {
        uuid = UUID.randomUUID();
    }

    public SlotColor(Tag tag)
    {
	    CompoundTag nbt = (CompoundTag) tag;
        uuid = nbt.read("uuid", UUIDUtil.CODEC).orElse(UUID.randomUUID());
        fromTag(nbt);
	}

    public static class Builder
	{
        public SlotColor build()
        {
            return new SlotColor();
        }
    }

    @Override
    public CompoundTag toTag()
    {
        CompoundTag tag = new CompoundTag();

        tag.store("uuid", UUIDUtil.CODEC, uuid);
        tag.put("settings", settings.toTag());

        return tag;
    }

    @Override
    public SlotColor fromTag(CompoundTag tag)
    {
        if (tag.contains("settings"))
            settings.fromTag(tag.getCompoundOrEmpty("settings"));

        return this;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
			return true;

        if (o == null || getClass() != o.getClass())
			return false;
        
        SlotColor that = (SlotColor) o;

        return Objects.equals(uuid, that.uuid);
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode(uuid);
    }
}
