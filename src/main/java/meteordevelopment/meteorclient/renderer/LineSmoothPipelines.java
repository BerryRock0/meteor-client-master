/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.renderer;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Since 26.3 the OpenGL backend no longer receives the frontend {@code RenderPipeline} when it applies pipeline
 * state (the state is baked into a {@code GlRenderPipeline} at compile time). The only shared value between the
 * frontend pipeline and its backend counterpart is the pipeline name, which is its location. Pipelines that want
 * line smoothing register their location here when they are built; the backend mixin then enables
 * {@code GL_LINE_SMOOTH} when such a pipeline is bound.
 */
public class LineSmoothPipelines {
    private static final Set<String> NAMES = ConcurrentHashMap.newKeySet();

    private LineSmoothPipelines() {}

    public static void add(String location) {
        NAMES.add(location);
    }

    public static boolean contains(String name) {
        return NAMES.contains(name);
    }
}
