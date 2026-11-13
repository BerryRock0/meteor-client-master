package meteordevelopment.meteorclient.systems.templates.minerplacers;

import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.systems.Units;
import meteordevelopment.meteorclient.systems.Systems;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.misc.NbtUtils;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class MinerPlacers extends Units<MinerPlacer>
{
    public MinerPlacers()
    {
        super();
    }

    public static MinerPlacers get()
    {
        return Systems.get(MinerPlacers.class);
    }

    @Override
    public File getFile()
    {
        if (!Utils.canUpdate())
            return null;

        return new File(new File(MeteorClient.FOLDER, "minerplacers"), Utils.getFileWorldName() + ".nbt");
    }

    @Override
    public CompoundTag toTag()
    {
        CompoundTag tag = new CompoundTag();
        tag.put("minerplacers", NbtUtils.listToTag(units));
        return tag;
    }

    @Override
    public MinerPlacers fromTag(CompoundTag tag)
    {
        units.clear();

        for (Tag minerPlacerTag : tag.getListOrEmpty("minerplacers"))
            units.add(new MinerPlacer(minerPlacerTag));

        return this;
    }
}
