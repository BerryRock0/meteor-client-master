package com.mojang.blaze3d.vertex;

import org.joml.Matrix4f;
import org.joml.Matrix3f;

/** Legacy shim: 1.14.4 has no PoseStack; implement the minimal 26.x API. */
public class PoseStack {
    public static final class Pose {
        public final Matrix4f position = new Matrix4f();
        public final Matrix3f normal = new Matrix3f();
        public Pose(Matrix4f p, Matrix3f n) { position.set(p); normal.set(n); }
    }
    private final java.util.ArrayDeque<Pose> stack = new java.util.ArrayDeque<>();

    public PoseStack() { stack.push(new Pose(new Matrix4f(), new Matrix3f())); }

    public void pushPose() { stack.push(new Pose(stack.peek().position, stack.peek().normal)); }
    public void popPose() { stack.pop(); }
    public Pose peek() { return stack.peek(); }
    public Pose last() { return stack.peek(); }
    public void translate(double x, double y, double z) { stack.peek().position.translate((float)x,(float)y,(float)z); }
    public void scale(double x, double y, double z) { stack.peek().position.scale((float)x,(float)y,(float)z); }
    public void mulPose(Matrix4f m) { stack.peek().position.mul(m); }
    public void multiply(Matrix4f m) { mulPose(m); }
    public int getPoseCount() { return stack.size(); }
    public void setPose(Pose p) { stack.clear(); stack.push(new Pose(p.position, p.normal)); }
    public void reset() { stack.clear(); stack.push(new Pose(new Matrix4f(), new Matrix3f())); }
}
