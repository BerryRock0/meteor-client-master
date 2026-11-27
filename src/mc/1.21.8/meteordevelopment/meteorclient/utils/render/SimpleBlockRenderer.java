/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.utils.render;

/**
 * Compatibility stub: the 26.x submit-node renderer is unavailable on older versions.
 */
public final class SimpleBlockRenderer {
    private SimpleBlockRenderer() {}

    public static void renderWithBlockEntity(net.minecraft.world.level.block.entity.BlockEntity blockEntity,
                                             float tickDelta, IVertexConsumerProvider provider) {
    }

    public static void render(net.minecraft.core.BlockPos pos, net.minecraft.world.level.block.state.BlockState state,
                              IVertexConsumerProvider provider) {
    }
}
