/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.systems.modules.movement;

import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.mixin.ClientPlayerEntityAccessor;
import meteordevelopment.meteorclient.mixin.PlayerMoveC2SPacketAccessor;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.entity.EntityUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.s2c.play.PlayerAbilitiesS2CPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.util.math.Vec3d;

public class Flight extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final SettingGroup sgAntiKick = settings.createGroup("Anti Kick");
    private final SettingGroup sgTicks = settings.createGroup("Ticks");

    private final Setting<Mode> mode = sgGeneral.add(new EnumSetting.Builder<Mode>()
        .name("mode")
        .description("The mode for Flight.")
        .defaultValue(Mode.Abilities)
        .build()
    );

    private final Setting<Double> speed = sgGeneral.add(new DoubleSetting.Builder()
        .name("hoirozontal-speed")
        .description("Your speed when flying.")
        .build()
    );

    private final Setting<Double> verticalSpeed = sgGeneral.add(new DoubleSetting.Builder()
        .name("vertical-speed")
        .description("Your speed when flying.")
        .build()
    );

    public final Setting akEnable = sgAntiKick.add(new BoolSetting.Builder()
        .name("antikick")
        .description("update server boolean.")
        .defaultValue(false)
        .build());

    private final Setting<AntiKickMode> antiKickMode = sgAntiKick.add(new EnumSetting.Builder<AntiKickMode>()
        .name("antikick-mode")
        .description("The mode for anti kick.")
        .defaultValue(AntiKickMode.None)
        .build()
    );

    public final Setting ticksSinceLastPos = sgAntiKick.add(new IntSetting.Builder()
        .name("ticks-since-last-pos")
        .description("Set ticks since last pos.")
        .build()
    );
    
    public final Setting beginakdelay = sgAntiKick.add(new IntSetting.Builder()
        .name("begin-antikick-delay")
        .description("server timer begin value.")
        .build()
    );
    
    public final Setting endakdelay = sgAntiKick.add(new IntSetting.Builder()
        .name("end-antikick-delay")
        .description("server timer begin value.")
        .build()
    );

    public final Setting akincrement = sgAntiKick.add(new BoolSetting.Builder()
        .name("antikick-timer-increment")
        .description("increment server time value.")
        .defaultValue(false)
        .build()
    );
    public final Setting akdecrement = sgAntiKick.add(new BoolSetting.Builder()
        .name("antikick-timer-decrement")
        .description("decrement server time value.")
        .defaultValue(false)
        .build()
    );
    
    public final Setting akqueue = sgAntiKick.add(new BoolSetting.Builder()
        .name("antikick-timer-queue")
        .description("queue returning boolean value.")
        .defaultValue(false).build()
    );
    
    public final Setting akalways = sgAntiKick.add(new BoolSetting.Builder()
        .name("antikick-timer-always")
        .description("always returning boolean value.")
        .defaultValue(false)
        .build()
    );

   private final Setting<Boolean> pre = sgTicks.add(new BoolSetting.Builder()
        .name("Pre")
        .description("Load script before tick.")
        .defaultValue(false)
        .build()
    );
    private final Setting<Boolean> post = sgTicks.add(new BoolSetting.Builder()
        .name("Post")
        .description("Load script after tick.")
        .defaultValue(false)
        .build()
    );

    public int akDelay;

    
    public Flight()
    {
        super(Categories.Movement, "flight", "FLYYYY! No Fall is recommended with this module.");
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
        if(antiKickTimer())
        switch (antiKickMode.get())
        {
            case None -> {}    
            case Normal -> {mc.player.getAbilities().flying = false; mc.player.getAbilities().allowFlying = false;}
        }

        switch (mode.get())
        {
            case Abilities -> 
            {
                if (mc.player.isSpectator() || mc.player.getAbilities().creativeMode)
                     return;
                mc.player.getAbilities().flying = true; 
                mc.player.getAbilities().allowFlying = true;
                mc.player.getAbilities().setFlySpeed(speed.get().floatValue());
            }
            case Velocity ->
            {
                Vec3d playerVelocity = mc.player.getVelocity();
                if (mc.options.jumpKey.isPressed())
                    playerVelocity = playerVelocity.add(0, verticalSpeed.get(), 0);
                if (mc.options.sneakKey.isPressed())
                    playerVelocity = playerVelocity.subtract(0, verticalSpeed.get(), 0);
                mc.player.setVelocity(0, 0, 0);
                mc.player.setVelocity(playerVelocity);
            }   
        }
    }

    public boolean antiKickTimer()
    {
        if (akDelay != (int)endakdelay.get() && akEnable.get().booleanValue())
        {
            if ((Boolean)akincrement.get()) akDelay++;
            if ((Boolean)akdecrement.get()) akDelay--;
            return (Boolean)akqueue.get();
        }
        akDelay = (Integer)beginakdelay.get();
        return (Boolean)akalways.get();
    }

    public enum Mode
    {
        Abilities,
        Velocity
    }

    public enum AntiKickMode
    {
        None,
        Normal
    }
}
