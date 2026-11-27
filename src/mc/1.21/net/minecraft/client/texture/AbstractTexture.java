/*
 * Compatibility shim: this Minecraft tree does not ship AbstractTexture under this name.
 */
package net.minecraft.client.texture;

import static org.lwjgl.opengl.GL11C.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11C.glBindTexture;
import static org.lwjgl.opengl.GL11C.glGenTextures;

public abstract class AbstractTexture {
    private final int id = glGenTextures();

    public void bindTexture() {
        glBindTexture(GL_TEXTURE_2D, id);
    }

    public int getId() {
        return id;
    }
}
