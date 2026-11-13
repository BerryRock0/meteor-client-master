/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.systems;

import meteordevelopment.meteorclient.events.game.GameJoinedEvent;
import meteordevelopment.meteorclient.events.game.GameLeftEvent;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.orbit.EventPriority;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Template for per-world storages of configurable units (miner placers, clones, workers, etc.).
 * <p>
 * Keeps the generic list handling (add/remove/removeAll/isEmpty/iteration), the load on world
 * join and clear on world leave, while concrete subclasses provide the unit type, the storage
 * file, the NBT tag key and the unit creation from NBT.
 *
 * @param <T> type of the stored units
 */
public abstract class Units<T> extends System<Units<T>> implements Iterable<T>
{
    public final List<T> units = new ArrayList<>();

    public Units()
    {
        super(null);
    }

    public void add(T unit)
    {
        if (!units.contains(unit))
			units.add(unit);

        save();
    }

    public void remove(T unit)
    {   
        if (units.contains(unit))
			units.remove(unit);
        
        save();
    }

    public void removeAll(Collection<T> c)
    {
        if (units.containsAll(c))
			units.removeAll(c);

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

    public abstract File getFile();

    public abstract CompoundTag toTag();

    public abstract Units<T> fromTag(CompoundTag tag);

    @Override
    public @NotNull Iterator<T> iterator()
    {
        return new UnitsIterator();
    }

    private final class UnitsIterator implements Iterator<T>
    {
        private final Iterator<T> it = units.iterator();

        @Override
        public boolean hasNext()
        {
            return it.hasNext();
        }

        @Override
        public T next()
        {
            return it.next();
        }

        @Override
        public void remove()
        {
            it.remove();
            save();
        }
    }
}
