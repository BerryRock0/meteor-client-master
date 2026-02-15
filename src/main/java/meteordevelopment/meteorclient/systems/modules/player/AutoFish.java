/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.systems.modules.player;

import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;

import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.orbit.EventHandler;


public class AutoFish extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();


    private final Setting<Double> range = sgGeneral.add(new DoubleSetting.Builder()
        .name("range")
        .description("The maximum range of fishing bobber detecting sound.")
        .defaultValue(0)
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

    public double x,y,z;
    public int catchDelay;
    public int castDelay;

    public AutoFish()
    {
        super(Categories.Player, "auto-fish", "Automatically fishes for you.");
    }
    
    @EventHandler
    private void onReceivePacket(PacketEvent.Receive event)
    {
        if (mc.player.fishHook == null)
            return;
        
        if (event.packet instanceof PlaySoundS2CPacket soundPacket)
        {
            if(soundPacket.getSound().value().id().toString().equalsIgnoreCase("minecraft:entity.fishing_bobber.splash") || soundPacket.getSound().value().id().toString().equalsIgnoreCase("entity.fishing_bobber.splash"))
                x = soundPacket.getX(); y = soundPacket.getY(); z = soundPacket.getZ();
        }

        if (mc.player.fishHook.squaredDistanceTo(x, y, z) <= range.get() || mc.player.fishHook.getHookedEntity() != null)
        {
            tryCatch();
            tryCast();
        }
    }

    public void tryCatch()
    {
        if (catchDelay != catchValue.get())
        {
            catchDelay--;
            return;   
        }
        catchDelay = catchValue.get();
        Utils.rightClick();
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
    }
}
