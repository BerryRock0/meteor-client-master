/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.mixin;

import com.mojang.renderpearl.api.pipeline.CompiledRenderPipeline;
import com.mojang.renderpearl.backend.opengl.GlDevice;
import com.mojang.renderpearl.backend.opengl.GlProgram;
import com.mojang.renderpearl.backend.opengl.VertexArray;
import meteordevelopment.meteorclient.renderer.LineSmoothPipelines;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static org.lwjgl.opengl.GL11C.*;

/**
 * Since 26.3 the polygon mode is applied by {@code GlRenderPipeline#bind()} instead of
 * {@code GlCommandEncoder#applyPipelineState(RenderPipeline, ...)}. The backend pipeline only shares its
 * name (the pipeline location) with the frontend pipeline, so the line smooth flag is looked up in
 * {@link LineSmoothPipelines} when the pipeline is constructed.
 */
@Mixin(targets = "com.mojang.renderpearl.backend.opengl.GlRenderPipeline")
public abstract class GlRenderPipelineMixin {
    @Unique
    private boolean meteor$lineSmooth;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onCreate(GlDevice device, CompiledRenderPipeline.CreateInfo createInfo, GlProgram program, VertexArray vertexArray, CallbackInfo ci) {
        meteor$lineSmooth = LineSmoothPipelines.contains(createInfo.name());
    }

    @Inject(method = "bind", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL33C;glPolygonMode(II)V"))
    private void onPolygonMode(CallbackInfo ci) {
        if (meteor$lineSmooth) {
            glEnable(GL_LINE_SMOOTH);
            glLineWidth(1);
        } else {
            glDisable(GL_LINE_SMOOTH);
        }
    }
}
