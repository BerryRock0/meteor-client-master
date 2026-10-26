/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.systems.modules.player;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.gui.widgets.containers.WTable;
import meteordevelopment.meteorclient.gui.widgets.pressable.WButton;
import meteordevelopment.meteorclient.gui.widgets.pressable.WMinus;
import meteordevelopment.meteorclient.mc.Vanilla;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.friends.Friends;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.render.Xray;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.entity.fakeplayer.FakePlayerEntity;
import meteordevelopment.meteorclient.utils.entity.fakeplayer.FakePlayerManager;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.meteorclient.utils.player.Rotations;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class FakePlayer extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    public final Setting<String> name = sgGeneral.add(new StringSetting.Builder()
        .name("name")
        .description("The name of the fake player.")
        .defaultValue("seasnail8169")
        .build()
    );

    public final Setting<Boolean> copyInv = sgGeneral.add(new BoolSetting.Builder()
        .name("copy-inv")
        .description("Copies your inventory to the fake player.")
        .defaultValue(true)
        .build()
    );

    public final Setting<Integer> health = sgGeneral.add(new IntSetting.Builder()
        .name("health")
        .description("The fake player's default health.")
        .defaultValue(20)
        .min(1)
        .sliderRange(1, 100)
        .build()
    );

    public final Setting<Behaviour> behaviour = sgGeneral.add(new EnumSetting.Builder<Behaviour>()
        .name("behaviour")
        .description("How the fake player behaves. Stasis keeps it motionless; Follow and Wander drive client-side movement.")
        .defaultValue(Behaviour.Stasis)
        .build()
    );

    public final Setting<Double> followDistance = sgGeneral.add(new DoubleSetting.Builder()
        .name("follow-distance")
        .description("How close the fake player follows you before stopping.")
        .defaultValue(2)
        .min(0.5)
        .sliderRange(0.5, 16)
        .visible(() -> behaviour.get() == Behaviour.Follow)
        .build()
    );

    public final Setting<Double> wanderRadius = sgGeneral.add(new DoubleSetting.Builder()
        .name("wander-radius")
        .description("How far from its current position the fake player wanders.")
        .defaultValue(10)
        .min(1)
        .sliderRange(1, 32)
        .visible(() -> behaviour.get() == Behaviour.Wander)
        .build()
    );

    public final Setting<Boolean> sprint = sgGeneral.add(new BoolSetting.Builder()
        .name("sprint")
        .description("Makes the fake player sprint while moving.")
        .defaultValue(false)
        .visible(() -> behaviour.get() != Behaviour.Stasis)
        .build()
    );

    public final Setting<Boolean> swing = sgGeneral.add(new BoolSetting.Builder()
        .name("swing")
        .description("Makes the fake player swing its arm while moving.")
        .defaultValue(true)
        .visible(() -> behaviour.get() != Behaviour.Stasis)
        .build()
    );

    public final Setting<Double> range = sgGeneral.add(new DoubleSetting.Builder()
        .name("range")
        .description("How far to look for mining or combat targets.")
        .defaultValue(6)
        .min(1)
        .sliderRange(1, 16)
        .visible(() -> behaviour.get() == Behaviour.Mine || behaviour.get() == Behaviour.Fight)
        .build()
    );

    public final Setting<Boolean> real = sgGeneral.add(new BoolSetting.Builder()
        .name("real")
        .description("Also performs the action for real through your own player (actual blocks broken / damage dealt), moving you into reach.")
        .defaultValue(false)
        .visible(() -> behaviour.get() == Behaviour.Mine || behaviour.get() == Behaviour.Fight)
        .build()
    );

    private WTable table;
    private BlockPos minePos;
    /** Whether the autopilot currently holds the forward key, so we never cancel the user's own input. */
    private boolean moving;
    /** Whether the autopilot currently holds the jump key. */
    private boolean jumping;

    public FakePlayer() {
        super(Categories.Player, "fake-player", "Spawns a client-side fake player for testing usages. No need to be active.");
    }

    public enum Behaviour {
        Stasis,
        Follow,
        Wander,
        Mine,
        Fight
    }

    @Override
    public void onDeactivate() {
        releaseKeys();
        minePos = null;
        FakePlayerManager.clear();
        if (table != null) table.clear();
    }

    /** Real (non-simulated) mining/attack autopilot for your own player. Only runs while active. */
    @EventHandler
    private void onTick(TickEvent.Pre event) {
        if (!Utils.canUpdate() || !real.get()) {
            releaseKeys();
            minePos = null;
            return;
        }

        switch (behaviour.get()) {
            case Mine -> tickMine();
            case Fight -> tickFight();
            default -> {
                releaseKeys();
                minePos = null;
            }
        }
    }

    private void tickMine() {
        // Drop the current target once it has been broken.
        if (minePos != null && !Xray.ORES.contains(mc.level.getBlockState(minePos).getBlock())) minePos = null;
        if (minePos == null) minePos = findNearestResource(mc.player.position(), range.get());

        if (minePos == null) {
            releaseKeys();
            return;
        }

        boolean reached = PlayerUtils.isWithinReach(minePos);
        faceAndMove(minePos.getX() + 0.5, minePos.getY() + 0.5, minePos.getZ() + 0.5, reached);

        if (reached) {
            BlockUtils.breakBlock(minePos, Direction.UP, InteractionHand.MAIN_HAND, true);
            if (!Xray.ORES.contains(mc.level.getBlockState(minePos).getBlock())) minePos = null;
        }
    }

    private void tickFight() {
        LivingEntity target = findNearestEnemy(mc.player.position(), range.get());

        if (target == null) {
            releaseKeys();
            return;
        }

        boolean reached = PlayerUtils.isWithin(target, mc.player.entityInteractionRange());
        faceAndMove(target.getX(), target.getY() + target.getBbHeight() / 2, target.getZ(), reached);

        if (reached && mc.player.getAttackStrengthScale(0) >= 1) {
            mc.gameMode.attack(mc.player, target);
            Vanilla.swing(mc.player, InteractionHand.MAIN_HAND);
        }
    }

    /** Faces your player at (x, y, z) and walks toward it, stopping when {@code stop} is true. */
    private void faceAndMove(double x, double y, double z, boolean stop) {
        Vec3 target = new Vec3(x, y, z);
        mc.player.setYRot((float) Rotations.getYaw(target));
        mc.player.setXRot((float) Rotations.getPitch(target));

        driveKeys(!stop, !stop && mc.player.horizontalCollision);
    }

    /**
     * Drives the forward/jump keys for the autopilot. The keys are only touched while the
     * autopilot actually needs them: while driving, the forward key is re-asserted every tick
     * (like {@code PathFinder} does), and when it stops, it is released exactly once. This way
     * the key is never force-cleared on ticks where the autopilot is idle, so manual movement
     * keeps working in every mode.
     */
    private void driveKeys(boolean move, boolean jump) {
        if (mc.options == null) return;

        if (move) {
            if (!mc.options.keyUp.isDown()) mc.options.keyUp.setDown(true);
        } else if (moving && mc.options.keyUp.isDown()) {
            mc.options.keyUp.setDown(false);
        }
        moving = move;

        if (jump) {
            if (!mc.options.keyJump.isDown()) mc.options.keyJump.setDown(true);
        } else if (jumping && mc.options.keyJump.isDown()) {
            mc.options.keyJump.setDown(false);
        }
        jumping = jump;
    }

    /** Releases the keys only if this module pressed them itself. */
    private void releaseKeys() {
        driveKeys(false, false);
    }

    /** Nearest resource block within {@code range} of {@code origin}, or null. */
    public BlockPos findNearestResource(Vec3 origin, double range) {
        if (mc.level == null) return null;

        BlockPos best = null;
        double bestDist = range * range;
        int r = (int) Math.ceil(range);
        int ox = Mth.floor(origin.x()), oy = Mth.floor(origin.y()), oz = Mth.floor(origin.z());

        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int x = ox - r; x <= ox + r; x++) {
            for (int y = oy - r; y <= oy + r; y++) {
                for (int z = oz - r; z <= oz + r; z++) {
                    pos.set(x, y, z);
                    if (!Xray.ORES.contains(mc.level.getBlockState(pos).getBlock())) continue;

                    double d = origin.distanceToSqr(x + 0.5, y + 0.5, z + 0.5);
                    if (d <= bestDist) {
                        bestDist = d;
                        best = pos.immutable();
                    }
                }
            }
        }

        return best;
    }

    /** Nearest attackable living entity within {@code range} of {@code origin}, or null. */
    public LivingEntity findNearestEnemy(Vec3 origin, double range) {
        if (mc.level == null) return null;

        LivingEntity best = null;
        double bestDist = range * range;

        for (Entity e : mc.level.entitiesForRendering()) {
            if (!(e instanceof LivingEntity le)) continue;
            if (!le.isAlive() || le.isDeadOrDying()) continue;
            if (le == mc.player) continue;
            // Our own bots are client-side dummies: attacking them is meaningless.
            if (le instanceof FakePlayerEntity) continue;

            if (le instanceof Player p) {
                if (p.isCreative() || p.isSpectator()) continue;
                if (!Friends.get().shouldAttack(p)) continue;
            }

            double d = origin.distanceToSqr(le.position());
            if (d <= bestDist) {
                bestDist = d;
                best = le;
            }
        }

        return best;
    }

    @Override
    public WWidget getWidget(GuiTheme theme) {
        table = theme.table();
        fillTable(theme, table);

        return table;
    }

    private void fillTable(GuiTheme theme, WTable table) {
        for (FakePlayerEntity fakePlayer : FakePlayerManager.getFakePlayers()) {
            table.add(theme.label(fakePlayer.getName().getString()));
            WMinus delete = table.add(theme.minus()).expandCellX().right().widget();
            delete.action = () -> {
                FakePlayerManager.remove(fakePlayer);
                table.clear();
                fillTable(theme, table);
            };
            table.row();
        }

        WButton spawn = table.add(theme.button("Spawn")).expandCellX().right().widget();
        spawn.action = () -> {
            FakePlayerManager.add(name.get(), health.get(), copyInv.get());
            table.clear();
            fillTable(theme, table);
        };

        WButton clear = table.add(theme.button("Clear All")).right().widget();
        clear.action = () -> {
            FakePlayerManager.clear();
            table.clear();
            fillTable(theme, table);
        };
    }

    @Override
    public String getInfoString() {
        return String.valueOf(FakePlayerManager.count());
    }
}
