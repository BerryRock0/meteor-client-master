/* Compatibility stub: the 26.x tooltip component API is unavailable on older versions. */
package meteordevelopment.meteorclient.utils.tooltip;

public class BookTooltipComponent implements net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent, MeteorTooltipData {
    public BookTooltipComponent(Object... args) {}

    @Override public int getHeight(net.minecraft.client.gui.Font font) { return 0; }
    @Override public int getWidth(net.minecraft.client.gui.Font font) { return 0; }
    @Override public net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent getComponent() { return this; }
}
