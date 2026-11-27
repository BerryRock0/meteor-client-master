package net.fabricmc.fabric.api.client.keybinding;

import net.minecraft.client.options.KeyBinding;

/** Compile-only replacement for the old Fabric keybinding registry. */
public final class KeyBindingRegistry {
    public static final KeyBindingRegistry INSTANCE = new KeyBindingRegistry();

    public void addCategory(String category) {}

    public void register(KeyBinding keyBinding) {}
}
