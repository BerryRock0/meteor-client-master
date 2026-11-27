/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */
package meteordevelopment.meteorclient.renderer;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.textures.GpuSampler;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.GpuBuffer;
import com.mojang.blaze3d.vertex.GpuBufferSlice;
import com.mojang.blaze3d.vertex.PoseStack;
import org.joml.Matrix4f;

/** Legacy implementation of the 26.x MeshRenderer builder. */
public final class MeshRenderer {
    private MeshBuilder mesh;
    private PoseStack matrices;

    public static MeshRenderer begin() { return new MeshRenderer(); }

    public MeshRenderer attachments(Object color, Object depth) { return this; }
    public MeshRenderer attachments(Object framebuffer) { return this; }
    public MeshRenderer clearColor(Object color) { return this; }
    public MeshRenderer pipeline(Object pipeline) { return this; }
    public MeshRenderer mesh(GpuBuffer vertices, GpuBuffer indices) { return this; }
    public MeshRenderer mesh(MeshBuilder mesh) { this.mesh = mesh; return this; }
    public MeshRenderer mesh(MeshBuilder mesh, Matrix4f matrix) { this.mesh = mesh; return this; }
    public MeshRenderer mesh(MeshBuilder mesh, PoseStack matrices) { this.mesh = mesh; this.matrices = matrices; return this; }
    public MeshRenderer transform(Matrix4f matrix) { return this; }
    public MeshRenderer transform(PoseStack matrices) { this.matrices = matrices; return this; }
    public MeshRenderer fullscreen() { return this; }
    public MeshRenderer uniform(String name, Object slice) { return this; }
    public MeshRenderer sampler(String name, GpuTextureView view, GpuSampler sampler) { return this; }

    public void end() {
        if (mesh != null) mesh.renderLegacy(matrices);
    }
}
