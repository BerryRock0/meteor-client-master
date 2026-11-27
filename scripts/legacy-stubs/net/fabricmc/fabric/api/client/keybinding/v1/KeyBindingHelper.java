package net.fabricmc.fabric.api.client.keybinding.v1;

import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;

/** Compile-only replacement for Fabric API's keybinding helper. */
public final class KeyBindingHelper {
    public static KeyBinding registerKeyBinding(KeyBinding keyBinding) {
        return keyBinding;
    }

    public static InputUtil.Key getBoundKeyOf(KeyBinding keyBinding) {
        return null;
    }
}
