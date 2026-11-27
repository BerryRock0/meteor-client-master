/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */
package net.minecraft.client.gui;

/**
 * Legacy shim: 26.x renamed {@code GuiGraphics} to {@code GuiGraphicsExtractor}.
 * For 1.20+ we simply extend the real GuiGraphics; for older versions a minimal
 * implementation is generated separately.
 */
public class GuiGraphicsExtractor extends GuiGraphics {
    public GuiGraphicsExtractor(net.minecraft.client.Minecraft mc, Object renderState, int mouseX, int mouseY) {
        super(mc, null);
    }
}
