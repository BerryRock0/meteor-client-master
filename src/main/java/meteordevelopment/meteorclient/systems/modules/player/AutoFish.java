/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.systems.modules.player;

import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;

import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.orbit.EventHandler;


public class AutoFish extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Bite> biteMode = sgGeneral.add(new EnumSetting.Builder<Bite>()
        .name("Bite mode")
        .description("Chose bite detection mode")
        .defaultValue(Bite.Sound)
        .build()
    );
    
    private final Setting<Boolean> pre = sgGeneral.add(new BoolSetting.Builder()
        .name("Pre")
        .description("Load script before tick.")
        .defaultValue(false)
        .build()
    );
    private final Setting<Boolean> post = sgGeneral.add(new BoolSetting.Builder()
        .name("Post")
        .description("Load script after tick.")
        .defaultValue(false)
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
    private void onPreTick(TickEvent.Pre event)
    {
        if (pre.get())
            main();
    }

    
    @EventHandler
    private void onPostTick(TickEvent.Post event)
    {
        if (post.get())
            main();
    }
    public void main()
    {   
        if (biteDetected())
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
        Utils.rightClick();
        inUse = true;   
    }

    private boolean biteDetected()
    {
        switch (biteMode.get())
        {
            case Sound -> {return SoundEvent.id().equals(SoundEvents.ENTITY_FISHING_BOBBER_SPLASH);}
            case Entity -> {return mc.player.fishHook.getHookedEntity() != null;}
        }
        return false;
    }	

    public enum Bite
    {
        Sound, Entity   
    }
}
