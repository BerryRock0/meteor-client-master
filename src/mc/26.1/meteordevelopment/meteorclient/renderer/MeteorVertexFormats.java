/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.renderer;

import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;

public abstract class MeteorVertexFormats {
    // POS2: 2D position (x, y)
    public static final VertexFormat POS2 = VertexFormat.builder()
        .add("Position", VertexFormatElement.POSITION)
        .build();

    // POS2_COLOR: 2D position + RGBA color
    public static final VertexFormat POS2_COLOR = VertexFormat.builder()
        .add("Position", VertexFormatElement.POSITION)
        .add("Color", VertexFormatElement.COLOR)
        .build();

    // POS2_TEXTURE_COLOR: 2D position + UV texture coords + RGBA color
    public static final VertexFormat POS2_TEXTURE_COLOR = VertexFormat.builder()
        .add("Position", VertexFormatElement.POSITION)
        .add("Texture", VertexFormatElement.UV0)
        .add("Color", VertexFormatElement.COLOR)
        .build();

    private MeteorVertexFormats() {}
}
