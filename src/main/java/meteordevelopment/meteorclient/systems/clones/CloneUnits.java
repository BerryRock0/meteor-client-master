/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.systems.clones;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.game.GameJoinedEvent;
import meteordevelopment.meteorclient.events.game.GameLeftEvent;
import meteordevelopment.meteorclient.systems.System;
import meteordevelopment.meteorclient.systems.Systems;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.misc.NbtUtils;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.orbit.EventPriority;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import org.jetbrains.annotations.NotNull;

/**
 * Per-world storage of {@link CloneUnit}s - the clone equivalent of the worker units of
 * {@code WorkersModule}/{@code MinerPlacers}. Units are saved next to the other per-world data
 * and are loaded when a world is joined; the {@link Clones} module turns them into live
 * clones while it is enabled.
 */
public class CloneUnits extends System<CloneUnits> implements Iterable<CloneUnit>
{
    public final List<CloneUnit> units = new ArrayList<>();

    public CloneUnits()
    {
        super(null);
    }

    public static CloneUnits get()
    {
        return Systems.get(CloneUnits.class);
    }

    public void add(CloneUnit unit)
    {
        if (units.contains(unit))
        {
            save();
            return;
        }

        units.add(unit);
        save();
    }

    public void remove(CloneUnit unit)
    {
        boolean removed = units.remove(unit);
        if (removed)
			save();
    }

    public void removeAll(Collection<CloneUnit> c)
    {
        boolean removed = units.removeAll(c);
        if (removed)
			save();
    }

    public boolean isEmpty()
    {
        return units.isEmpty();
    }

    @EventHandler
    private void onGameJoined(GameJoinedEvent event)
    {
        load();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onGameDisconnected(GameLeftEvent event)
    {
        units.clear();
    }

    @Override
    public File getFile()
    {
        if (!Utils.canUpdate())
			return null;

        return new File(new File(MeteorClient.FOLDER, "clones"), Utils.getFileWorldName() + ".nbt");
    }

    @Override
    public @NotNull Iterator<CloneUnit> iterator()
    {
        return units.iterator();
    }

    @Override
    public CompoundTag toTag()
    {
        CompoundTag tag = new CompoundTag();
        tag.put("units", NbtUtils.listToTag(units));
        return tag;
    }

    @Override
    public CloneUnits fromTag(CompoundTag tag)
    {
        units.clear();

        for (Tag unitTag : tag.getListOrEmpty("units"))
            units.add(new CloneUnit(unitTag));

        return this;
    }
}
