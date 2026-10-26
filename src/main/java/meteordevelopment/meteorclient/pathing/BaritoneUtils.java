/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.pathing;

import meteordevelopment.meteorclient.MeteorClient;
import net.minecraft.core.BlockPos;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * Optional runtime bridge to Baritone.
 *
 * <p>Baritone is not a dependency of the mod, so everything here goes through reflection: when
 * Baritone is not installed, {@link #IS_AVAILABLE} is false and every method degrades into a
 * cheap no-op that lets the caller fall back to its own movement.
 *
 * <p>Baritone can only drive the local player, so this utility is for modules that move the real
 * player (e.g. FakePlayer's mine/fight autopilot). Client-side entities keep their own built-in
 * movement.
 */
public final class BaritoneUtils {
    /** Whether Baritone's API is present in this run. */
    public static final boolean IS_AVAILABLE;

    private static Object customGoalProcess;
    private static Object pathingBehavior;

    private static Method setGoalAndPath;
    private static Method isPathing;
    private static Method cancelEverything;

    private static Constructor<?> goalGetToBlock;
    private static Constructor<?> goalNear;

    static {
        boolean available = false;

        try {
            Object provider = Class.forName("baritone.api.BaritoneAPI")
                .getMethod("getProvider").invoke(null);
            Object primary = provider.getClass().getMethod("getPrimaryBaritone").invoke(provider);

            customGoalProcess = primary.getClass().getMethod("getCustomGoalProcess").invoke(primary);
            pathingBehavior = primary.getClass().getMethod("getPathingBehavior").invoke(primary);

            setGoalAndPath = Class.forName("baritone.api.process.ICustomGoalProcess")
                .getMethod("setGoalAndPath", Class.forName("baritone.api.pathing.goals.Goal"));
            isPathing = pathingBehavior.getClass().getMethod("isPathing");
            cancelEverything = pathingBehavior.getClass().getMethod("cancelEverything");

            goalGetToBlock = Class.forName("baritone.api.pathing.goals.GoalGetToBlock").getConstructor(BlockPos.class);
            goalNear = Class.forName("baritone.api.pathing.goals.GoalNear").getConstructor(int.class, int.class, int.class, int.class);

            available = true;
        } catch (Throwable t) {
            // No Baritone (or an incompatible build) - callers use their fallback movement.
            MeteorClient.LOG.debug("Baritone API not available: {}", t.toString());
        }

        IS_AVAILABLE = available;
    }

    private BaritoneUtils() {
    }

    /** Whether Baritone is currently pathing the local player. */
    public static boolean isPathing() {
        if (!IS_AVAILABLE) return false;

        try {
            return (boolean) isPathing.invoke(pathingBehavior);
        } catch (Throwable t) {
            return false;
        }
    }

    /**
     * Paths the local player to get next to {@code pos} (Baritone's {@code GoalGetToBlock}).
     * Returns false when Baritone is unavailable or the goal could not be created.
     */
    public static boolean pathToBlock(BlockPos pos) {
        if (!IS_AVAILABLE) return false;

        try {
            setGoalAndPath.invoke(customGoalProcess, goalGetToBlock.newInstance(pos));
            return true;
        } catch (Throwable t) {
            MeteorClient.LOG.debug("Failed to set Baritone GoalGetToBlock: {}", t.toString());
            return false;
        }
    }

    /**
     * Paths the local player towards {@code pos}, stopping within {@code range} blocks
     * (Baritone's {@code GoalNear}). Returns false when Baritone is unavailable or the goal
     * could not be created.
     */
    public static boolean pathNear(BlockPos pos, int range) {
        if (!IS_AVAILABLE) return false;

        try {
            setGoalAndPath.invoke(customGoalProcess, goalNear.newInstance(pos.getX(), pos.getY(), pos.getZ(), range));
            return true;
        } catch (Throwable t) {
            MeteorClient.LOG.debug("Failed to set Baritone GoalNear: {}", t.toString());
            return false;
        }
    }

    /** Cancels Baritone's current pathing. */
    public static void cancel() {
        if (!IS_AVAILABLE) return;

        try {
            cancelEverything.invoke(pathingBehavior);
        } catch (Throwable ignored) {
        }
    }
}
