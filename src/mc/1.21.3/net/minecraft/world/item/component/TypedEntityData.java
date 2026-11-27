/* Compatibility placeholder for the 26.x typed entity data component. */
package net.minecraft.world.item.component;

public final class TypedEntityData<T> {
    public net.minecraft.nbt.CompoundTag copyTag() { return new net.minecraft.nbt.CompoundTag(); }
    public net.minecraft.nbt.CompoundTag copyTagWithoutId() { return new net.minecraft.nbt.CompoundTag(); }
    public boolean contains(String key) { return false; }
    public net.minecraft.nbt.CompoundTag data() { return new net.minecraft.nbt.CompoundTag(); }
}
