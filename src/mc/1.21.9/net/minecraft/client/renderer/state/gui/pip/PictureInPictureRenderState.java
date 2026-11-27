/* Compatibility placeholder for the 26.x picture-in-picture render state. */
package net.minecraft.client.renderer.state.gui.pip;

public interface PictureInPictureRenderState {
    static net.minecraft.client.gui.navigation.ScreenRectangle getBounds(int x0, int y0, int x1, int y1, Object scissorArea) {
        return new net.minecraft.client.gui.navigation.ScreenRectangle(x0, y0, x1 - x0, y1 - y0);
    }
}
