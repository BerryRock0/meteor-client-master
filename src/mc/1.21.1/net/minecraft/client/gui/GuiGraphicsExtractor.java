/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */
package net.minecraft.client.gui;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.renderer.RenderType;

import java.util.List;

/**
 * Legacy shim: 26.x renamed {@code GuiGraphics} to {@code GuiGraphicsExtractor}.
 *
 * <p>1.21.5 and older expose the old {@link net.minecraft.client.gui.GuiGraphics} API: a
 * {@code PoseStack}, render-type functions instead of pipelines, and no strata. The overloads
 * below keep the shared 26.x call sites compiling without touching each feature module.</p>
 */
public class GuiGraphicsExtractor extends GuiGraphics {
    public GuiGraphicsExtractor(net.minecraft.client.Minecraft mc, Object renderState, int mouseX, int mouseY) {
        super(mc, null);
    }

    public void nextStratum() {}

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

    public void tooltip(net.minecraft.client.gui.Font font,
                        List<net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent> components,
                        int x, int y,
                        net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner positioner,
                        net.minecraft.resources.ResourceLocation style) {
        // The old API takes Component lines; client tooltip components cannot be reconstructed
        // losslessly, so render the collected text through the legacy overload.
        List<net.minecraft.util.FormattedCharSequence> lines = new java.util.ArrayList<>();
        for (var component : components) {
            lines.add(net.minecraft.util.FormattedCharSequence.forward(component.toString(), net.minecraft.network.chat.Style.EMPTY));
        }
        renderTooltip(font, lines, positioner, x, y);
    }

    public void setTooltipForNextFrame(net.minecraft.client.gui.Font font,
                                       List<?> components, Object image, int x, int y) {
        List<net.minecraft.util.FormattedCharSequence> lines = new java.util.ArrayList<>();
        for (Object component : components) {
            if (component instanceof net.minecraft.network.chat.Component c) {
                lines.add(c.getVisualOrderText());
            } else if (component instanceof net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent c) {
                lines.add(net.minecraft.util.FormattedCharSequence.forward(c.toString(), net.minecraft.network.chat.Style.EMPTY));
            }
        }
        renderTooltip(font, lines, net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner.INSTANCE, x, y);
    }

    public void blit(RenderPipeline pipeline, net.minecraft.resources.ResourceLocation texture,
                     int x, int y, int u, int v, int width, int height, int textureWidth, int textureHeight) {
        super.blit((java.util.function.Function<net.minecraft.resources.ResourceLocation, net.minecraft.client.renderer.RenderType>) RenderType::guiTextured, texture, x, y, u, v, width, height, textureWidth, textureHeight);
    }

    public void blit(RenderPipeline pipeline, net.minecraft.resources.ResourceLocation texture,
                     int x, int y, int u, int v, int width, int height, int textureWidth, int textureHeight, int color) {
        super.blit((java.util.function.Function<net.minecraft.resources.ResourceLocation, net.minecraft.client.renderer.RenderType>) RenderType::guiTextured, texture, x, y, u, v, width, height, textureWidth, textureHeight, color);
    }

    public void blit(RenderPipeline pipeline, net.minecraft.resources.ResourceLocation texture,
                     int x, int y, float u, float v, int width, int height,
                     int textureWidth, int textureHeight, int textureWidth2, int textureHeight2, int color) {
        super.blit((java.util.function.Function<net.minecraft.resources.ResourceLocation, net.minecraft.client.renderer.RenderType>) RenderType::guiTextured, texture, x, y, u, v, width, height, textureWidth, textureHeight, textureWidth2, textureHeight2, color);
    }

    public void blitSprite(RenderPipeline pipeline, net.minecraft.resources.ResourceLocation sprite,
                           int x, int y, int width, int height) {
        super.blitSprite((java.util.function.Function<net.minecraft.resources.ResourceLocation, net.minecraft.client.renderer.RenderType>) RenderType::guiTextured, sprite, x, y, width, height);
    }

    public void blitSprite(RenderPipeline pipeline, net.minecraft.resources.ResourceLocation sprite,
                           int x, int y, int width, int height, int spriteWidth, int spriteHeight) {
        super.blitSprite((java.util.function.Function<net.minecraft.resources.ResourceLocation, net.minecraft.client.renderer.RenderType>) RenderType::guiTextured, sprite, x, y, width, height, 0, 0, spriteWidth, spriteHeight);
    }
}
