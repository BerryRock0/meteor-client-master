/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.utils.entity.fakeplayer;

import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.player.FakePlayer;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.world.entity.Entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static meteordevelopment.meteorclient.MeteorClient.mc;

/**
 * Fake player registry. Public API for other mods and addons: clones can be spawned, queried and
 * removed without touching the {@code FakePlayer} module's UI, and every method is safe to call
 * from anywhere on the client thread.
 *
 * <p>Each clone is a {@link FakePlayerEntity} pilot carrying the AI/mirror decisions. In
 * singleplayer (and with the module's server-clone setting on) the pilot is additionally backed
 * by a real {@link ServerClone} player on the integrated server; on multiplayer servers the
 * clone is a pure client-side imitation.
 */
public class FakePlayerManager {
    private static final List<FakePlayerEntity> ENTITIES = new ArrayList<>();
    private static boolean tickerSubscribed;

    private FakePlayerManager() {
    }

    /** The currently spawned fake players. The list is a read-only view; use {@link #add} / {@link #remove} to change it. */
    public static List<FakePlayerEntity> getFakePlayers() {
        return Collections.unmodifiableList(ENTITIES);
    }

    public static FakePlayerEntity get(String name) {
        for (FakePlayerEntity fp : ENTITIES) {
            if (fp.getName().getString().equals(name)) return fp;
        }

        return null;
    }

    /**
     * Spawns a clone at your position with your attributes. In singleplayer it becomes a real
     * server-side player (see the module's server-clone setting); elsewhere a client-side
     * imitation.
     *
     * @return the spawned clone's pilot, or null when not in a world.
     */
    public static FakePlayerEntity add(String name, float health, boolean copyInv) {
        if (!Utils.canUpdate()) return null;

        FakePlayerEntity fakePlayer = new FakePlayerEntity(mc.player, name, health, copyInv);
        fakePlayer.aiControlled = true;

        FakePlayer module = Modules.get().get(FakePlayer.class);
        if (module != null && module.serverClone.get() && mc.getSingleplayerServer() != null) {
            fakePlayer.server = ServerClone.create(name, health, copyInv, mc.player.position(), mc.player.getYRot(), mc.player.getXRot());
        }

        fakePlayer.spawn();
        ENTITIES.add(fakePlayer);
        subscribeTicker();
        return fakePlayer;
    }

    /** Spawns a fake player with the module's default name, health and inventory copy settings. */
    public static FakePlayerEntity add() {
        FakePlayer module = Modules.get().get(FakePlayer.class);
        if (module == null) return null;

        return add(module.name.get(), module.health.get(), module.copyInv.get());
    }

    public static void remove(FakePlayerEntity fp) {
        ENTITIES.removeIf(fp1 -> {
            if (fp1.getName().getString().equals(fp.getName().getString())) {
                fp1.despawn();
                return true;
            }

            return false;
        });
    }

    public static void clear() {
        if (ENTITIES.isEmpty()) return;
        ENTITIES.forEach(FakePlayerEntity::despawn);
        ENTITIES.clear();
    }

    /**
     * Whether an entity is one of our clones: either a client-side pilot or the server-side twin
     * of a singleplayer clone (which lives in the client level as a normal tracked player).
     */
    public static boolean isClone(Entity entity) {
        for (FakePlayerEntity fp : ENTITIES) {
            if (fp == entity) return true;
            if (fp.server != null && entity.getUUID().equals(fp.server.getUuid())) return true;
        }

        return false;
    }

    private static void subscribeTicker() {
        if (tickerSubscribed) return;
        MeteorClient.EVENT_BUS.subscribe(FakePlayerManager.class);
        tickerSubscribed = true;
    }

    /**
     * Server-backed clones are not part of the client level, so nothing else ticks them. Here
     * the pilot runs its AI/mirror logic and physics for one tick, then the resulting state is
     * copied onto the real server player. Client-side-only clones are ticked by the client level
     * as usual and are skipped.
     */
    @EventHandler
    private static void onTick(TickEvent.Pre event) {
        if (ENTITIES.isEmpty()) return;

        // Left the world: drop stale pilots (their server clones go with the shutting-down server).
        if (!Utils.canUpdate()) {
            clear();
            return;
        }

        for (Iterator<FakePlayerEntity> it = ENTITIES.iterator(); it.hasNext(); ) {
            FakePlayerEntity fp = it.next();
            if (fp.server == null) continue;

            if (!fp.server.isAlive() || !fp.server.isInExpectedLevel()) {
                fp.despawn();
                it.remove();
                continue;
            }

            fp.tick();
            fp.server.syncFrom(fp);
        }
    }

    public static void forEach(Consumer<FakePlayerEntity> action) {
        for (FakePlayerEntity fakePlayer : ENTITIES) {
            action.accept(fakePlayer);
        }
    }

    public static int count() {
        return ENTITIES.size();
    }

    public static Stream<FakePlayerEntity> stream() {
        return ENTITIES.stream();
    }

    public static boolean contains(FakePlayerEntity fp) {
        return ENTITIES.contains(fp);
    }
}
