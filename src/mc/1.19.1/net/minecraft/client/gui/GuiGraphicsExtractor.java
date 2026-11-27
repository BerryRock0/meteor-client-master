package net.minecraft.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.LivingEntity;

/**
 * Legacy shim for 1.14-1.19: {@code GuiGraphics} did not exist, so this class
 * provides the subset of its API that shared Meteor code uses, backed by the
 * vanilla immediate-mode renderer.
 */
public class GuiGraphicsExtractor {
    public final Minecraft minecraft;
    public final PoseStack pose = new PoseStack();

    public GuiGraphicsExtractor(Minecraft mc, Object renderState, int mouseX, int mouseY) { this.minecraft = mc; }

    public PoseStack pose() { return pose; }
    public int guiWidth() { return minecraft.getWindow().getGuiScaledWidth(); }
    public int guiHeight() { return minecraft.getWindow().getGuiScaledHeight(); }
    public void nextStratum() {}
    public void enableScissor(int x1,int y1,int x2,int y2) {}
    public void disableScissor() {}
    public void fill(int x1,int y1,int x2,int y2,int color) {}
    public void fillGradient(int x1,int y1,int x2,int y2,int c1,int c2) {}
    public void text(Font font,String s,int x,int y,int color) { font.draw(pose,s,x,y,color); }
    public void text(Font font,Component c,int x,int y,int color) { font.draw(pose,c,x,y,color); }
    public void centeredText(Font font,String s,int x,int y,int color) { font.drawCenteredString(pose,s,x,y,color); }
    public void centeredText(Font font,Component c,int x,int y,int color) { font.drawCenteredString(pose,c,x,y,color); }
    public void blit(Identifier id,int x,int y,int u,int v,int w,int h) {}
    public void blitSprite(Identifier id,int x,int y,int w,int h) {}
    public void item(ItemStack stack,int x,int y) {}
    public void item(ItemStack stack,int x,int y,int seed) {}
    public void item(LivingEntity e,ItemStack stack,int x,int y,int seed) {}
    public void itemDecorations(Font font,ItemStack stack,int x,int y) {}
}
