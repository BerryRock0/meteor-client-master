/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */
package meteordevelopment.meteorclient.mc;

import net.minecraft.world.phys.Vec3;

import java.lang.reflect.Method;

/** Bridges the {@code Camera.position()} / {@code Camera.getPosition()} rename. */
public final class CameraUtils {
    private CameraUtils() {}

    public static Vec3 position(Object camera) {
        for (String name : new String[]{"position", "getPosition"}) {
            try {
                Method m = camera.getClass().getMethod(name);
                m.setAccessible(true);
                return (Vec3) m.invoke(camera);
            } catch (ReflectiveOperationException ignored) {
            }
        }
        throw new IllegalStateException("Camera has neither position() nor getPosition()");
    }
}
