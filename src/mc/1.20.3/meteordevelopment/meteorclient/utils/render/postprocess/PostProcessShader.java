/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.utils.render.postprocess;

import meteordevelopment.meteorclient.renderer.MeshRenderer;

/**
 * Compatibility stub: the 26.x render-pipeline post-process API is unavailable on older versions.
 */
public abstract class PostProcessShader {
    protected PostProcessShader(Object pipeline) {}

    protected boolean shouldDraw() { return false; }

    public boolean shouldDraw(net.minecraft.world.entity.Entity entity) { return shouldDraw(); }

    protected void setupPass(MeshRenderer renderer) {}

    public void clearTexture() {}
    public void submitVertices() {}
    public void render() {}
    public void onResized(int width, int height) {}
}
