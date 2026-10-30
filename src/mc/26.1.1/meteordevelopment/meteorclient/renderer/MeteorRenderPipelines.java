/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.renderer;

import com.mojang.blaze3d.pipeline.BindGroupLayout;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.PrimitiveTopology;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.DepthStencilState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.CompareOp;
import com.mojang.blaze3d.shaders.UniformType;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import meteordevelopment.meteorclient.MeteorClient;
import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.resources.ResourceManager;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public abstract class MeteorRenderPipelines {
    private static final List<RenderPipeline> PIPELINES = new ArrayList<>();

    // Snippets

    private static final BindGroupLayout MESH_BIND_GROUP = BindGroupLayout.builder()
        .withUniform("MeshData", UniformType.UNIFORM_BUFFER)
        .build();

    private static final RenderPipeline.Snippet MESH_UNIFORMS = RenderPipeline.builder()
        .withBindGroupLayout(MESH_BIND_GROUP)
        .buildSnippet();

    // World

    public static final RenderPipeline WORLD_COLORED = add(new ExtendedRenderPipelineBuilder(MESH_UNIFORMS)
        .withLocation(MeteorClient.identifier("pipeline/world_colored"))
        .withVertexBinding(0, DefaultVertexFormat.POSITION_COLOR).withPrimitiveTopology(PrimitiveTopology.TRIANGLES)
        .withVertexShader(MeteorClient.identifier("pos_color"))
        .withFragmentShader(MeteorClient.identifier("pos_color"))
        .withDepthStencilState(new DepthStencilState(CompareOp.ALWAYS_PASS, false))
        .withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
        .withCull(false)
        .build()
    );

    public static final RenderPipeline WORLD_COLORED_LINES = add(new ExtendedRenderPipelineBuilder(MESH_UNIFORMS)
        .withLineSmooth()
        .withLocation(MeteorClient.identifier("pipeline/world_colored_lines"))
        .withVertexBinding(0, DefaultVertexFormat.POSITION_COLOR).withPrimitiveTopology(PrimitiveTopology.DEBUG_LINES)
        .withVertexShader(MeteorClient.identifier("pos_color"))
        .withFragmentShader(MeteorClient.identifier("pos_color"))
        .withDepthStencilState(new DepthStencilState(CompareOp.ALWAYS_PASS, false))
        .withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
        .withCull(false)
        .build()
    );

    public static final RenderPipeline WORLD_COLORED_DEPTH = add(new ExtendedRenderPipelineBuilder(MESH_UNIFORMS)
        .withLocation(MeteorClient.identifier("pipeline/world_colored_depth"))
        .withVertexBinding(0, DefaultVertexFormat.POSITION_COLOR).withPrimitiveTopology(PrimitiveTopology.TRIANGLES)
        .withVertexShader(MeteorClient.identifier("pos_color"))
        .withFragmentShader(MeteorClient.identifier("pos_color"))
        .withDepthStencilState(new DepthStencilState(DepthStencilState.DEFAULT.depthTest(), false))
        .withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
        .withCull(false)
        .build()
    );

    public static final RenderPipeline WORLD_COLORED_LINES_DEPTH = add(new ExtendedRenderPipelineBuilder(MESH_UNIFORMS)
        .withLineSmooth()
        .withLocation(MeteorClient.identifier("pipeline/world_colored_lines_depth"))
        .withVertexBinding(0, DefaultVertexFormat.POSITION_COLOR).withPrimitiveTopology(PrimitiveTopology.DEBUG_LINES)
        .withVertexShader(MeteorClient.identifier("pos_color"))
        .withFragmentShader(MeteorClient.identifier("pos_color"))
        .withDepthStencilState(new DepthStencilState(DepthStencilState.DEFAULT.depthTest(), false))
        .withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
        .withCull(false)
        .build()
    );

    // UI

    public static final RenderPipeline UI_COLORED = add(new ExtendedRenderPipelineBuilder(MESH_UNIFORMS)
        .withLocation(MeteorClient.identifier("pipeline/ui_colored"))
        .withVertexBinding(0, MeteorVertexFormats.POS2_COLOR).withPrimitiveTopology(PrimitiveTopology.TRIANGLES)
        .withVertexShader(MeteorClient.identifier("pos2_color"))
        .withFragmentShader(MeteorClient.identifier("pos_color"))
        .withDepthStencilState(new DepthStencilState(CompareOp.ALWAYS_PASS, false))
        .withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
        .withCull(true)
        .build()
    );

    public static final RenderPipeline UI_COLORED_LINES = add(new ExtendedRenderPipelineBuilder(MESH_UNIFORMS)
        .withLocation(MeteorClient.identifier("pipeline/ui_colored_lines"))
        .withVertexBinding(0, MeteorVertexFormats.POS2_COLOR).withPrimitiveTopology(PrimitiveTopology.DEBUG_LINES)
        .withVertexShader(MeteorClient.identifier("pos2_color"))
        .withFragmentShader(MeteorClient.identifier("pos_color"))
        .withDepthStencilState(new DepthStencilState(CompareOp.ALWAYS_PASS, false))
        .withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
        .withCull(true)
        .build()
    );

    public static final RenderPipeline UI_TEXTURED = add(new ExtendedRenderPipelineBuilder(MESH_UNIFORMS)
        .withLocation(MeteorClient.identifier("pipeline/ui_textured"))
        .withVertexBinding(0, MeteorVertexFormats.POS2_TEXTURE_COLOR).withPrimitiveTopology(PrimitiveTopology.TRIANGLES)
        .withVertexShader(MeteorClient.identifier("pos2_tex_color"))
        .withFragmentShader(MeteorClient.identifier("pos_tex_color"))
        .withBindGroupLayout(BindGroupLayout.builder().withSampler("u_Texture").build())
        .withDepthStencilState(new DepthStencilState(CompareOp.ALWAYS_PASS, false))
        .withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
        .withCull(true)
        .build()
    );

    public static final RenderPipeline UI_TEXT = add(new ExtendedRenderPipelineBuilder(MESH_UNIFORMS)
        .withLocation(MeteorClient.identifier("pipeline/ui_text"))
        .withVertexBinding(0, MeteorVertexFormats.POS2_TEXTURE_COLOR).withPrimitiveTopology(PrimitiveTopology.TRIANGLES)
        .withVertexShader(MeteorClient.identifier("pos2_tex_color"))
        .withFragmentShader(MeteorClient.identifier("text"))
        .withBindGroupLayout(BindGroupLayout.builder().withSampler("u_Texture").build())
        .withDepthStencilState(new DepthStencilState(CompareOp.ALWAYS_PASS, false))
        .withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
        .withCull(true)
        .build()
    );

    // Post Process

    public static final RenderPipeline POST_OUTLINE = add(new ExtendedRenderPipelineBuilder()
        .withLocation(MeteorClient.identifier("pipeline/post/outline"))
        .withVertexBinding(0, MeteorVertexFormats.POS2).withPrimitiveTopology(PrimitiveTopology.TRIANGLES)
        .withVertexShader(MeteorClient.identifier("post-process/base"))
        .withFragmentShader(MeteorClient.identifier("post-process/outline"))
        .withBindGroupLayout(BindGroupLayout.builder()
            .withSampler("u_Texture")
            .withUniform("PostData", UniformType.UNIFORM_BUFFER)
            .withUniform("OutlineData", UniformType.UNIFORM_BUFFER)
            .build())
        .withDepthStencilState(new DepthStencilState(CompareOp.ALWAYS_PASS, false))
        .withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
        .withCull(false)
        .build()
    );

    public static final RenderPipeline POST_IMAGE = add(new ExtendedRenderPipelineBuilder(MESH_UNIFORMS)
        .withLocation(MeteorClient.identifier("pipeline/post/image"))
        .withVertexBinding(0, MeteorVertexFormats.POS2).withPrimitiveTopology(PrimitiveTopology.TRIANGLES)
        .withVertexShader(MeteorClient.identifier("post-process/base"))
        .withFragmentShader(MeteorClient.identifier("post-process/image"))
        .withBindGroupLayout(BindGroupLayout.builder()
            .withSampler("u_Texture")
            .withSampler("u_TextureI")
            .withUniform("PostData", UniformType.UNIFORM_BUFFER)
            .withUniform("ImageData", UniformType.UNIFORM_BUFFER)
            .build())
        .withDepthStencilState(new DepthStencilState(CompareOp.ALWAYS_PASS, false))
        .withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
        .withCull(false)
        .build()
    );

    // Blur

    public static final RenderPipeline BLUR_DOWN = add(new ExtendedRenderPipelineBuilder(MESH_UNIFORMS)
        .withLocation(MeteorClient.identifier("pipeline/blur/down"))
        .withVertexBinding(0, MeteorVertexFormats.POS2).withPrimitiveTopology(PrimitiveTopology.TRIANGLES)
        .withVertexShader(MeteorClient.identifier("blur"))
        .withFragmentShader(MeteorClient.identifier("blur_down"))
        .withBindGroupLayout(BindGroupLayout.builder()
            .withSampler("u_Texture")
            .withUniform("BlurData", UniformType.UNIFORM_BUFFER)
            .build())
        .withDepthStencilState(new DepthStencilState(CompareOp.ALWAYS_PASS, false))
        .withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
        .withCull(false)
        .build()
    );

    public static final RenderPipeline BLUR_UP = add(new ExtendedRenderPipelineBuilder(MESH_UNIFORMS)
        .withLocation(MeteorClient.identifier("pipeline/blur/up"))
        .withVertexBinding(0, MeteorVertexFormats.POS2).withPrimitiveTopology(PrimitiveTopology.TRIANGLES)
        .withVertexShader(MeteorClient.identifier("blur"))
        .withFragmentShader(MeteorClient.identifier("blur_up"))
        .withBindGroupLayout(BindGroupLayout.builder()
            .withSampler("u_Texture")
            .withUniform("BlurData", UniformType.UNIFORM_BUFFER)
            .build())
        .withDepthStencilState(new DepthStencilState(CompareOp.ALWAYS_PASS, false))
        .withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
        .withCull(false)
        .build()
    );

    public static final RenderPipeline BLUR_PASSTHROUGH = add(new ExtendedRenderPipelineBuilder(MESH_UNIFORMS)
        .withLocation(MeteorClient.identifier("pipeline/blur/up"))
        .withVertexBinding(0, MeteorVertexFormats.POS2).withPrimitiveTopology(PrimitiveTopology.TRIANGLES)
        .withVertexShader(MeteorClient.identifier("passthrough"))
        .withFragmentShader(MeteorClient.identifier("passthrough"))
        .withBindGroupLayout(BindGroupLayout.builder().withSampler("u_Texture").build())
        .withDepthStencilState(new DepthStencilState(CompareOp.ALWAYS_PASS, false))
        .withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
        .withCull(false)
        .build()
    );

    private static RenderPipeline add(RenderPipeline pipeline) {
        PIPELINES.add(pipeline);
        return pipeline;
    }

    public static void precompile() {
        GpuDevice device = RenderSystem.getDevice();
        ResourceManager resources = Minecraft.getInstance().getResourceManager();

        for (RenderPipeline pipeline : PIPELINES) {
            try {
                // Pipelines declare bare shader ids (e.g. "pos_color"); the game resolves them to
                // "shaders/<id>.vsh|.fsh" resources via the shader type converter.
                device.precompilePipeline(pipeline, (identifier, type) -> {
                    var file = type.idConverter().idToFile(identifier);

                    var resource = resources.getResource(file).orElseThrow(
                        () -> new IllegalStateException("Shader resource not found: " + file)
                    );

                    try (var in = resource.open()) {
                        return IOUtils.toString(in, StandardCharsets.UTF_8);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            } catch (Exception e) {
                // Never let a pipeline warm-up failure break the resource reload.
                MeteorClient.LOG.error("Failed to precompile pipeline {}", pipeline.getLocation(), e);
            }
        }
    }

    private MeteorRenderPipelines() {
    }
}
