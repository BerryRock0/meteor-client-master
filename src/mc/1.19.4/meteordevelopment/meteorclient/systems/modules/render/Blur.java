/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.systems.modules.render;

import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;

/** Legacy stub: the GPU-based blur pipeline is a 26.x-only feature. */
public class Blur extends Module {
    public Blur() {
        super(Categories.Render, "blur", "Blurs the background of screens (requires 26.x).");
    }
}
