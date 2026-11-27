/* Compatibility placeholder for the 26.x block tint source. */
package net.minecraft.client.color.block;

@FunctionalInterface
public interface BlockTintSource {
    int getTint(int defaultColor);
}
