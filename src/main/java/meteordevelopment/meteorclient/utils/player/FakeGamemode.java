/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.utils.player;

import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.game.GameLeftEvent;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.utils.entity.EntityUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.protocol.game.ServerboundChangeGameModePacket;
import net.minecraft.network.protocol.game.ServerboundPlayerAbilitiesPacket;
import net.minecraft.network.protocol.game.ServerboundSetCreativeModeSlotPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.level.GameType;

import static meteordevelopment.meteorclient.MeteorClient.mc;

/**
 * Gamemode imitation backed by a real attempt to get the mode granted.
 *
 * <p>Arming first performs a real change: in singleplayer the vanilla {@code setGameMode}
 * runs on the integrated server's own thread (no permission check - it is our server), and
 * on multiplayer servers the vanilla {@link ServerboundChangeGameModePacket} is sent, which
 * servers that do not validate it grant for real. In both cases everything then behaves
 * exactly like the real mode: real items, no rollbacks, vanilla abilities.</p>
 *
 * <p>When the server refuses (vanilla permission check logs a warning and ignores the
 * packet), the local state - mode and abilities - is re-applied every tick so the client
 * keeps behaving like the mode, and every outgoing packet that would reveal the fake to
 * the server's protections is suppressed: creative-slot items never leave the client and
 * the server never learns that creative flight was enabled. The server therefore never
 * sees anything it could kick or flag.</p>
 */
public class FakeGamemode {
    /** The mode we are pretending to have, or {@code null} when no fake is armed. */
    private static GameType faked;
    private static boolean subscribed;

    private FakeGamemode() {}

    /**
     * Attempts a real gamemode change and arms an undetectable client-side fake for as long
     * as the server has not granted the mode.
     */
    public static void arm(GameType mode) {
        if (mc.player == null || mc.getConnection() == null) return;

        ServerPlayer serverPlayer = null;
        if (mc.hasSingleplayerServer() && mc.getSingleplayerServer() != null) {
            serverPlayer = mc.getSingleplayerServer().getPlayerList().getPlayer(mc.player.getUUID());
        }

        if (serverPlayer != null) {
            // Singleplayer: run the vanilla change on the server thread itself - the exact
            // flow of /gamemode, thread-safe and permission-free, so it works even in
            // worlds created without cheats. Items and block actions become truly real.
            ServerPlayer player = serverPlayer;
            mc.getSingleplayerServer().execute(() -> player.setGameMode(mode));
        } else {
            // Multiplayer: the real attempt - servers that do not validate this packet
            // grant the mode for real, vanilla servers just ignore it.
            mc.getConnection().send(new ServerboundChangeGameModePacket(mode));
        }

        applyLocal(mode);

        if (faked == null) {
            faked = mode;
        }
        if (!subscribed) {
            subscribed = true;
            MeteorClient.EVENT_BUS.subscribe(FakeGamemode.class);
        }
    }

    /** Stops the fake and syncs the local state to the mode the server actually has. */
    public static void disarm() {
        GameType confirmed = serverConfirmed();

        faked = null;
        if (subscribed) {
            subscribed = false;
            MeteorClient.EVENT_BUS.unsubscribe(FakeGamemode.class);
        }

        if (mc.player != null && mc.gameMode != null) {
            applyLocal(confirmed != null ? confirmed : GameType.SURVIVAL);
        }
    }

    /** True while the local gamemode differs from the one the server granted. */
    public static boolean isActive() {
        return faked != null && serverConfirmed() != faked;
    }

    /** The gamemode the server has for this player (tab list entry), or null. */
    public static GameType serverConfirmed() {
        if (mc.player == null || mc.getConnection() == null) return null;
        return EntityUtils.getGameMode(mc.player);
    }

    @EventHandler
    private static void onTick(TickEvent.Pre event) {
        if (faked == null || mc.player == null || mc.gameMode == null || mc.getConnection() == null) return;

        if (serverConfirmed() == faked) {
            // The server granted the mode - its own packets keep everything in sync from
            // here on, the fake and its masking are no longer needed.
            faked = null;
            subscribed = false;
            MeteorClient.EVENT_BUS.unsubscribe(FakeGamemode.class);
            return;
        }

        // Refused: server packets (respawn, plugins, anticheats) can push their own mode
        // and abilities at any moment - re-apply the fake so it keeps working locally.
        applyLocal(faked);
    }

    @EventHandler
    private static void onPacketSend(PacketEvent.Send event) {
        if (!isActive()) return;

        // Never hand the server an item it did not give us - vanilla logs it and
        // anticheats flag creative-slot packets as a hacked client. The item stays a
        // local-only thing, which is the only thing possible while the server refuses.
        if (event.packet instanceof ServerboundSetCreativeModeSlotPacket) {
            event.cancel();
            return;
        }

        // Same for abilities: the server must not learn that creative flight was enabled.
        if (event.packet instanceof ServerboundPlayerAbilitiesPacket) event.cancel();
    }

    @EventHandler
    private static void onGameLeft(GameLeftEvent event) {
        if (faked != null) {
            faked = null;
            subscribed = false;
            MeteorClient.EVENT_BUS.unsubscribe(FakeGamemode.class);
        }
    }

    private static void applyLocal(GameType mode) {
        mc.gameMode.setLocalMode(mode);
        applyAbilities(mode);
    }

    /** Mirrors the mode into the player abilities so isCreative() and friends behave like it. */
    private static void applyAbilities(GameType mode) {
        Abilities abilities = mc.player.getAbilities();

        switch (mode) {
            case CREATIVE -> {
                abilities.instabuild = true;   // isCreative(): infinite items, no consumption
                abilities.mayfly = true;       // allows creative flight
                abilities.invulnerable = true; // no damage
                abilities.mayBuild = true;     // can place/break
            }
            case SPECTATOR -> {
                abilities.instabuild = false;
                abilities.mayfly = true;
                abilities.flying = true;
                abilities.invulnerable = true;
                abilities.mayBuild = false;
            }
            default -> {
                abilities.instabuild = false;
                abilities.mayfly = false;
                abilities.flying = false;
                abilities.invulnerable = false;
                abilities.mayBuild = true;
            }
        }
    }
}
