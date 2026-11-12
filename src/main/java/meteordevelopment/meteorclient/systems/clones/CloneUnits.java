/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.systems.clones;

import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.systems.Units;
import meteordevelopment.meteorclient.systems.Systems;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.misc.NbtUtils;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import org.jetbrains.annotations.NotNull;

import java.io.File;

/**
 * Per-world storage of {@link CloneUnit}s - the clone equivalent of the worker units of
 * {@code WorkersModule}/{@code MinerPlacers}. Units are saved next to the other per-world data
 * and are loaded when a world is joined; the {@link meteordevelopment.meteorclient.systems.modules.player.Clones}
 * module turns them into live clones while it is enabled.
 */
public class CloneUnits extends Units<CloneUnit> {
    public CloneUnits() {
        super();
    }

    public static CloneUnits get() {
        return Systems.get(CloneUnits.class);
    }

    @Override
    public File getFile() {
        if (!Utils.canUpdate()) {
            return null;
        }

        return new File(new File(MeteorClient.FOLDER, "clones"), Utils.getFileWorldName() + ".nbt");
    }

    @Override
    public CompoundTag toTag() {
        CompoundTag tag = new CompoundTag();
        tag.put("units", NbtUtils.listToTag(units));
        return tag;
    }

    @Override
    public CloneUnits fromTag(CompoundTag tag) {
        units.clear();

        for (Tag unitTag : tag.getListOrEmpty("units")) {
            units.add(new CloneUnit(unitTag));
        }

        return this;
    }
}
