/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.utils.entity.clone;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.PropertyMap;
import com.google.common.collect.HashMultimap;
import com.mojang.datafixers.util.Pair;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.util.ReferenceCountUtil;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.mc.Vanilla;
import meteordevelopment.meteorclient.systems.clones.CloneUnit;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.Connection;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoRemovePacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundSetEquipmentPacket;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.lang.reflect.Field;
import java.util.UUID;

import static meteordevelopment.meteorclient.MeteorClient.mc;

/**
 * A real player on the integrated server (singleplayer) standing in for a clone.
 *
 * <p>Unlike the client-side {@link CloneEntity}, this clone actually exists for the server:
 * it mines real blocks through its {@link net.minecraft.server.level.ServerPlayerGameMode},
 * deals real damage through {@link ServerPlayer#attack}, shows up in the tab list with a
 * PlayerInfo entry, and is rendered for you by the normal entity-tracking packets - so there is
 * exactly one visible clone, driven by the {@link CloneEntity} pilot.
 *
 * <p>How it works: a throwaway {@link Connection} backed by an in-process {@link EmbeddedChannel}
 * whose outbound traffic is drained by a discarding handler - packets are built and then
 * released, nothing ever leaves the JVM. Because the connection is never registered with
 * {@code ServerConnectionListener}, it is never ticked, so the vanilla keep-alive and idle
 * logic never runs for it. The player itself is ticked by the integrated server like any other
 * entity, but {@link ServerClonePlayer#travel} is a no-op: the pilot is the sole authority on
 * the clone's position, which is copied over every client tick - no server-side physics drift.
 *
 * <p>Multiplayer note: a dedicated server cannot be given a second real player without a second
 * account session, so on servers the clone remains a client-side imitation.
 */
public class ServerClone
{
    private final IntegratedServer server;
    private final ServerLevel level;
    private final Connection connection;
    private final EmbeddedChannel channel;
    private final ServerClonePlayer player;
    private final UUID uuid;

    /**
     * The integrated server never ticks the clone player, so its attack cooldown ticker never
     * counts down. We reset it by reflection before every attack so the clone hits with full
     * weapon damage instead of ~1 HP (fist).
     */
    private static final Field ATTACK_STRENGTH_TICKER;

    static
    {
        Field f;
        try
        {
            f = LivingEntity.class.getDeclaredField("attackStrengthTicker");
            f.setAccessible(true);
        } 
        catch (NoSuchFieldException e)
        {
            throw new RuntimeException("Cannot find LivingEntity.attackStrengthTicker", e);
        }
        ATTACK_STRENGTH_TICKER = f;
    }

    private ServerClone(IntegratedServer server, ServerLevel level, Connection connection, EmbeddedChannel channel, ServerClonePlayer player)
    {
        this.server = server;
        this.level = level;
        this.connection = connection;
        this.channel = channel;
        this.player = player;
        this.uuid = player.getUUID();
    }

