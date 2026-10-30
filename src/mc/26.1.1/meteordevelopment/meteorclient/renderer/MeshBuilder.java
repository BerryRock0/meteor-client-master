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

    public MeshBuilder vec3(double x, double y, double z) {
        debugVertexBufferCapacity();

        long p = verticesPointer;

        memPutFloat(p, (float) (x - cameraX));
        memPutFloat(p + 4, (float) y);
        memPutFloat(p + 8, (float) (z - cameraZ));

        verticesPointer += 12;
        return this;
    }

    public MeshBuilder vec2(double x, double y) {
        debugVertexBufferCapacity();

        long p = verticesPointer;

        memPutFloat(p, (float) x);
        memPutFloat(p + 4, (float) y);

        verticesPointer += 8;
        return this;
    }

    public MeshBuilder color(Color c) {
        debugVertexBufferCapacity();

        long p = verticesPointer;

        memPutByte(p, (byte) c.r);
        memPutByte(p + 1, (byte) c.g);
        memPutByte(p + 2, (byte) c.b);
        memPutByte(p + 3, (byte) (c.a * (float) alpha));

        verticesPointer += 4;
        return this;
    }

    public int next() {
        return vertexI++;
    }

    public void line(int i1, int i2) {
        debugIndexBufferCapacity();

        long p = indicesPointer + indicesCount * 4L;

        memPutInt(p, i1);
        memPutInt(p + 4, i2);

        indicesCount += 2;
    }

    public void quad(int i1, int i2, int i3, int i4) {
        debugIndexBufferCapacity();

        long p = indicesPointer + indicesCount * 4L;

        memPutInt(p, i1);
        memPutInt(p + 4, i2);
        memPutInt(p + 8, i3);

        memPutInt(p + 12, i3);
        memPutInt(p + 16, i4);
        memPutInt(p + 20, i1);

        indicesCount += 6;
    }

    public void triangle(int i1, int i2, int i3) {
        debugIndexBufferCapacity();

        long p = indicesPointer + indicesCount * 4L;

        memPutInt(p, i1);
        memPutInt(p + 4, i2);
        memPutInt(p + 8, i3);

        indicesCount += 3;
    }

    public void ensureQuadCapacity() {
        ensureCapacity(4, 6);
    }

    public void ensureTriCapacity() {
        ensureCapacity(3, 3);
    }

    public void ensureLineCapacity() {
        ensureCapacity(2, 2);
    }

    public void ensureCapacity(int vertexCount, int indexCount) {
        if (DEBUG && (indexCount % primitiveIndicesCount != 0)) {
            throw new IllegalArgumentException("Unexpected amount of indices written to MeshBuilder.");
        }

        if (vertices == null || indices == null) {
            allocateBuffers(256 * 4, 512 * 4);
            return;
        }

        if ((vertexI + vertexCount) * primitiveVerticesSize >= vertices.capacity()) {
            int offset = getVerticesOffset();
            int newSize = Math.max(vertices.capacity() * 2, vertices.capacity() + vertexCount * primitiveVerticesSize);
            ByteBuffer newVertices = BufferUtils.createByteBuffer(newSize);
            memCopy(memAddress0(vertices), memAddress0(newVertices), offset);

            vertices = newVertices;
            verticesPointerStart = memAddress0(vertices);
            verticesPointer = verticesPointerStart + offset;
        }

        if ((indicesCount + indexCount) * Integer.BYTES >= indices.capacity()) {
            int newSize = Math.max(indices.capacity() * 2, indices.capacity() + indexCount * Integer.BYTES);

            ByteBuffer newIndices = BufferUtils.createByteBuffer(newSize);
            memCopy(memAddress0(indices), memAddress0(newIndices), indicesCount * 4L);

            indices = newIndices;
            indicesPointer = memAddress0(indices);
        }
    }

    private void allocateBuffers(int vertexCount, int indexCount) {
        vertices = BufferUtils.createByteBuffer(primitiveVerticesSize * vertexCount);
        verticesPointer = verticesPointerStart = memAddress0(vertices);

        indices = BufferUtils.createByteBuffer(indexCount * Integer.BYTES);
        indicesPointer = memAddress0(indices);
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

    private void debugVertexBufferCapacity() {
        if (DEBUG && (vertices == null || (verticesPointer - verticesPointerStart + primitiveVerticesSize) > vertices.capacity())) {
            throw new IndexOutOfBoundsException("Vertices written to MeshBuilder without calling 'ensureCapacity()' first!");
        }
    }

    private void debugIndexBufferCapacity() {
        if (DEBUG && (indices == null || (indicesCount + primitiveIndicesCount) * Integer.BYTES > indices.capacity())) {
            throw new IndexOutOfBoundsException("Indices written to MeshBuilder without calling 'ensureCapacity()' first!");
        }
    }
}