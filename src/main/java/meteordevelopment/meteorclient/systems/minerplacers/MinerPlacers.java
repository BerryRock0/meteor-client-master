
package meteordevelopment.meteorclient.systems.minerplacers;

import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.game.GameJoinedEvent;
import meteordevelopment.meteorclient.events.game.GameLeftEvent;
import meteordevelopment.meteorclient.systems.System;
import meteordevelopment.meteorclient.systems.Systems;
import meteordevelopment.meteorclient.systems.minerplacers.events.MinerPlacerAddedEvent;
import meteordevelopment.meteorclient.systems.minerplacers.events.MinerPlacerRemovedEvent;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.files.StreamUtils;
import meteordevelopment.meteorclient.utils.misc.NbtUtils;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.orbit.EventPriority;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.CompoundTag;
import org.apache.commons.lang3.Strings;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class MinerPlacers extends System<MinerPlacers> implements Iterable<MinerPlacer>
{	
    public final List<MinerPlacer> minerPlacers = new ArrayList<>();
	
	public MinerPlacers()
	{
		super(null);
	}
		
	public static MinerPlacers get()
	{
        return Systems.get(MinerPlacers.class);
    }
    
	public boolean add(MinerPlacer minerPlacer)
	{
        if (minerPlacers.contains(minerPlacer))
        {
            save();
            return true;
        }

        minerPlacers.add(minerPlacer);
        save();

        MeteorClient.EVENT_BUS.post(new MinerPlacerAddedEvent(minerPlacer));

		return false;
    }
    
    public boolean remove(MinerPlacer minerPlacer)
    {
        boolean removed = minerPlacers.remove(minerPlacer);
        if (removed) {
            save();
            MeteorClient.EVENT_BUS.post(new MinerPlacerRemovedEvent(minerPlacer));
        }

        return removed;
    }
    
    public void removeAll(Collection<MinerPlacer> c) {
        boolean removed = minerPlacers.removeAll(c);
        if (removed) save();
    }

    public MinerPlacer get(String name)
    {
        for (MinerPlacer minerPlacer : minerPlacers)
        {
            if (minerPlacer.name.get().equalsIgnoreCase(name))
				return minerPlacer;
        }

        return null;
    }
    
    @EventHandler
    private void onGameJoined(GameJoinedEvent event)
    {
        load();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onGameDisconnected(GameLeftEvent event)
    {
        minerPlacers.clear();
    }

    @Override
    public File getFile()
    {
        if (!Utils.canUpdate()) return null;
        return new File(new File(MeteorClient.FOLDER, "minerplacers"), Utils.getFileWorldName() + ".nbt");
    }

    public boolean isEmpty() {
        return minerPlacers.isEmpty();
    }

    @Override
    public @NotNull Iterator<MinerPlacer> iterator() {
        return new MinerPlacerIterator();
    }

    @Override
    public NbtCompound toTag()
    {
        CompoundTag tag = new CompoundTag();
        tag.put("minerplacers", NbtUtils.listToTag(minerPlacers));
        return tag;
    }

    @Override
    public MinerPlacers fromTag(CompoundTag tag)
    {
        minerPlacers.clear();

        for (Tag minerPlacerTag : tag.getListOrEmpty("minerplacers"))
        {
            minerPlacers.add(new MinerPlacer(minerPlacerTag));
        }

        return this;
    }

    private final class MinerPlacerIterator implements Iterator<MinerPlacer>
    {
        private final Iterator<MinerPlacer> it = minerPlacers.iterator();

        @Override
        public boolean hasNext()
        {
            return it.hasNext();
        }

        @Override
        public MinerPlacer next()
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
