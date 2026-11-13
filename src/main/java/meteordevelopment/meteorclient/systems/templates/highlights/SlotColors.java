package meteordevelopment.meteorclient.systems.templates.highlights;

import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.systems.Units;
import meteordevelopment.meteorclient.systems.Systems;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.misc.NbtUtils;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class SlotColors extends Units<SlotColor>
{
    public SlotColors()
    {
        super();
    }

    public static SlotColors get()
    {
        return Systems.get(SlotColors.class);
    }

    @Override
    public File getFile()
    {
        if (!Utils.canUpdate())
            return null;

        return new File(new File(MeteorClient.FOLDER, "slotcolors"), Utils.getFileWorldName() + ".nbt");
    }

    @Override
    public CompoundTag toTag()
    {
        CompoundTag tag = new CompoundTag();
        tag.put("slotcolors", NbtUtils.listToTag(units));
        return tag;
    }

    @Override
    public SlotColors fromTag(CompoundTag tag)
    {
        units.clear();

        for (Tag slotColorTag : tag.getListOrEmpty("slotcolors"))
            units.add(new SlotColor(slotColorTag));

        return this;
    }
}
