/* Compatibility shim: 26.x item stack template used by compact shulker tooltips. */
package net.minecraft.world.item;

public final class ItemStackTemplate {
    public net.minecraft.core.Holder<net.minecraft.world.item.Item> item() { return null; }
    public int count() { return 0; }
    public net.minecraft.world.item.ItemStack create() { return net.minecraft.world.item.ItemStack.EMPTY; }
}
