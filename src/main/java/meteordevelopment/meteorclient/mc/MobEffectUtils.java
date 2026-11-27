/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */
package meteordevelopment.meteorclient.mc;

import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;

/** Resolves effect constants by their 26.x name across older mappings. */
public final class MobEffectUtils {
    private MobEffectUtils() {}

    private static final java.util.Map<String, String> LEGACY = java.util.Map.ofEntries(
        java.util.Map.entry("SPEED", "MOVEMENT_SPEED"),
        java.util.Map.entry("SLOWNESS", "MOVEMENT_SLOWDOWN"),
        java.util.Map.entry("HASTE", "DIG_SPEED"),
        java.util.Map.entry("MINING_FATIGUE", "DIG_SLOWDOWN"),
        java.util.Map.entry("JUMP_BOOST", "JUMP"),
        java.util.Map.entry("STRENGTH", "DAMAGE_BOOST"),
        java.util.Map.entry("RESISTANCE", "DAMAGE_RESISTANCE")
    );

    @SuppressWarnings("unchecked")
    public static Holder<MobEffect> get(String name) {
        for (String candidate : new String[] { name, LEGACY.getOrDefault(name, name) }) {
            try {
                return (Holder<MobEffect>) MobEffects.class.getField(candidate).get(null);
            } catch (ReflectiveOperationException ignored) {
            }
        }
        return null;
    }
}
