/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.renderer;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexFormat;
import meteordevelopment.meteorclient.mc.Vanilla;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.world.phys.Vec3;
import org.lwjgl.BufferUtils;

import java.nio.ByteBuffer;

import static meteordevelopment.meteorclient.MeteorClient.mc;
import static org.lwjgl.system.MemoryUtil.*;

public class MeshBuilder {
    private static final boolean DEBUG = FabricLoader.getInstance().isDevelopmentEnvironment() || Boolean.getBoolean("meteor.render.debug");

    public double alpha = 1;

    private final VertexFormat format;
    private final int primitiveVerticesSize;
    private final int primitiveIndicesCount;

    private ByteBuffer vertices = null;
    private long verticesPointerStart, verticesPointer;

    private ByteBuffer indices = null;
    private long indicesPointer;

    private int vertexI, indicesCount;

    private boolean building;
    private double cameraX, cameraZ;

    public MeshBuilder(RenderPipeline pipeline) {
        this(pipeline.getVertexFormat(), pipeline.getVertexFormatMode());
    }

    public MeshBuilder(VertexFormat format, VertexFormat.Mode drawMode) {
        this.format = format;
        primitiveVerticesSize = format.getVertexSize();
        primitiveIndicesCount = drawMode.primitiveLength;
    }

    public MeshBuilder(VertexFormat format, VertexFormat.Mode drawMode, int vertexCount, int indexCount) {
        this(format, drawMode);
        allocateBuffers(vertexCount, indexCount);
    }

    public void begin() {
        if (building) throw new IllegalStateException("Mesh.begin() called while already building.");

        verticesPointer = verticesPointerStart;
        vertexI = 0;
        indicesCount = 0;

        building = true;

        if (Utils.rendering3D) {
            Vec3 camera = Vanilla.mainCamera().position();

            cameraX = camera.x;
            cameraZ = camera.z;
        } else {
            cameraX = 0;
            cameraZ = 0;
        }
    }

    public void end() {
        if (!building) throw new IllegalStateException("Mesh.end() called while not building.");
        building = false;
    }

    public boolean isBuilding() {
        return building;
    }

    public void ensureCapacity(int vertexCount, int indexCount) {
        if (vertices == null || vertexCount * primitiveVerticesSize > vertices.capacity() || indexCount * 4 > (indices != null ? indices.capacity() : 0)) {
            allocateBuffers(vertexCount, indexCount);
        }
    }

    public void allocateBuffers(int vertexCount, int indexCount) {
        vertices = BufferUtils.createByteBuffer(vertexCount * primitiveVerticesSize);
        indices = BufferUtils.createByteBuffer(indexCount * 4);

        verticesPointerStart = memAddress(vertices);
        verticesPointer = verticesPointerStart;
        indicesPointer = memAddress(indices);

        vertexI = 0;
        indicesCount = 0;
    }

    public int getPrimitiveVerticesSize() {
        return primitiveVerticesSize;
    }

    public int getPrimitiveIndicesCount() {
        return primitiveIndicesCount;
    }

    public int getVerticesCount() {
        return vertexI;
    }

    public int getIndicesCount() {
        return indicesCount;
    }

    public int getVerticesSize() {
        return primitiveVerticesSize * vertexI;
    }

    public long getVerticesPointer() {
        return verticesPointer;
    }

    public long getIndicesPointer() {
        return indicesPointer;
    }

    public void putIndex(int index) {
        memPutInt(indicesPointer, index);
        indicesPointer += 4;
        indicesCount++;
    }

    public void advanceVertex() {
        vertexI++;
        verticesPointer += primitiveVerticesSize;
    }

    public ByteBuffer getVertices() {
        return vertices;
    }

    public ByteBuffer getIndices() {
        return indices;
    }

    public GpuBuffer uploadVertexBuffer(CommandEncoder encoder) {
        GpuBuffer buffer = RenderSystem.getDevice().createBuffer(() -> "Meteor MeshBuilder Vertex Buffer", primitiveVerticesSize * vertexI, GpuBuffer.Usage.VERTEX | GpuBuffer.Usage.DYNAMIC);
        encoder.writeToBuffer(buffer, 0, vertices);
        return buffer;
    }

    public GpuBuffer uploadIndexBuffer(CommandEncoder encoder) {
        GpuBuffer buffer = RenderSystem.getDevice().createBuffer(() -> "Meteor MeshBuilder Index Buffer", 4 * indicesCount, GpuBuffer.Usage.INDEX | GpuBuffer.Usage.DYNAMIC);
        encoder.writeToBuffer(buffer, 0, indices);
        return buffer;
    }

    public GpuBuffer getVertexBuffer() {
        vertices.limit(getVerticesOffset());
        return RenderSystem.getDevice().createBuffer(() -> "Meteor MeshBuilder Vertex Buffer", GpuBuffer.Usage.VERTEX, vertices);
    }

    public GpuBuffer getIndexBuffer() {
        indices.limit(indicesCount * 4);
        return RenderSystem.getDevice().createBuffer(() -> "Meteor MeshBuilder Index Buffer", GpuBuffer.Usage.INDEX, indices);
    }

    private int getVerticesOffset() {
        return (int) (verticesPointer - verticesPointerStart);
    }
}