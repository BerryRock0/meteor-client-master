/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.utils.entity.clone;

import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.systems.modules.player.CloneUnit;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static meteordevelopment.meteorclient.MeteorClient.mc;

/**
 * Clone registry. Public API for other mods and addons: clones can be spawned, queried and
 * removed without touching the {@code Clones} module's UI, and every method is safe to call
 * from anywhere on the client thread.
 *
 * <p>Each clone is a {@link CloneEntity} pilot carrying the AI/mirror decisions. In
 * singleplayer (and with the module's server-clone setting on) the pilot is additionally backed
 * by a real {@link ServerClone} player on the integrated server; on multiplayer servers the
 * clone is a pure client-side imitation.
 */
public class CloneManager
{
    private static final List<CloneEntity> ENTITIES = new ArrayList<>();
    private static boolean tickerSubscribed;

    private CloneManager()
    {}

    /** The currently spawned clones. The list is a read-only view; use {@link #add} / {@link #remove} to change it. */
    public static List<CloneEntity> getClones()
    {
        return Collections.unmodifiableList(ENTITIES);
    }

    public static CloneEntity get(String name)
    {
        for (CloneEntity fp : ENTITIES)
        {
            if (fp.getName().getString().equals(name))
				return fp;
        }

        return null;
    }

    /**
     * Spawns the clone described by {@code unit} at your position. In singleplayer it becomes a
     * real server-side player (see the unit's server-clone setting); elsewhere a client-side
     * imitation.
     *
     * @return the spawned clone's pilot, or null when not in a world.
     */
    public static CloneEntity add(CloneUnit unit)
    {
        if (!Utils.canUpdate())
			return null;

        CloneEntity clone = new CloneEntity(mc.player, unit.name.get(), unit.maxHealth.get().floatValue(), unit.maxAbsorption.get().floatValue());
        clone.aiControlled = true;
        clone.unit = unit;

        if (unit.serverClone.get() && mc.getSingleplayerServer() != null)
        {
            clone.server = ServerClone.create(unit.name.get(),
            unit.armor.get().floatValue(),
            unit.armorToughness.get().floatValue(),
            unit.attackDamage.get().floatValue(),
            unit.attackKnockback.get().floatValue(),
            unit.attackSpeed.get().floatValue(),
            unit.blockBreakSpeed.get().floatValue(),
            unit.blockInteractionRange.get().floatValue(),
            unit.burningTime.get().floatValue(),
            unit.cameraDistance.get().floatValue(),
            unit.entityInteractionRange.get().floatValue(),
            unit.explosionKnockbackResistance.get().floatValue(),
            unit.fallDamageMultiplier.get().floatValue(),
            unit.flyingSpeed.get().floatValue(),
            unit.followRange.get().floatValue(),
            unit.gravity.get().floatValue(),
            unit.jumpStrength.get().floatValue(),
            unit.knockbackResistance.get().floatValue(),
            unit.luck.get().floatValue(),
            unit.maxAbsorption.get().floatValue(),
            unit.maxHealth.get().floatValue(),
            unit.miningEfficiency.get().floatValue(),
            unit.movementEfficiency.get().floatValue(),
            unit.movementSpeed.get().floatValue(),
            unit.oxygenBonus.get().floatValue(),
            unit.safeFallDistance.get().floatValue(),
            unit.scale.get().floatValue(),
            unit.sneakingSpeed.get().floatValue(),
            unit.spawnReinforcements.get().floatValue(),
            unit.stepHeight.get().floatValue(),
            unit.submergedMiningSpeed.get().floatValue(),
            unit.sweepingDamageRatio.get().floatValue(),
            unit.temptRange.get().floatValue(),
            unit.waterMovementEfficiency.get().floatValue(),
            unit.waypointReceiveRange.get().floatValue(),
            unit.waypointTransmitRange.get().floatValue(),
            mc.player.position(), mc.player.getYRot(), mc.player.getXRot());

            // Equip before the server starts tracking the clone, so the initial pairing
            // snapshot already carries the configured weapon and armor.
            if (clone.server != null)
                clone.server.equip(unit);
        }

        clone.spawn();
        ENTITIES.add(clone);
        subscribeTicker();
        return clone;
    }

    /**
     * Public API overload: wraps the arguments into a transient unit (stored nowhere) with
     * default settings and spawns it.
     */
    public static CloneEntity add(String name, float health)
    {
        return add(CloneUnit.transientOf(name, health));
    }

    /** Spawns a clone with the unit defaults. */
    public static CloneEntity add()
    {
        return add(new CloneUnit.Builder().build());
    }

    /** The clone currently running for {@code unit}, or null. */
    public static CloneEntity get(CloneUnit unit)
    {
        if (unit == null)
			return null;

        for (CloneEntity fp : ENTITIES)
        {
            if (fp.unit == unit)
				return fp;
        }

        return null;
    }

    /** Despawns the clone running for {@code unit}, if any. */
    public static void remove(CloneUnit unit)
    {
        CloneEntity fp = get(unit);
        if (fp != null) remove(fp);
    }

    public static void remove(CloneEntity fp)
    {
        ENTITIES.removeIf(fp1 ->
        {
            if (fp1.getName().getString().equals(fp.getName().getString()))
            {
                fp1.despawn();
                return true;
            }

            return false;
        });
    }

    public static void clear()
    {
        if (ENTITIES.isEmpty())
			return;

        ENTITIES.forEach(CloneEntity::despawn);
        ENTITIES.clear();
    }

    /**
     * Whether an entity is one of our clones: either a client-side pilot or the server-side twin
     * of a singleplayer clone (which lives in the client level as a normal tracked player).
     */
    public static boolean isClone(Entity entity)
    {
        for (CloneEntity fp : ENTITIES)
        {
            if (fp == entity)
				return true;
            if (fp.server != null && entity.getUUID().equals(fp.server.getUuid()))
				return true;
        }

        return false;
    }

    private static void subscribeTicker()
    {
        if (tickerSubscribed) return;
        MeteorClient.EVENT_BUS.subscribe(CloneManager.class);
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
        if (ENTITIES.isEmpty())
        return;

        // Left the world: drop stale pilots (their server clones go with the shutting-down server).
        if (!Utils.canUpdate())
        {
            clear();
            return;
        }

        for (Iterator<CloneEntity> it = ENTITIES.iterator(); it.hasNext(); )
        {
            CloneEntity fp = it.next();
            if (fp.server == null) continue;

            if (!fp.server.isAlive() || !fp.server.isInExpectedLevel())
            {
                fp.despawn();
                it.remove();
                continue;
            }

            fp.tick();
            fp.server.syncFrom(fp);
        }
    }

    public static void forEach(Consumer<CloneEntity> action)
    {
        for (CloneEntity clone : ENTITIES)
        {
            action.accept(clone);
        }
    }

    public static int count()
    {
        return ENTITIES.size();
    }

    public static Stream<CloneEntity> stream()
    {
        return ENTITIES.stream();
    }

    public static boolean contains(CloneEntity fp)
    {
        return ENTITIES.contains(fp);
    }

    /** Resolves an item id (e.g. "minecraft:netherite_sword") into an ItemStack, or empty. */
    public static ItemStack itemStackFromId(String id)
    {
        if (id == null || id.isBlank())
            return ItemStack.EMPTY;

        Identifier identifier = Identifier.tryParse(id.trim());
        if (identifier == null)
            return ItemStack.EMPTY;

        Item item = BuiltInRegistries.ITEM.getValue(identifier);
        if (item == null)
            return ItemStack.EMPTY;

        return new ItemStack(item);
    }
}
