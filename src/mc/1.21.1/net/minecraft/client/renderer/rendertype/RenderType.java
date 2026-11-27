/* Compatibility alias for the 26.x RenderType package move. */
package net.minecraft.client.renderer.rendertype;

public abstract class RenderType extends net.minecraft.client.renderer.RenderType {
    public RenderType(String name, int vertexSize, boolean crumbling, boolean sorting, Runnable setup, Runnable teardown) {
        super(name, vertexSize, crumbling, sorting, setup, teardown);
    }
}
