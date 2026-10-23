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
import net.minecraft.world.InteractionHand;
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

    /** Cooldown (in ticks) between automatic jumps, so the player does not spam the jump. */
    private int jumpCooldown;
    /** Current wander target, only used in {@link Behaviour.Wander}. */
    private Vec3 wanderTarget;
    /** Ticks left before a new wander target is picked. */
    private int wanderTimer;

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
        unsetRemoved();
        mc.level.addEntity(this);
    }

    public void despawn() {
        mc.level.removeEntity(getId(), RemovalReason.DISCARDED);
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

        boolean wantMove = false;
        double targetX = getX(), targetZ = getZ();

        if (module.behaviour.get() == FakePlayer.Behaviour.Follow) {
            double dx = mc.player.getX() - getX();
            double dz = mc.player.getZ() - getZ();
            double stopSq = module.followDistance.get() * module.followDistance.get();

            if (dx * dx + dz * dz > stopSq) {
                targetX = mc.player.getX();
                targetZ = mc.player.getZ();
                wantMove = true;
            }
        } else { // Wander
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

            targetX = wanderTarget.x;
            targetZ = wanderTarget.z;
            wantMove = true;
        }

        xxa = 0;
        zza = wantMove ? 1 : 0;

        if (wantMove) {
            // Face the movement target. Minecraft's look vector is (-sin(yaw), cos(yaw)), so the
            // yaw that points at (dx, dz) is atan2(-dx, dz).
            double dx = targetX - getX();
            double dz = targetZ - getZ();
            float yaw = (float) Math.toDegrees(Math.atan2(-dx, dz));

            setYRot(yaw);
            setXRot(0);
            yBodyRot = yaw;
            yHeadRot = yaw;

            setSprinting(module.sprint.get());

            // Auto-jump: walking into a block (stairs, a 1-block ledge) sets horizontalCollision on
            // the previous move, so hop over it.
            if (onGround() && horizontalCollision) jumpFromGround();

            if (module.swing.get() && tickCount % 10 == 0) {
                Vanilla.swing(this, InteractionHand.MAIN_HAND);
            }
        } else {
            setSprinting(false);
        }

        // Apply vanilla physics: gravity, friction, collision and the movement set above.
        travel(new Vec3(xxa, 0, zza));
    }
}
