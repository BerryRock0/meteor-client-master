/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.utils.entity;

import meteordevelopment.meteorclient.mc.TypeIds;
import meteordevelopment.meteorclient.mc.Vanilla;

import java.util.Set;

/**
 * Matches entities, items and block entities by namespaced identifier strings.
 *
 * <p>Type constants differ between Minecraft versions. Matching by identifier keeps the same
 * behaviour everywhere: a type that does not exist in a given version simply never matches.</p>
 */
public final class TypeMatch {
    private TypeMatch() {}

    public static String id(Object type) {
        return Vanilla.typeId(type);
    }

    public static boolean isIn(Set<String> ids, Object type) {
        String id = Vanilla.typeId(type);
        if (id.isEmpty()) return false;
        return ids.contains(id) || ids.contains(id.substring(id.indexOf(':') + 1));
    }

    public static boolean isWeapon(Object stackOrItem) {
        return isIn(TypeIds.WEAPONS, stackOrItem);
    }

    public static boolean isShulkerBox(Object item) {
        return isIn(TypeIds.SHULKER_BOXES, item);
    }
}