    /**
     * Creates and registers the clone. Must be called on the client thread while a singleplayer
     * world is running.
     *
     * @return the clone, or null when not in singleplayer or when creation failed.
     */
    public static ServerClone create
    (
    String name,
    float armor,
    float armorToughness,
    float attackDamage,
    float attackKnockback,
    float attackSpeed,
    float blockBreakSpeed,
    float blockInteractionRange,
    float burningTime,
    float cameraDistance,
    float entityInteractionRange,
    float explosionKnockbackResistance,
    float fallDamageMultiplier,
    float flyingSpeed,
    float followRange,
    float gravity,
    float jumpStrength,
    float knockbackResistance,
    float luck,
    float maxAbsorption,
    float maxHealth,
    float miningEfficiency,
    float movementEfficiency,
    float movementSpeed,
    float oxygenBonus,
    float safeFallDistance,
    float scale,
    float sneakingSpeed,
    float spawnReinforcements,
    float stepHeight,
    float submergedMiningSpeed,
    float sweepingDamageRatio,
    float temptRange,
    float waterMovementEfficiency,
    float waypointReceiveRange,
    float waypointTransmitRange,
    Vec3 pos,
    float yaw,
    float pitch
    )
    {
        if (mc.getSingleplayerServer() == null || mc.level == null || mc.player == null)
			return null;

        try
        {
            IntegratedServer server = mc.getSingleplayerServer();
            PlayerList playerList = server.getPlayerList();
            ServerLevel level = server.getLevel(mc.level.dimension());
            Connection connection = new Connection(PacketFlow.SERVERBOUND);
            EmbeddedChannel channel = new EmbeddedChannel(connection);
            GameProfile profile = cloneProfile(name);

            channel.pipeline().addLast(new ChannelOutboundHandlerAdapter()
            {
                @Override
                public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise)
                {
                    ReferenceCountUtil.release(msg);
                    promise.setSuccess();
                }
            });
            
            if (level == null)
				return null;

            ServerClonePlayer player = new ServerClonePlayer(server, level, profile, ClientInformation.createDefault());
            player.setPos(pos);
            player.setYRot(yaw);
            player.setXRot(pitch);
            player.yBodyRot = yaw;
            player.yHeadRot = yaw;
            player.setOldPosAndRot();

            
            applyAttributes
            (
				player,
				armor,
				armorToughness,
				attackDamage,
				attackKnockback,
				attackSpeed,
				blockBreakSpeed,
				blockInteractionRange,
				burningTime,
				cameraDistance,
				entityInteractionRange,
				explosionKnockbackResistance,
				fallDamageMultiplier,
				flyingSpeed,
				followRange,
				gravity,
				jumpStrength,
				knockbackResistance,
				luck,
				maxAbsorption,
				maxHealth,
				miningEfficiency,
				movementEfficiency,
				movementSpeed,
				oxygenBonus,
				safeFallDistance,
				scale,
				sneakingSpeed,
				spawnReinforcements,
				stepHeight,
				submergedMiningSpeed,
				sweepingDamageRatio,
				temptRange,
				waterMovementEfficiency,
				waypointReceiveRange,
				waypointTransmitRange
			);

            // Health and absorption ride on the (now applied) attributes.
            player.setHealth(maxHealth);
            player.setAbsorptionAmount(maxAbsorption);

            new ServerGamePacketListenerImpl(server, connection, player, CommonListenerCookie.createInitial(profile, false));
            
            playerList.getPlayers().add(player);
            playerList.broadcastAll(ClientboundPlayerInfoUpdatePacket.createPlayerInitializing(List.of(player)));
            level.addNewPlayer(player);

            return new ServerClone(server, level, connection, channel, player);
        } 
        catch (Throwable t) 
        {
            MeteorClient.LOG.error("Failed to create a singleplayer clone", t);
            return null;
        }
    }

    /**
     * Applies clone stat tuning to the server player: max health, attack damage, movement speed,
     * armor, armor toughness and knockback resistance. Called once at spawn.
     */
    private static void applyAttributes
    (
    ServerPlayer player,
    float armor,
    float armorToughness,
    float attackDamage,
    float attackKnockback,
    float attackSpeed,
    float blockBreakSpeed,
    float blockInteractionRange,
    float burningTime,
    float cameraDistance,
    float entityInteractionRange,
    float explosionKnockbackResistance,
    float fallDamageMultiplier,
    float flyingSpeed,
    float followRange,
    float gravity,
    float jumpStrength,
    float knockbackResistance,
    float luck,
    float maxAbsorption,
    float maxHealth,
    float miningEfficiency,
    float movementEfficiency,
    float movementSpeed,
    float oxygenBonus,
    float safeFallDistance,
    float scale,
    float sneakingSpeed,
    float spawnReinforcements,
    float stepHeight,
    float submergedMiningSpeed,
    float sweepingDamageRatio,
    float temptRange,
    float waterMovementEfficiency,
    float waypointReceiveRange,
    float waypointTransmitRange
    )
    {
        set(player, Attributes.ARMOR, armor);
        set(player, Attributes.ARMOR_TOUGHNESS, armorToughness);
        set(player, Attributes.ATTACK_DAMAGE, attackDamage);
        set(player, Attributes.ATTACK_KNOCKBACK, attackKnockback);
        set(player, Attributes.ATTACK_SPEED, attackSpeed);
        set(player, Attributes.BLOCK_BREAK_SPEED, blockBreakSpeed);
        set(player, Attributes.BLOCK_INTERACTION_RANGE, blockInteractionRange);
        set(player, Attributes.BURNING_TIME, burningTime);
        set(player, Attributes.CAMERA_DISTANCE, cameraDistance);
        set(player, Attributes.ENTITY_INTERACTION_RANGE, entityInteractionRange);
        set(player, Attributes.EXPLOSION_KNOCKBACK_RESISTANCE, explosionKnockbackResistance);
        set(player, Attributes.FALL_DAMAGE_MULTIPLIER, fallDamageMultiplier);
        set(player, Attributes.FLYING_SPEED, flyingSpeed);
        set(player, Attributes.FOLLOW_RANGE, followRange);
        set(player, Attributes.GRAVITY, gravity);
        set(player, Attributes.JUMP_STRENGTH, jumpStrength);
        set(player, Attributes.KNOCKBACK_RESISTANCE, knockbackResistance);
        set(player, Attributes.LUCK, luck);
        set(player, Attributes.MAX_ABSORPTION, maxAbsorption);
        set(player, Attributes.MAX_HEALTH, maxHealth);
        set(player, Attributes.MINING_EFFICIENCY, miningEfficiency);
        set(player, Attributes.MOVEMENT_EFFICIENCY, movementEfficiency);
        set(player, Attributes.MOVEMENT_SPEED, movementSpeed);
        set(player, Attributes.OXYGEN_BONUS, oxygenBonus);
        set(player, Attributes.SAFE_FALL_DISTANCE, safeFallDistance);
        set(player, Attributes.SCALE, scale);
        set(player, Attributes.SNEAKING_SPEED, sneakingSpeed);
        set(player, Attributes.SPAWN_REINFORCEMENTS_CHANCE, spawnReinforcements);
        set(player, Attributes.STEP_HEIGHT, stepHeight);
        set(player, Attributes.SUBMERGED_MINING_SPEED, submergedMiningSpeed);
        set(player, Attributes.SWEEPING_DAMAGE_RATIO, sweepingDamageRatio);
        set(player, Attributes.TEMPT_RANGE, temptRange);
        set(player, Attributes.WATER_MOVEMENT_EFFICIENCY, waterMovementEfficiency);
        set(player, Attributes.WAYPOINT_RECEIVE_RANGE, waypointReceiveRange);
        set(player, Attributes.WAYPOINT_TRANSMIT_RANGE, waypointTransmitRange);
    }

    /** Null-safe base-value set: a player does not carry every registered attribute (tempt range, spawn reinforcements, ...). */
    private static void set(ServerPlayer player, Holder<Attribute> attribute, float value)
    {
        AttributeInstance instance = player.getAttribute(attribute);
        if (instance != null)
            instance.setBaseValue(value);
    }

    /** Profile with a fresh UUID (the real one is taken by you) that carries your skin data. */
    private static GameProfile cloneProfile(String name)
    {
        // GameProfile.properties() forwards an immutable multimap, so copy your textures into a
        // mutable one before handing it to the new profile.
        PropertyMap original = mc.player.getGameProfile().properties();
        PropertyMap copy = new PropertyMap(HashMultimap.create(original));

        return new GameProfile(UUID.randomUUID(), name, copy);
    }

    public UUID getUuid()
    {
        return uuid;
    }

    public boolean isAlive()
    {
        return player.isAlive();
    }

    /** False when the clone somehow ended up in another dimension; the pilot then gets dropped. */
    public boolean isInExpectedLevel()
    {
        return player.level() == level;
    }

    /** Copies the pilot's state onto the server player - the pilot is the only physics authority. */
    public void syncFrom(CloneEntity pilot)
    {
        player.setPos(pilot.position());

        player.setYRot(pilot.getYRot());
        player.setXRot(pilot.getXRot());
        player.yBodyRot = pilot.yBodyRot;
        player.yHeadRot = pilot.yHeadRot;

        player.setSprinting(pilot.isSprinting());
        player.setShiftKeyDown(pilot.isShiftKeyDown());
        player.setDeltaMovement(Vec3.ZERO);
    }

    /** Swings for real: the packet reaches you through normal entity tracking. */
    public void swing()
    {
        Vanilla.swingClone(player);
    }

    /** Ticks the clone like a real player: increments the attack cooldown ticker so {@link #attack}
     * deals full weapon damage. Call this every tick while the clone is alive. */
    public void tick() {
        // Mirrors Player.tick(): the attack cooldown ticker increments every tick, so a freshly
        // spawned clone starts weak and ramps up to full strength like a real player.
        int ticker;
        try
        {
            ticker = (Integer) ATTACK_STRENGTH_TICKER.get(player);
        }
        catch (IllegalAccessException e)
        {
            return;
        }

        int delay = (int) player.getCurrentItemAttackStrengthDelay();
        if (ticker < delay)
        {
            try
            {
                ATTACK_STRENGTH_TICKER.setInt(player, ticker + 1);
            }
            catch (IllegalAccessException e)
            {
                // ignore - falls back to default damage
            }
        }
    }

    /** Attacks the server-side twin of a client-known entity, gated by the vanilla attack cooldown. */
    public void attack(LivingEntity clientTarget)
    {
        // The client-known entity id is client-local: the server twin has a different id. Match
        // by UUID instead.
        Entity target = player.level().getEntity(clientTarget.getUUID());
        if (!(target instanceof LivingEntity serverTarget) || !serverTarget.isAlive())
			return;
        if (player.distanceToSqr(serverTarget) > 16)
			return;

        // player.attack() applies weapon damage, enchantments and critical hits - now that the
        // clone ticks (see #tick), its attack cooldown reaches max and damage is full strength.
        player.attack(serverTarget);
    }

    /** Breaks a block through the clone's own game mode: real break, real drops, real tool wear. */
    public void breakBlock(BlockPos pos)
    {
        if (player.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) > 36) return;
        if (player.level().getBlockState(pos).isAir()) return;

        player.gameMode.destroyBlock(pos);
    }

    /**
     * Equips the items the unit configured (main hand, off hand, armor slots) on the real player.
     *
     * <p>Changed slots are broadcast to tracking players explicitly: the clone player is never
     * ticked by the integrated server (see {@link #tick}), so vanilla's own equipment-change
     * detection in {@code LivingEntity.tick()} never runs for it and no
     * {@link ClientboundSetEquipmentPacket} would ever be sent - which is exactly why the
     * weapon model used to stay invisible on your screen.
     */
    public void equip(CloneUnit unit)
    {
        List<Pair<EquipmentSlot, ItemStack>> changed = new ArrayList<>(6);

        applySlot(EquipmentSlot.MAINHAND, CloneManager.itemStackFromId(unit.mainHand.get()), changed);
        applySlot(EquipmentSlot.OFFHAND, CloneManager.itemStackFromId(unit.offHand.get()), changed);
        applySlot(EquipmentSlot.HEAD, CloneManager.itemStackFromId(unit.helmet.get()), changed);
        applySlot(EquipmentSlot.CHEST, CloneManager.itemStackFromId(unit.chestplate.get()), changed);
        applySlot(EquipmentSlot.LEGS, CloneManager.itemStackFromId(unit.leggings.get()), changed);
        applySlot(EquipmentSlot.FEET, CloneManager.itemStackFromId(unit.boots.get()), changed);

        if (!changed.isEmpty())
            level.getChunkSource().sendToTrackingPlayers(player, new ClientboundSetEquipmentPacket(player.getId(), changed));
    }

    private void applySlot(EquipmentSlot slot, ItemStack stack, List<Pair<EquipmentSlot, ItemStack>> changed)
    {
        if (ItemStack.matches(player.getItemBySlot(slot), stack)) return;

        player.setItemSlot(slot, stack);
        changed.add(Pair.of(slot, stack.copy()));
    }

    /** Removes the clone from the server, the tab list and the world. Safe to call twice. */
    public void despawn()
    {
        try
        {
            PlayerList playerList = server.getPlayerList();
            playerList.getPlayers().remove(player);
            if (player.isPassenger())
				player.stopRiding();
            level.removePlayerImmediately(player, Entity.RemovalReason.DISCARDED);
            playerList.broadcastAll(new ClientboundPlayerInfoRemovePacket(List.of(uuid)));
        }
        catch (Throwable t)
        {
            // The world may already be closing down - the pilot is going away regardless.
            MeteorClient.LOG.debug("Failed to cleanly remove the clone from the server: {}", t.toString());
        }
        finally
        {
            channel.close();
        }
    }

    /**
     * The server-side body of a clone. Two deliberate deviations from a vanilla player:
     * {@link #travel} never moves it (the pilot decides every position), and it never drops its
     * copied inventory when killed - a clone death just removes it from the world.
     */
    private static class ServerClonePlayer extends ServerPlayer
    {
        public ServerClonePlayer(IntegratedServer server, ServerLevel level, GameProfile profile, ClientInformation info)
        {
            super(server, level, profile, info);
        }

        @Override
        public void travel(Vec3 movement)
        {}

        @Override
        protected void dropAllDeathLoot(ServerLevel level, DamageSource source)
        {}
    }
}
