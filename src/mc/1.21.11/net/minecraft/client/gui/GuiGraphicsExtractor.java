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
        super(mc, null, 0, 0);
    }

    public void text(net.minecraft.client.gui.Font font, String text, int x, int y, int color) {
        drawString(font, text, x, y, color);
    }

    public void text(net.minecraft.client.gui.Font font, String text, int x, int y, int color, boolean shadow) {
        drawString(font, text, x, y, color, shadow);
    }

    public void text(net.minecraft.client.gui.Font font, net.minecraft.network.chat.Component text, int x, int y, int color) {
        drawString(font, text, x, y, color);
    }

    public void text(net.minecraft.client.gui.Font font, net.minecraft.network.chat.Component text, int x, int y, int color, boolean shadow) {
        drawString(font, text, x, y, color, shadow);
    }

    public void text(net.minecraft.client.gui.Font font, net.minecraft.network.chat.MutableComponent text, int x, int y, int color) {
        drawString(font, text, x, y, color);
    }

    public void text(net.minecraft.client.gui.Font font, net.minecraft.util.FormattedCharSequence text, int x, int y, int color, boolean shadow) {
        drawString(font, text, x, y, color, shadow);
    }

    public void item(net.minecraft.world.item.ItemStack stack, int x, int y) {
        renderItem(stack, x, y);
    }

    public void itemDecorations(net.minecraft.client.gui.Font font, net.minecraft.world.item.ItemStack stack, int x, int y, Object text) {
        renderItemDecorations(font, stack, x, y);
    }

    public void itemDecorations(net.minecraft.client.gui.Font font, net.minecraft.world.item.ItemStack stack, int x, int y) {
        renderItemDecorations(font, stack, x, y);
    }

    public void entity(Object state, float scale, Object translation, Object rotation, Object color,
                       int x1, int y1, int x2, int y2) {}
    public void map(Object state, int x, int y, int width, int height) {}
    public void map(Object state) {}
}
