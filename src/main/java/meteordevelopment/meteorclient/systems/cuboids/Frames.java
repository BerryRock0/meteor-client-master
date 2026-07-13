package meteordevelopment.meteorclient.systems.cuboids;

import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.game.GameJoinedEvent;
import meteordevelopment.meteorclient.events.game.GameLeftEvent;
import meteordevelopment.meteorclient.systems.System;
import meteordevelopment.meteorclient.systems.Systems;
import meteordevelopment.meteorclient.systems.frames.events.FrameAddedEvent;
import meteordevelopment.meteorclient.systems.frames.events.FrameRemovedEvent;
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

public class Frames extends System<Frames> implements Iterable<Frame>
{	
    public final List<Frame> frames = new ArrayList<>();
	
	public Frames()
	{
		super(null);
	}
		
	public static Frames get()
	{
        return Systems.get(Frames.class);
    }
    
	public boolean add(Frame frame)
	{
        if (frames.contains(frame))
        {
            save();
            return true;
        }

        frames.add(frame);
        save();

        MeteorClient.EVENT_BUS.post(new FrameAddedEvent(frame));

		return false;
    }
    
    public boolean remove(Frame frame)
    {
        boolean removed = frames.remove(frame);
        if (removed) {
            save();
            MeteorClient.EVENT_BUS.post(new MinerPlacerRemovedEvent(frame));
        }

        return removed;
    }
    
    public void removeAll(Collection<Frame> c) {
        boolean removed = frames.removeAll(c);
        if (removed) save();
    }
    
    @EventHandler
    private void onGameJoined(GameJoinedEvent event)
    {
        load();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onGameDisconnected(GameLeftEvent event)
    {
        frames.clear();
    }

    @Override
    public File getFile()
    {
        if (!Utils.canUpdate()) return null;
        return new File(new File(MeteorClient.FOLDER, "frames"), Utils.getFileWorldName() + ".nbt");
    }

    public boolean isEmpty() {
        return frames.isEmpty();
    }

    @Override
    public @NotNull Iterator<Frame> iterator() {
        return new FrameIterator();
    }

    @Override
    public CompoundTag toTag()
    {
        CompoundTag tag = new CompoundTag();
        tag.put("frames", NbtUtils.listToTag(frames));
        return tag;
    }

    @Override
    public Frames fromTag(CompoundTag tag)
    {
        frames.clear();

        for (Tag frameTag : tag.getListOrEmpty("frames"))
        {
            frames.add(new Frame(minerPlacerTag));
        }

        return this;
    }

    private final class FrameIterator implements Iterator<Frame>
    {
        private final Iterator<Frame> it = frames.iterator();

        @Override
        public boolean hasNext()
        {
            return it.hasNext();
        }

        @Override
        public Frame next()
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
