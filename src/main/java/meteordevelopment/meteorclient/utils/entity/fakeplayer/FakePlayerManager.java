/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.utils.entity.fakeplayer;

import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.player.FakePlayer;
import meteordevelopment.meteorclient.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static meteordevelopment.meteorclient.MeteorClient.mc;

/**
 * Client-side fake player registry. Public API for other mods and addons: fake players can be
 * spawned, queried and removed without touching the {@code FakePlayer} module's UI, and every
 * method is safe to call from anywhere on the client thread.
 */
public class FakePlayerManager {
    private static final List<FakePlayerEntity> ENTITIES = new ArrayList<>();

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
     * Spawns a client-side fake player at your position with your attributes.
     *
     * @return the spawned fake player, or null when not in a world.
     */
    public static FakePlayerEntity add(String name, float health, boolean copyInv) {
        if (!Utils.canUpdate()) return null;

        FakePlayerEntity fakePlayer = new FakePlayerEntity(mc.player, name, health, copyInv);
        fakePlayer.aiControlled = true;
        fakePlayer.spawn();
        ENTITIES.add(fakePlayer);
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
