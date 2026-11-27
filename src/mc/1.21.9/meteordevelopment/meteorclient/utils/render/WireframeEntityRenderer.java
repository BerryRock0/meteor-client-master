/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.utils.render;

import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.utils.render.color.Color;

/** Compatibility stub: the 26.x submit-node wireframe renderer is unavailable on older versions. */
public final class WireframeEntityRenderer {
    private WireframeEntityRenderer() {}

    public static void render(Render3DEvent event, net.minecraft.world.entity.Entity entity, double scale,
                              Color sideColor, Color lineColor, ShapeMode shapeMode) {
    }
}
