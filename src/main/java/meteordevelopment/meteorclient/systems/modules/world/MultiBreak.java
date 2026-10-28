/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.systems.modules.world;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.mc.Vanilla;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.render.Xray;
import meteordevelopment.meteorclient.utils.world.BlockIterator;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Breaks several blocks at once: every tick the start/stop digging packet pair is sent for
 * every matching block in range in one go, so the server digs them all simultaneously instead
 * of one after another. Blocks your current tool can insta-mine (and every block in creative)
 * therefore fall together as a whole area; harder blocks are still limited by the server's own
 * dig-speed check.
 */
public class MultiBreak extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Double> range = sgGeneral.add(new DoubleSetting.Builder()
        .name("range")
        .description("The break range.")
        .defaultValue(4.5)
        .min(1)
        .sliderMax(6)
        .build()
    );

    private final Setting<Integer> maxBlocksPerTick = sgGeneral.add(new IntSetting.Builder()
        .name("max-blocks-per-tick")
        .description("Maximum number of blocks to break per tick.")
        .defaultValue(8)
        .min(1)
        .sliderMax(64)
        .build()
    );

    private final Setting<Mode> mode = sgGeneral.add(new EnumSetting.Builder<Mode>()
        .name("mode")
        .description("Which blocks to break.")
        .defaultValue(Mode.All)
        .build()
    );

    private final Setting<Boolean> swing = sgGeneral.add(new BoolSetting.Builder()
        .name("swing")
        .description("Swings your hand while breaking.")
        .defaultValue(true)
        .build()
    );

    private final List<BlockPos> blocks = new ArrayList<>();

    public MultiBreak() {
        super(Categories.World, "multi-break", "Breaks multiple blocks at once around you.");
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        if (mc.player == null || mc.level == null) return;

        double px = mc.player.getX(), py = mc.player.getY(), pz = mc.player.getZ();
        double rangeSq = range.get() * range.get();

        blocks.clear();

        BlockIterator.register((int) Math.ceil(range.get()), (int) Math.ceil(range.get()), (pos, state) -> {
            if (Vec3.atCenterOf(pos).distanceToSqr(px, py, pz) > rangeSq) return;
            if (mode.get() == Mode.Ores && !Xray.ORES.contains(state.getBlock())) return;
            if (!BlockUtils.canBreak(pos, state)) return;

            blocks.add(pos.immutable());
        });

        BlockIterator.after(() -> {
            if (blocks.isEmpty()) return;

            // Closest first, so the cap below always keeps the blocks nearest to you.
            blocks.sort(Comparator.comparingDouble(pos -> Vec3.atCenterOf(pos).distanceToSqr(px, py, pz)));

            int count = 0;
            for (BlockPos pos : blocks) {
                if (count >= maxBlocksPerTick.get()) break;

                Direction direction = BlockUtils.getDirection(pos);

                // The start/stop pair is what makes the server dig the block right now: with a
                // suitable tool (or in creative) it breaks instantly, all selected blocks at once.
                mc.gameMode.startPrediction(mc.level, sequence -> new ServerboundPlayerActionPacket(ServerboundPlayerActionPacket.Action.START_DESTROY_BLOCK, pos, direction, sequence));
                mc.gameMode.startPrediction(mc.level, sequence -> new ServerboundPlayerActionPacket(ServerboundPlayerActionPacket.Action.STOP_DESTROY_BLOCK, pos, direction, sequence));

                count++;
            }

            if (swing.get()) Vanilla.swing(mc.player, InteractionHand.MAIN_HAND);

            blocks.clear();
        });
    }

    public enum Mode {
        All,
        Ores
    }
}
