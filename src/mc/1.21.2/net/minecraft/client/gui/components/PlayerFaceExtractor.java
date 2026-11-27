/* Compatibility shim: 26.x player face render-state extractor. */
package net.minecraft.client.gui.components;

public final class PlayerFaceExtractor {
    private PlayerFaceExtractor() {}

    public static void extractRenderState(net.minecraft.client.gui.GuiGraphicsExtractor graphics,
                                          Object skin,
                                          int x, int y, int size, int color) {}

    public static void extractRenderState(net.minecraft.client.gui.GuiGraphicsExtractor graphics,
                                          Object skin,
                                          int x, int y, int size) {}
}
