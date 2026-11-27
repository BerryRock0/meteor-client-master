/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.utils.misc.input;

import com.mojang.blaze3d.platform.InputConstants;
import meteordevelopment.meteorclient.mc.Vanilla;
import net.minecraft.client.KeyMapping;

public class KeyBinds {
    public static KeyMapping OPEN_GUI = Vanilla.createKeyMapping("key.meteor-client.open-gui", Vanilla.KEY_TYPE, InputConstants.KEY_RSHIFT);
    public static KeyMapping OPEN_COMMANDS = Vanilla.createKeyMapping("key.meteor-client.open-commands", Vanilla.KEY_TYPE, InputConstants.KEY_PERIOD);

    private KeyBinds() {
    }

    public static KeyMapping[] apply(KeyMapping[] binds) {
        // Add key binding
        KeyMapping[] newBinds = new KeyMapping[binds.length + 2];

        System.arraycopy(binds, 0, newBinds, 0, binds.length);
        newBinds[binds.length] = OPEN_GUI;
        newBinds[binds.length + 1] = OPEN_COMMANDS;

        return newBinds;
    }
}
