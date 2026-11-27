/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */
package meteordevelopment.meteorclient.mc;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;

/** Resolves attribute constants added after 1.21.5 by name instead of hardcoding them. */
public final class AttributeUtils {
    private AttributeUtils() {}

    public static Holder<Attribute> get(String name) {
        try {
            Object value = Attributes.class.getField(name).get(null);
            @SuppressWarnings("unchecked") Holder<Attribute> holder = (Holder<Attribute>) value;
            return holder;
        } catch (ReflectiveOperationException e) {
            return null;
        }
    }
}
