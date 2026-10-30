/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.mixin;

import com.mojang.renderpearl.backend.api.GpuDeviceBackend;
import com.mojang.renderpearl.backend.opengl.GlDevice;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.renderpearl.backend.api.RenderPassBackend;
import meteordevelopment.meteorclient.mixininterface.IGpuDevice;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(targets = "com.mojang.renderpearl.backend.opengl.GlCommandEncoder")
public abstract class GlCommandEncoderMixin {
    @Shadow
    @Final
    private GlDevice device;

    @SuppressWarnings("deprecation")
    @Inject(method = "createRenderPass(Lcom/mojang/renderpearl/api/commands/RenderPassDescriptor;)Lcom/mojang/renderpearl/backend/api/RenderPassBackend;", at = @At("RETURN"))
    private void createRenderPass$iGpuDevice(CallbackInfoReturnable<RenderPassBackend> cir) {
        ((IGpuDevice) device).meteor$onCreateRenderPass(cir.getReturnValue());
    }
}
