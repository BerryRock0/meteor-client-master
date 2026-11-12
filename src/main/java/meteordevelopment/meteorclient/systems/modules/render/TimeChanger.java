/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.systems.modules.render;

import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.protocol.game.ClientboundSetTimePacket;

public class TimeChanger extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Double> time = sgGeneral.add(new DoubleSetting.Builder()
        .name("time")
        .description("The specified time to be set.")
        .defaultValue(0)
        .sliderRange(-20000, 20000)
        .build()
    );

    private final Setting<Double> timeScale = sgGeneral.add(new DoubleSetting.Builder()
        .name("time-scale")
        .description("Multiplier of the world time. Values below 1 slow down the world time, values above 1 speed it up.")
        .defaultValue(1)
        .range(0, 10)
        .build()
    );

    long oldTime;
    private long lastTime;

    public TimeChanger() {
        super(Categories.Render, "time-changer", "Makes you able to set a custom time.");
    }

    @Override
    public void onActivate() {
        oldTime = mc.level.getGameTime();
        lastTime = mc.level.getGameTime();
    }

    @Override
    public void onDeactivate() {
        mc.level.getLevelData().setGameTime(oldTime);
    }

    @EventHandler
    private void onPacketReceive(PacketEvent.Receive event) {
        if (event.packet instanceof ClientboundSetTimePacket packet) {
            oldTime = packet.gameTime();
            event.cancel();
        }
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (timeScale.get() == 1) {
            mc.level.getLevelData().setGameTime(time.get().longValue());
        } else {
            long currentTime = mc.level.getGameTime();
            long delta = currentTime - lastTime;
            lastTime = currentTime;
            mc.level.getLevelData().setGameTime(lastTime + Math.round(delta * timeScale.get()));
        }
    }
}
