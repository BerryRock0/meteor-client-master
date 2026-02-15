/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.systems.modules.player;

import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.world.entity.projectile.FishingHook;

public class AutoFish extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Bite> biteMode = sgSettings.add(new EnumSetting.Builder<Bite>()
        .name("Bite mode")
        .description("Chose bite detection mode")
        .defaultValue(Bite.Sound)
        .build()
    );

    private final Setting<Integer> castValue = sgGeneral.add(new IntSetting.Builder()
        .name("cast-delay")
        .description("How long to wait between recasts if the bobber fails to land in water.")
        .defaultValue(0)
        .build()
    );

    private final Setting<Integer> catchValue = sgGeneral.add(new IntSetting.Builder()
        .name("catch-delay")
        .description("How long to wait after hooking a fish to reel it in.")
        .defaultValue(0)
        .build()
    );

    public boolean inUse;
    public int catchDelay;
    public int recastDelay;

    public AutoFish()
    {
        super(Categories.Player, "auto-fish", "Automatically fishes for you.");
    }

    @EventHandler
    public void main(PacketEvent.Receive event)
    {
        if (biteDetected(event))
            tryCatch();
        
        if (!inUse)
            tryCast();
    }

    public void tryCatch()
    {
        if (catchDelay != catchValue.get())
        {
            catchDelay--;
            return;   
        }
        catchDelay = catchValue.get();
        Utils.rightClick(); inUse = false;
    }

    public void tryCast()
    {
        if (castDelay != castValue.get())
        {
            castDelay--;
            return;   
        }
        castDelay = castValue.get();
        Utils.rightClick(); inUse = true;   
    }

    @EventHandler()
    private boolean biteDetected(PacketEvent.Receive event)
    {
        switch (biteMode.get())
        {
            case Sound -> {return processSoundUpdate(event);}
            case Entity -> {return processEntityUpdate(event);}
        }
    }

	private boolean processSoundUpdate(PacketEvent.Receive event)
	{	
		if(event.packet instanceof ClientboundSoundPacket sound)
		if(SoundEvents.FISHING_BOBBER_SPLASH.equals(sound.getSound().value()))
            return true;
        
		return false;
	}
	
	private boolean processEntityUpdate(PacketEvent.Receive event)
	{		

		if(event.packet instanceof ClientboundSetEntityDataPacket update)
		if(mc.level.getEntity(update.id()) instanceof FishingHook bobber)	
		if(bobber == mc.player.fishing)
            return true;
        
		
		return false;
	}

    public enum Bite
    {
        Sound, Entity   
    }
}
