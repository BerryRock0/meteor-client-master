/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.gui.screens;

import meteordevelopment.meteorclient.compat.Compat;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.WindowScreen;
import meteordevelopment.meteorclient.gui.widgets.containers.WHorizontalList;
import meteordevelopment.meteorclient.gui.widgets.pressable.WButton;
import net.minecraft.SharedConstants;
import net.minecraft.client.gui.screens.Screen;

public class ProtocolVersionScreen extends WindowScreen {
    public ProtocolVersionScreen(GuiTheme theme, Screen parent) {
        super(theme, "Protocol Version");
        this.parent = parent;
    }

    @Override
    public void initWidgets() {
        int protocolVersion = Compat.getProtocolVersion();
        int releaseProtocolVersion = SharedConstants.RELEASE_NETWORK_PROTOCOL_VERSION;
        int snapshotProtocolVersion = SharedConstants.SNAPSHOT_NETWORK_PROTOCOL_VERSION;

        WHorizontalList list = add(theme.horizontalList()).expandX().widget();

        addButton(list, "Auto (" + protocolVersion + ")", () -> {
            Compat.setProtocolVersion(protocolVersion);
            onClose();
        });
        addButton(list, "Release (" + releaseProtocolVersion + ")", () -> {
            Compat.setProtocolVersion(releaseProtocolVersion);
            onClose();
        });
        addButton(list, "Snapshot (" + snapshotProtocolVersion + ")", () -> {
            Compat.setProtocolVersion(snapshotProtocolVersion);
            onClose();
        });
    }

    private void addButton(WHorizontalList list, String text, Runnable action) {
        WButton button = list.add(theme.button(text)).expandX().widget();
        button.action = action;
    }
}
