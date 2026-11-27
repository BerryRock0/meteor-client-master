/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */
package com.mojang.blaze3d.textures;
/** Legacy shim: 26.x GPU texture. */
public interface GpuTexture extends GpuTextureView {
    int getWidth(int level);
    int getHeight(int level);
}
