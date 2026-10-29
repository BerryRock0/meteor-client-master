/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.utils.entity.clone;

import com.mojang.authlib.GameProfile;
import meteordevelopment.meteorclient.mc.Vanilla;
import meteordevelopment.meteorclient.mixin.AbstractClientPlayerAccessor;
import meteordevelopment.meteorclient.mixin.EntityAccessor;
import meteordevelopment.meteorclient.systems.modules.player.Clones;
import meteordevelopment.meteorclient.systems.clones.CloneUnit;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.player.RemotePlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

import java.util.UUID;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class CloneEntity extends RemotePlayer
{
    /**
     * Disables entity push with this clone
     */
    public boolean doNotPush;
    /**
     * Stops rendering the clone when you are inside it
     */
    public boolean hideWhenInsideCamera;
    /**
     * Prevents you from interacting with the clone; will also prevent TargetUtils selecting it as a target
     */
    public boolean noHit;

    /**
     * When true, this clone drives its own movement and physics every tick (walking, jumping,
     * gravity) based on its unit's behaviour setting. Only the manager-spawned
     * manager-spawned clones set this; decorative ones (PopChams ghosts, Blink models) leave it off so they stay
     * exactly as before.
     */
    public boolean aiControlled;

    /**
     * The unit that describes this clone: behaviour, health, range and friends live there and are
     * read live every tick, so editing a running unit's settings takes effect immediately. Set by
     * {@link CloneManager}; API spawns get a transient unit that is stored nowhere.
     */
    public CloneUnit unit;

    /**
     * When set, this clone also exists as a real player on the integrated server (singleplayer).
     * The pilot logic below keeps running client-side and the bridge applies its outcomes for
     * real: blocks broken through the clone's own game mode, damage through its own attacks.
     * Null on multiplayer servers, where a server-side player is impossible without a second
     * account and the clone stays a client-side imitation.
     */
    public ServerClone server;

    /** Mirror mode anchors: your position and the clone's position when mirroring started. */
    private Vec3 mirrorPlayerOrigin;
    private Vec3 mirrorCloneOrigin;
	private Vec3 wanderTarget; /** Current wander target, only used in {@link Behaviour.Wander}. */
    private int jumpCooldown; /** Cooldown (in ticks) between automatic jumps, so the player does not spam the jump. */
    private int attackCooldown; /** Ticks left before the clone may deal another real attack (Fight mode, server-clone only). */
    private int wanderTimer; /** Ticks left before a new wander target is picked. */
    private boolean mirrorWasSwinging; /** Whether the local player was swinging on the previous tick (edge detection). */

    public CloneEntity(Player player, String name, float health, float absorption)
    {
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
        setHealth(health);
        setAbsorptionAmount(absorption);
    }

    @Override
    public int getId()
    {
        return ((EntityAccessor) this).meteor$getId();
    }

    public void spawn()
    {
        // Singleplayer clones are registered by the integrated server instead of the client
        // level - adding the pilot here too would render the clone twice.
        if (server != null)
			return;

        unsetRemoved();
        mc.level.addEntity(this);
    }

    public void despawn()
    {
        if (server != null)
        {
            server.despawn();
            server = null;
            return;
        }

        if (mc.level != null)
			mc.level.removeEntity(getId(), RemovalReason.DISCARDED);
        setRemoved(RemovalReason.DISCARDED);
    }

    @Nullable
    @Override
    protected PlayerInfo getPlayerInfo()
    {
        PlayerInfo entry = super.getPlayerInfo();

        if (entry == null)
        {
            ((AbstractClientPlayerAccessor) this).meteor$setPlayerInfo(mc.getConnection().getPlayerInfo(mc.player.getUUID()));
        }

        return entry;
    }

    @Override
    public void aiStep()
    {
        // RemotePlayer.aiStep() only applies network interpolation and never calls the vanilla
        // Player.aiStep(), so the clone would otherwise be a frozen statue. We drive movement
        // ourselves below, but only for the manager-spawned players; decorative subclasses
        // (PopChams ghosts, Blink models) leave aiControlled off and keep their old behaviour.
        super.aiStep();

        if (aiControlled && mc.player != null)
			driveBehaviour();

        // Tick the server-side clone like a real player so its attack cooldown ramps up to full
        // strength and player.attack() deals proper weapon damage.
        if (server != null)
			server.tick();

        // Keep the server-side twin's equipment in sync with the unit's configured items.
        if (server != null)
			server.equip(unit);
    }

    private void driveBehaviour()
    {
        // Every aspect of the clone (behaviour, range, filters) lives in its unit.
        CloneUnit unit = this.unit;
        if (unit == null)
			return;

        // Equip the items the unit configured (main hand, off hand, armor slots).
        equip(unit);

        // Stasis keeps the original statue behaviour: no physics, no movement.
        if (unit.behaviour.get() == Clones.Behaviour.Stasis)
			return;

        // Mirror has its own control loop: it copies your inputs instead of seeking targets.
        if (unit.behaviour.get() == Clones.Behaviour.Mirror)
        {
            driveMirror(unit);
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

        switch (unit.behaviour.get())
        {
            case Follow ->
            {
                double dx = mc.player.getX() - getX();
                double dz = mc.player.getZ() - getZ();
                double stopSq = unit.followDistance.get() * unit.followDistance.get();

                tx = mc.player.getX();
                ty = mc.player.getEyeY();
                tz = mc.player.getZ();

                if (dx * dx + dz * dz > stopSq)
					moving = true;
            }
            case Wander ->
            {
                if (wanderTarget == null || wanderTimer <= 0 || position().distanceToSqr(wanderTarget) < 1.0)
                {
                    double r = unit.wanderRadius.get();
                    wanderTarget = new Vec3(getX() + (random.nextDouble() - 0.5) * 2 * r, getY(), getZ() + (random.nextDouble() - 0.5) * 2 * r);
                    wanderTimer = 100; // re-pick a target roughly every 5 seconds
                }

                wanderTimer--;

                tx = wanderTarget.x;
                ty = wanderTarget.y + 0.5;
                tz = wanderTarget.z;
                moving = true;
            }
            case Mine ->
            {
                // The bot seeks an ore near itself, walks up to it, then stands and swings.
                BlockPos ore = Clones.findNearestResource(position(), unit.range.get());
                if (ore != null)
                {
                    tx = ore.getX() + 0.5;
                    ty = ore.getY() + 0.5;
                    tz = ore.getZ() + 0.5;

                    if (position().distanceToSqr(tx, ty, tz) > 2.5) 
						moving = true;
                    else
						acting = true;
                }

                if (acting)
					actingOre = ore;
            }
            case Fight ->
            {
                // The bot seeks the nearest living thing, closes in to melee range, then swings.
                LivingEntity enemy = Clones.findNearestEnemy(position(), unit.range.get());
                if (enemy != null)
                {
                    tx = enemy.getX();
                    ty = enemy.getY() + enemy.getBbHeight() / 2;
                    tz = enemy.getZ();

                    if (distanceToSqr(enemy) > 6.0)
						moving = true; // ~2.4 blocks, safely inside melee
                    else
						acting = true;
                }

                if (acting) actingTarget = enemy;
            }
        }

        xxa = 0;
        zza = moving ? 1 : 0;

        if (moving)
        {
            // Face the movement target. Minecraft's look vector is (-sin(yaw), cos(yaw)), so the
            // yaw that points at (dx, dz) is atan2(-dx, dz).
            double dx = tx - getX();
            double dz = tz - getZ();
            float yaw = (float) Math.toDegrees(Math.atan2(-dx, dz));
            setYRot(yaw);
            setXRot(0);
            yBodyRot = yaw;
            yHeadRot = yaw;

            setSprinting(unit.sprint.get());

            // Auto-jump: walking into a block (stairs, a 1-block ledge) sets horizontalCollision on
            // the previous move, so hop over it.
            if (onGround() && horizontalCollision)
				jumpFromGround();

            if (unit.swing.get() && unit.behaviour.get() == Clones.Behaviour.Fight && tickCount % 10 == 0)
            {
                Vanilla.swing(this, InteractionHand.MAIN_HAND);
            }
        } 
        else 
        {
            setSprinting(false);

            if (acting)
            {
                // Look straight at the target (including pitch) and swing to act out mining/attacking.
                faceTarget(tx, ty, tz);

                if (unit.swing.get() && tickCount % 10 == 0)
                {
                    Vanilla.swing(this, InteractionHand.MAIN_HAND);
                    if (server != null) server.swing();
                }

                // Singleplayer: don't just act it out - break the ore and hit the enemy through
                // the server-side clone, so blocks and damage are real. Both calls gate
                // themselves (reach, attack cooldown, already-broken block).
                if (server != null)
                {
                    if (actingOre != null) server.breakBlock(actingOre);
                    if (actingTarget != null)
                    {
                        if (attackCooldown <= 0)
                        {
                            server.attack(actingTarget);
                            // Vanilla attack delay in ticks: 20 / attack speed (fist = 4 -> 5 ticks).
                            attackCooldown = (int) Math.max(1, Math.ceil(20.0 / unit.attackSpeed.get()));
                        } 
                        else
                        {
                            attackCooldown--;
                        }
                    }
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
    private void driveMirror(CloneUnit unit)
    {
        if (mirrorPlayerOrigin == null || mirrorCloneOrigin == null || mirrorPlayerOrigin.distanceToSqr(mc.player.position()) > 256)
        {
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
        setSprinting(unit.sprint.get() && mc.player.isSprinting());
        setShiftKeyDown(mc.player.isShiftKeyDown());

        // ...and your swings (edge-triggered, since isSwinging() stays true for the animation).
        if (unit.swing.get() && Vanilla.isSwinging(mc.player) && !mirrorWasSwinging)
        {
            Vanilla.swing(this, InteractionHand.MAIN_HAND);
            if (server != null)
				server.swing();
        }
        mirrorWasSwinging = Vanilla.isSwinging(mc.player);

        travel(new Vec3(xxa, 0, zza));
    }

    /** Turns the bot to look directly at a point, including pitch. */
    private void faceTarget(double x, double y, double z)
    {
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

    /** Equips the items the unit configured (main hand, off hand, armor slots). */
    public void equip(CloneUnit unit)
    {
        setItemSlot(EquipmentSlot.MAINHAND, CloneManager.itemStackFromId(unit.mainHand.get()));
        setItemSlot(EquipmentSlot.OFFHAND, CloneManager.itemStackFromId(unit.offHand.get()));
        setItemSlot(EquipmentSlot.HEAD, CloneManager.itemStackFromId(unit.helmet.get()));
        setItemSlot(EquipmentSlot.CHEST, CloneManager.itemStackFromId(unit.chestplate.get()));
        setItemSlot(EquipmentSlot.LEGS, CloneManager.itemStackFromId(unit.leggings.get()));
        setItemSlot(EquipmentSlot.FEET, CloneManager.itemStackFromId(unit.boots.get()));
    }
}
