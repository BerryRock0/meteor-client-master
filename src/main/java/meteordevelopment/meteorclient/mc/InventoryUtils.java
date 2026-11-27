/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */
package meteordevelopment.meteorclient.mc;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/** Bridges Inventory API changes introduced after 1.21.4. */
public final class InventoryUtils {
    private InventoryUtils() {}

    public static int getSelectedSlot(Inventory inventory) {
        try {
            Method m = inventory.getClass().getMethod("getSelectedSlot");
            return (int) m.invoke(inventory);
        } catch (ReflectiveOperationException e) {
            try {
                return (int) Inventory.class.getField("selected").get(inventory);
            } catch (ReflectiveOperationException ignored) {
                return 0;
            }
        }
    }

    public static void setSelectedSlot(Inventory inventory, int slot) {
        try {
            inventory.getClass().getMethod("setSelectedSlot", int.class).invoke(inventory, slot);
        } catch (ReflectiveOperationException e) {
            try {
                Inventory.class.getField("selected").setInt(inventory, slot);
            } catch (ReflectiveOperationException ignored) {
            }
        }
    }

    public static ItemStack getSelectedItem(Inventory inventory) {
        return inventory.getItem(getSelectedSlot(inventory));
    }

    public static List<ItemStack> getNonEquipmentItems(Inventory inventory) {
        try {
            @SuppressWarnings("unchecked")
            List<ItemStack> items = (List<ItemStack>) inventory.getClass().getMethod("getNonEquipmentItems").invoke(inventory);
            return items;
        } catch (ReflectiveOperationException e) {
            List<ItemStack> items = new ArrayList<>();
            for (int i = 0; i < inventory.getContainerSize(); i++) items.add(inventory.getItem(i));
            return items;
        }
    }
}
