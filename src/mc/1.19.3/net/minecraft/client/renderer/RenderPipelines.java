/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */
package net.minecraft.client.renderer;
import com.mojang.blaze3d.pipeline.RenderPipeline;
/** Legacy shim: 26.x built-in render pipelines. */
public final class RenderPipelines {
    public static final RenderPipeline GUI = new RenderPipeline();
    public static final RenderPipeline GUI_TEXTURED = new RenderPipeline();
    public static final RenderPipeline LINES = new RenderPipeline();
}
