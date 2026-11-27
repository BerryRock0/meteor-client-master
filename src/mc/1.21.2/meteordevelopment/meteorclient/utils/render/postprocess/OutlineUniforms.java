/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */
package meteordevelopment.meteorclient.utils.render.postprocess;

import com.mojang.blaze3d.vertex.GpuBufferSlice;

/** Legacy no-op replacement for the 1.21.6+ dynamic outline uniform storage. */
public final class OutlineUniforms {
    private OutlineUniforms() {}

    public static void flipFrame() {}

    public static GpuBufferSlice write(int width, float fillOpacity, int shapeMode, float glowMultiplier) {
        return null;
    }
}
