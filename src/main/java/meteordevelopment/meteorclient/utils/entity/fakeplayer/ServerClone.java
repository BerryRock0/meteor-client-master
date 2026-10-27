/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.utils.entity.fakeplayer;

import com.mojang.authlib.GameProfile;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.util.ReferenceCountUtil;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.mc.Vanilla;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoRemovePacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.UUID;

import static meteordevelopment.meteorclient.MeteorClient.mc;

/**
 * A real player on the integrated server (singleplayer) standing in for a clone.
 *
 * <p>Unlike the client-side {@link FakePlayerEntity}, this clone actually exists for the server:
 * it mines real blocks through its {@link net.minecraft.server.level.ServerPlayerGameMode},
 * deals real damage through {@link ServerPlayer#attack}, shows up in the tab list with a
 * PlayerInfo entry, and is rendered for you by the normal entity-tracking packets - so there is
 * exactly one visible clone, driven by the {@link FakePlayerEntity} pilot.
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
public class ServerClone {
    private final IntegratedServer server;
    private final ServerLevel level;
    private final Connection connection;
    private final EmbeddedChannel channel;
    private final ServerClonePlayer player;
    private final UUID uuid;

    private ServerClone(IntegratedServer server, ServerLevel level, Connection connection, EmbeddedChannel channel, ServerClonePlayer player) {
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
    public static ServerClone create(String name, float health, boolean copyInv, Vec3 pos, float yaw, float pitch) {
        if (mc.getSingleplayerServer() == null || mc.level == null || mc.player == null) return null;

        try {
            IntegratedServer server = mc.getSingleplayerServer();
            ServerLevel level = server.getLevel(mc.level.dimension());
            if (level == null) return null;

            GameProfile profile = cloneProfile(name);

            // Throwaway connection: the embedded channel fires channelActive so Connection picks
            // it up, and the handler added below drains (and releases) every outgoing packet.
            Connection connection = new Connection(PacketFlow.SERVERBOUND);
            EmbeddedChannel channel = new EmbeddedChannel(connection);
            channel.pipeline().addLast(new ChannelOutboundHandlerAdapter() {
                @Override
                public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
                    ReferenceCountUtil.release(msg);
                    promise.setSuccess();
                }
            });

            ServerClonePlayer player = new ServerClonePlayer(server, level, profile, ClientInformation.createDefault());
            player.setPos(pos);
            player.setYRot(yaw);
            player.setXRot(pitch);
            player.yBodyRot = yaw;
            player.yHeadRot = yaw;
            player.setOldPosAndRot();

            if (health <= 20) {
                player.setHealth(health);
            } else {
                player.setHealth(health);
                player.setAbsorptionAmount(health - 20);
            }

            if (copyInv) player.getInventory().replaceWith(mc.player.getInventory());

            // Wires the listener to the player (player.connection). We deliberately skip
            // setupInboundProtocol: there are no inbound packets to decode on this connection.
            new ServerGamePacketListenerImpl(server, connection, player, CommonListenerCookie.createInitial(profile, false));

            // Same order as PlayerList.placeNewPlayer, minus the login/configuration packets a
            // running client must never receive: tab entry first (the client needs the profile
            // to render the skin), then register the player so entity tracking spawns it.
            PlayerList playerList = server.getPlayerList();
            playerList.getPlayers().add(player);
            playerList.broadcastAll(ClientboundPlayerInfoUpdatePacket.createPlayerInitializing(List.of(player)));
            level.addNewPlayer(player);

            return new ServerClone(server, level, connection, channel, player);
        } catch (Throwable t) {
            MeteorClient.LOG.error("Failed to create a singleplayer clone", t);
            return null;
        }
    }

    /** Profile with a fresh UUID (the real one is taken by you) that carries your skin data. */
    private static GameProfile cloneProfile(String name) {
        GameProfile profile = new GameProfile(UUID.randomUUID(), name);
        profile.properties().putAll(mc.player.getGameProfile().properties());
        return profile;
    }

    public UUID getUuid() {
        return uuid;
    }

    public boolean isAlive() {
        return player.isAlive();
    }

    /** False when the clone somehow ended up in another dimension; the pilot then gets dropped. */
    public boolean isInExpectedLevel() {
        return player.level() == level;
    }

    /** Copies the pilot's state onto the server player - the pilot is the only physics authority. */
    public void syncFrom(FakePlayerEntity pilot) {
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
    public void swing() {
        Vanilla.swingClone(player);
    }

    /** Attacks the server-side twin of a client-known entity, gated by the vanilla attack cooldown. */
    public void attack(LivingEntity clientTarget) {
        if (player.getAttackStrengthScale(0) < 1) return;

        Entity target = player.level().getEntity(clientTarget.getId());
        if (!(target instanceof LivingEntity serverTarget) || !serverTarget.isAlive()) return;
        if (player.distanceToSqr(serverTarget) > 16) return;

        player.attack(serverTarget);
    }

    /** Breaks a block through the clone's own game mode: real break, real drops, real tool wear. */
    public void breakBlock(BlockPos pos) {
        if (player.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) > 36) return;
        if (player.level().getBlockState(pos).isAir()) return;

        player.gameMode.destroyBlock(pos);
    }

    /** Removes the clone from the server, the tab list and the world. Safe to call twice. */
    public void despawn() {
        try {
            PlayerList playerList = server.getPlayerList();
            playerList.getPlayers().remove(player);
            if (player.isPassenger()) player.stopRiding();
            level.removePlayerImmediately(player, Entity.RemovalReason.DISCARDED);
            playerList.broadcastAll(new ClientboundPlayerInfoRemovePacket(List.of(uuid)));
        } catch (Throwable t) {
            // The world may already be closing down - the pilot is going away regardless.
            MeteorClient.LOG.debug("Failed to cleanly remove the clone from the server: {}", t.toString());
        } finally {
            channel.close();
        }
    }

    /**
     * The server-side body of a clone. Two deliberate deviations from a vanilla player:
     * {@link #travel} never moves it (the pilot decides every position), and it never drops its
     * copied inventory when killed - a clone death just removes it from the world.
     */
    private static class ServerClonePlayer extends ServerPlayer {
        public ServerClonePlayer(IntegratedServer server, ServerLevel level, GameProfile profile, ClientInformation info) {
            super(server, level, profile, info);
        }

        @Override
        public void travel(Vec3 movement) {
        }

        @Override
        protected void dropAllDeathLoot(ServerLevel level, DamageSource source) {
        }
    }
}
