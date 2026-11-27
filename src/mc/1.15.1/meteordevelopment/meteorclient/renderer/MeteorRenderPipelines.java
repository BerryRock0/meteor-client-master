/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */
package meteordevelopment.meteorclient.renderer;
import com.mojang.blaze3d.pipeline.RenderPipeline;
/** Legacy placeholder for the 26.x render pipelines. */
public final class MeteorRenderPipelines {
    public static final RenderPipeline UI_COLORED = new RenderPipeline();
    public static final RenderPipeline UI_COLORED_LINES = new RenderPipeline();
    public static final RenderPipeline UI_TEXTURED = new RenderPipeline();
    public static final RenderPipeline UI_TEXT = new RenderPipeline();
    public static final RenderPipeline WORLD_COLORED = new RenderPipeline();
    public static final RenderPipeline WORLD_COLORED_LINES = new RenderPipeline();
    public static final RenderPipeline POST_OUTLINE = new RenderPipeline();
}
