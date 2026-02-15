/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.systems.modules.player;

import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.entity.projectile.FishingBobberEntity;

import meteordevelopment.meteorclient.mixin.FishingBobberEntityAccessor;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.world.TickRate;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.orbit.EventHandler;


public class AutoFish extends Module
{
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Double> range = sgGeneral.add(new DoubleSetting.Builder()
        .name("range")
        .description("The maximum range of fishing bobber detecting sound.")
        .defaultValue(0)
        .build()
    );

    private final Setting<Integer> castDelay = sgGeneral.add(new IntSetting.Builder()
        .name("cast-delay")
        .description("How long to wait between recasts if the bobber fails to land in water.")
        .defaultValue(0)
        .build()
    );

    private final Setting<Integer> castDelayVariance = sgGeneral.add(new IntSetting.Builder()
        .name("cast-delay-variance")
        .description("Maximum amount of randomness added to cast delay.")
        .defaultValue(0)
        .build()
    );

    private final Setting<Integer> catchDelay = sgGeneral.add(new IntSetting.Builder()
        .name("catch-delay")
        .description("How long to wait after hooking a fish to reel it in.")
        .defaultValue(0)
        .build()
    );

    private final Setting<Integer> catchDelayVariance = sgGeneral.add(new IntSetting.Builder()
        .name("catch-delay-variance")
        .description("Maximum amount of randomness added to catch delay.")
        .defaultValue(0)
        .build()
    );

    public double x,y,z;
    public boolean hooked;
    private double castDelayLeft = 0.0;
    private double catchDelayLeft = 0.0;
    
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

        tryCast();
        tryCatch();
    }

    private void tryCast()
    {
        if (castDelayLeft > 0)
        {
            castDelayLeft -= TickRate.INSTANCE.getTickRate() / 20.0;
            return;
        }

        useRod();
    }


    private void tryCatch()
    {
        if(mc.player.fishHook.squaredDistanceTo(x, y, z) <= range.get() || mc.player.fishHook.getHookedEntity() != null)
            useRod();
        
        if (!hooked)
        {
            if (((FishingBobberEntityAccessor) mc.player.fishHook).meteor$hasCaughtFish()) 
            {
                catchDelayLeft = randomizeDelay(catchDelay.get(), catchDelayVariance.get());
                hooked = true;
            }
            return;
        }

        if (catchDelayLeft > 0)
        {
            catchDelayLeft -= TickRate.INSTANCE.getTickRate() / 20.0;
            return;
        }

        useRod();
    }

    private double randomizeDelay(int delay, int variance)
    {
        if (variance == 0) return delay;

        // Sample the standard normal distribution via Box-Muller transform
        double scale = Math.sqrt(-2 * Math.log(Utils.random(0.0001, 1.0)));
        double angle = Math.TAU * Utils.random(0.0, 1.0);
        double norm = scale * Math.cos(angle);

        // Clamp to 3 standard deviations and re-scale to [-3.0, +3.0]
        final double MAX_SD = 3.0;
        norm = Math.clamp(norm, -MAX_SD, MAX_SD) / MAX_SD;

        delay += Math.round((float)(norm * variance));
        return Math.max(1, delay);
    }

    public void useRod()
    {
        Utils.rightClick();
        hooked = false;
        castDelayLeft = randomizeDelay(castDelay.get(), castDelayVariance.get());
    }
}
