/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */
package meteordevelopment.meteorclient.renderer;

/** Legacy placeholder for the 26.x render pipelines. */
public final class MeteorRenderPipelines {
    public static final DrawMode UI_COLORED = DrawMode.Triangles;
    public static final DrawMode UI_COLORED_LINES = DrawMode.Lines;
    public static final DrawMode UI_TEXTURED = DrawMode.Triangles;
    public static final DrawMode UI_TEXT = DrawMode.Triangles;
    public static final DrawMode WORLD_COLORED = DrawMode.Triangles;
    public static final DrawMode WORLD_COLORED_LINES = DrawMode.Lines;
    public static final DrawMode WORLD_COLORED_LINES_DEPTH = DrawMode.Lines;
    public static final DrawMode WORLD_COLORED_DEPTH = DrawMode.Triangles;
    public static final DrawMode POST_OUTLINE = DrawMode.Triangles;
    public static void precompile() {}
}
