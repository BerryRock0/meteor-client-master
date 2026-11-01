/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.gui.screens;

import meteordevelopment.meteorclient.compat.Compat;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.WindowScreen;
import meteordevelopment.meteorclient.gui.widgets.containers.WTable;
import meteordevelopment.meteorclient.gui.widgets.input.WTextBox;
import meteordevelopment.meteorclient.gui.widgets.pressable.WButton;
import net.minecraft.client.gui.screens.Screen;

public class ProtocolVersionScreen extends WindowScreen {
    private WButton setButton;

    public ProtocolVersionScreen(GuiTheme theme, Screen parent) {
        super(theme, "Protocol Version");
        this.parent = parent;
    }

    @Override
    public void initWidgets() {
        WTable table = add(theme.table()).widget();

        table.add(theme.label("Protocol Version: "));
        WTextBox versionInput = table.add(theme.textBox(String.valueOf(Compat.getProtocolVersion()))).minWidth(400).expandX().widget();
        versionInput.setFocused(true);
        table.row();

        setButton = add(theme.button("Set")).expandX().widget();
        setButton.action = () -> {
            try {
                Compat.setProtocolVersion(Integer.parseInt(versionInput.get().trim()));
                onClose();
            } catch (NumberFormatException ignored) {
            }
        };
    }
}
