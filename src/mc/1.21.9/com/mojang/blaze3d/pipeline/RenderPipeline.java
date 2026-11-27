/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */
package com.mojang.blaze3d.pipeline;
/** Legacy shim: 26.x pipeline descriptor. */
public final class RenderPipeline {
    public static final class Builder {
        public Builder() {}
        public Builder vertexFormat(Object format) { return this; }
        public Builder depthTest(boolean v) { return this; }
        public Builder blend(Object v) { return this; }
        public Builder cull(boolean v) { return this; }
        public Builder target(Object v) { return this; }
        public RenderPipeline build() { return new RenderPipeline(); }
    }
    public static Builder builder() { return new Builder(); }
}
