/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.utils.entity.fakeplayer;

import com.mojang.authlib.GameProfile;
import meteordevelopment.meteorclient.mc.Vanilla;
import meteordevelopment.meteorclient.mixin.AbstractClientPlayerAccessor;
import meteordevelopment.meteorclient.mixin.EntityAccessor;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.player.FakePlayer;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.player.RemotePlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

import java.util.UUID;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class FakePlayerEntity extends RemotePlayer {
    /**
     * Disables entity push with this fake player
     */
    public boolean doNotPush;
    /**
     * Stops rendering the fake player when you are inside it
     */
    public boolean hideWhenInsideCamera;
    /**
     * Prevents you from interacting with the fake player; will also prevent TargetUtils selecting it as a target
     */
    public boolean noHit;

    /**
     * When true, this fake player drives its own movement and physics every tick (walking, jumping,
     * gravity) based on the {@link FakePlayer} module's behaviour setting. Only the manager-spawned
     * fake players set this; decorative ones (PopChams ghosts, Blink models) leave it off so they stay
     * exactly as before.
     */
    public boolean aiControlled;

    /**
     * When set, this clone also exists as a real player on the integrated server (singleplayer).
     * The pilot logic below keeps running client-side and the bridge applies its outcomes for
     * real: blocks broken through the clone's own game mode, damage through its own attacks.
     * Null on multiplayer servers, where a server-side player is impossible without a second
     * account and the clone stays a client-side imitation.
     */
    public ServerClone server;

    /** Cooldown (in ticks) between automatic jumps, so the player does not spam the jump. */
    private int jumpCooldown;
    /** Current wander target, only used in {@link Behaviour.Wander}. */
    private Vec3 wanderTarget;
    /** Ticks left before a new wander target is picked. */
    private int wanderTimer;
    /** Mirror mode anchors: your position and the clone's position when mirroring started. */
    private Vec3 mirrorPlayerOrigin;
    private Vec3 mirrorCloneOrigin;
    /** Whether the local player was swinging on the previous tick (edge detection). */
    private boolean mirrorWasSwinging;

    public FakePlayerEntity(Player player, String name, float health, boolean copyInv) {
        super(mc.level, new GameProfile(UUID.randomUUID(), name));

        copyPosition(player);

        yRotO = getYRot();
        xRotO = getXRot();
        yHeadRot = player.yHeadRot;
        yHeadRotO = yHeadRot;
        yBodyRot = player.yBodyRot;
        yBodyRotO = yBodyRot;

        getAttributes().assignAllValues(player.getAttributes());
        setPose(player.getPose());

        if (health <= 20) {
            setHealth(health);
        } else {
            setHealth(health);
            setAbsorptionAmount(health - 20);
        }

        if (copyInv) getInventory().replaceWith(player.getInventory());
    }

    @Override
    public int getId() {
        return ((EntityAccessor) this).meteor$getId();
    }

    public void spawn() {
        // Singleplayer clones are registered by the integrated server instead of the client
        // level - adding the pilot here too would render the clone twice.
        if (server != null) return;

        unsetRemoved();
        mc.level.addEntity(this);
    }

    public void despawn() {
        if (server != null) {
            server.despawn();
            server = null;
            return;
        }

        if (mc.level != null) mc.level.removeEntity(getId(), RemovalReason.DISCARDED);
        setRemoved(RemovalReason.DISCARDED);
    }

    @Nullable
    @Override
    protected PlayerInfo getPlayerInfo() {
        PlayerInfo entry = super.getPlayerInfo();

        if (entry == null) {
            ((AbstractClientPlayerAccessor) this).meteor$setPlayerInfo(mc.getConnection().getPlayerInfo(mc.player.getUUID()));
        }

        return entry;
    }

    @Override
    public void aiStep() {
        // RemotePlayer.aiStep() only applies network interpolation and never calls the vanilla
        // Player.aiStep(), so the fake player would otherwise be a frozen statue. We drive movement
        // ourselves below, but only for the manager-spawned players; decorative subclasses
        // (PopChams ghosts, Blink models) leave aiControlled off and keep their old behaviour.
        super.aiStep();

        if (aiControlled && mc.player != null) driveBehaviour();
    }

    private void driveBehaviour() {
        FakePlayer module = Modules.get().get(FakePlayer.class);
        if (module == null) return;

        // Stasis keeps the original statue behaviour: no physics, no movement.
        if (module.behaviour.get() == FakePlayer.Behaviour.Stasis) return;

        // Mirror has its own control loop: it copies your inputs instead of seeking targets.
        if (module.behaviour.get() == FakePlayer.Behaviour.Mirror) {
            driveMirror(module);
            return;
        }

        // Target point the bot is interested in this tick, plus whether it should walk there or
        // stand and act on the target once close.
        double tx = getX(), ty = getEyeY(), tz = getZ();
        boolean moving = false;
        boolean acting = false;
        // The ore the bot is currently standing at, if any - broken for real by a server clone.
        BlockPos actingOre = null;
        // The enemy the bot is currently standing at, if any - attacked for real by a server clone.
        LivingEntity actingTarget = null;

        switch (module.behaviour.get()) {
            case Follow -> {
                double dx = mc.player.getX() - getX();
                double dz = mc.player.getZ() - getZ();
                double stopSq = module.followDistance.get() * module.followDistance.get();

                tx = mc.player.getX();
                ty = mc.player.getEyeY();
                tz = mc.player.getZ();

                if (dx * dx + dz * dz > stopSq) moving = true;
            }
            case Wander -> {
                if (wanderTarget == null || wanderTimer <= 0 || position().distanceToSqr(wanderTarget) < 1.0) {
                    double r = module.wanderRadius.get();
                    wanderTarget = new Vec3(
                        getX() + (random.nextDouble() - 0.5) * 2 * r,
                        getY(),
                        getZ() + (random.nextDouble() - 0.5) * 2 * r
                    );
                    wanderTimer = 100; // re-pick a target roughly every 5 seconds
                }

                wanderTimer--;

                tx = wanderTarget.x;
                ty = wanderTarget.y + 0.5;
                tz = wanderTarget.z;
                moving = true;
            }
            case Mine -> {
                // The bot seeks an ore near itself, walks up to it, then stands and swings.
                BlockPos ore = module.findNearestResource(position(), module.range.get());
                if (ore != null) {
                    tx = ore.getX() + 0.5;
                    ty = ore.getY() + 0.5;
                    tz = ore.getZ() + 0.5;

                    if (position().distanceToSqr(tx, ty, tz) > 2.5) moving = true;
                    else acting = true;
                }

                if (acting) actingOre = ore;
            }
            case Fight -> {
                // The bot seeks the nearest living thing, closes in to melee range, then swings.
                LivingEntity enemy = module.findNearestEnemy(position(), module.range.get());
                if (enemy != null) {
                    tx = enemy.getX();
                    ty = enemy.getY() + enemy.getBbHeight() / 2;
                    tz = enemy.getZ();

                    if (distanceToSqr(enemy) > 6.0) moving = true; // ~2.4 blocks, safely inside melee
                    else acting = true;
                }

                if (acting) actingTarget = enemy;
            }
        }

        xxa = 0;
        zza = moving ? 1 : 0;

        if (moving) {
            // Face the movement target. Minecraft's look vector is (-sin(yaw), cos(yaw)), so the
            // yaw that points at (dx, dz) is atan2(-dx, dz).
            double dx = tx - getX();
            double dz = tz - getZ();
            float yaw = (float) Math.toDegrees(Math.atan2(-dx, dz));
            setYRot(yaw);
            setXRot(0);
            yBodyRot = yaw;
            yHeadRot = yaw;

            setSprinting(module.sprint.get());

            // Auto-jump: walking into a block (stairs, a 1-block ledge) sets horizontalCollision on
            // the previous move, so hop over it.
            if (onGround() && horizontalCollision) jumpFromGround();

            if (module.swing.get() && module.behaviour.get() == FakePlayer.Behaviour.Fight && tickCount % 10 == 0) {
                Vanilla.swing(this, InteractionHand.MAIN_HAND);
            }
        } else {
            setSprinting(false);

            if (acting) {
                // Look straight at the target (including pitch) and swing to act out mining/attacking.
                faceTarget(tx, ty, tz);

                if (module.swing.get() && tickCount % 10 == 0) {
                    Vanilla.swing(this, InteractionHand.MAIN_HAND);
                    if (server != null) server.swing();
                }

                // Singleplayer: don't just act it out - break the ore and hit the enemy through
                // the server-side clone, so blocks and damage are real. Both calls gate
                // themselves (reach, attack cooldown, already-broken block).
                if (server != null) {
                    if (actingOre != null) server.breakBlock(actingOre);
                    if (actingTarget != null) server.attack(actingTarget);
                }
            }
        }

        // Apply vanilla physics: gravity, friction, collision and the movement set above.
        travel(new Vec3(xxa, 0, zza));
    }

    /**
     * Mirror mode: the clone repeats your movements and actions from its own anchor point.
     *
     * <p>Movement is servo'd toward {@code cloneOrigin + (your position - your origin)}, but the
     * offset is resolved in the clone's own - i.e. your copied - facing, so it steps exactly
     * where you stepped instead of turning toward the mirrored spot. Rotation, sprint, sneak and
     * swings are copied verbatim.
     */
    private void driveMirror(FakePlayer module) {
        if (mirrorPlayerOrigin == null || mirrorCloneOrigin == null
            || mirrorPlayerOrigin.distanceToSqr(mc.player.position()) > 256) {
            // First tick, or you teleported / respawned: (re-)anchor instead of sending the
            // clone flying across the map.
            mirrorPlayerOrigin = mc.player.position();
            mirrorCloneOrigin = position();
        }

        Vec3 wanted = mirrorCloneOrigin.add(mc.player.position().subtract(mirrorPlayerOrigin));
        Vec3 delta = wanted.subtract(position());

        // World-space offset -> the clone's local movement axes (forward = (-sin, 0, cos),
        // right = (cos, 0, sin) for Minecraft's yaw), so travel() below walks along YOUR facing.
        float yawRad = (float) Math.toRadians(getYRot());
        double forward = -Math.sin(yawRad) * delta.x + Math.cos(yawRad) * delta.z;
        double right = Math.cos(yawRad) * delta.x + Math.sin(yawRad) * delta.z;

        xxa = (float) Math.max(-1, Math.min(1, right));
        zza = (float) Math.max(-1, Math.min(1, forward));

        // Copy your pose verbatim.
        setYRot(mc.player.getYRot());
        setXRot(mc.player.getXRot());
        yBodyRot = mc.player.getYRot();
        yHeadRot = mc.player.getYRot();
        setSprinting(module.sprint.get() && mc.player.isSprinting());
        setShiftKeyDown(mc.player.isShiftKeyDown());

        // ...and your swings (edge-triggered, since isSwinging() stays true for the animation).
        if (module.swing.get() && Vanilla.isSwinging(mc.player) && !mirrorWasSwinging) {
            Vanilla.swing(this, InteractionHand.MAIN_HAND);
            if (server != null) server.swing();
        }
        mirrorWasSwinging = Vanilla.isSwinging(mc.player);

        travel(new Vec3(xxa, 0, zza));
    }

    /** Turns the bot to look directly at a point, including pitch. */
    private void faceTarget(double x, double y, double z) {
        double dx = x - getX();
        double dz = z - getZ();
        float yaw = (float) Math.toDegrees(Math.atan2(-dx, dz));
        setYRot(yaw);
        yBodyRot = yaw;
        yHeadRot = yaw;

        double dy = y - getEyeY();
        double distXZ = Math.sqrt(dx * dx + dz * dz);
        setXRot((float) Math.toDegrees(-Math.atan2(dy, distXZ)));
    }
}
