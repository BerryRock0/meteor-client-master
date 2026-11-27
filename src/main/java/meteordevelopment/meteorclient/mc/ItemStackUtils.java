/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */
package meteordevelopment.meteorclient.mc;

import com.mojang.serialization.DataResult;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.item.ItemStack;

/** Reads an item NBT tag without depending on the 26.x {@code ItemStackWithSlot} helper. */
public final class ItemStackUtils {
    private ItemStackUtils() {}

    public static ItemStack fromTag(CompoundTag tag, RegistryAccess access) {
        try {
            DataResult<ItemStack> result = ItemStack.CODEC.parse(
                access.createSerializationContext(NbtOps.INSTANCE), tag);
            return result.result().orElse(ItemStack.EMPTY);
        } catch (Throwable t) {
            return ItemStack.EMPTY;
        }
    }
}
