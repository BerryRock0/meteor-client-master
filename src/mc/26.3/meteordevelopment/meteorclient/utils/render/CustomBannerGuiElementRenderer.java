/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.utils.render;

import meteordevelopment.meteorclient.mc.Vanilla;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.sprite.SpriteGetter;
import net.minecraft.client.resources.model.AtlasManager;
import org.jspecify.annotations.NonNull;

public class CustomBannerGuiElementRenderer extends PictureInPictureRenderer<CustomBannerGuiElementRenderState> {
    private final SpriteGetter sprites;

    public CustomBannerGuiElementRenderer(AtlasManager atlasManager) {
        this.sprites = atlasManager.getBannerSpriteGetter();
    }

    @Override
    public @NonNull Class<CustomBannerGuiElementRenderState> getRenderStateClass() {
        return CustomBannerGuiElementRenderState.class;
    }

    protected void renderToTexture(CustomBannerGuiElementRenderState state, PoseStack matrixStack) {
        Vanilla.lighting().setupFor(Lighting.Entry.ITEMS_FLAT);
        matrixStack.translate(0.0F, 0.25F, 0.0F);
        Vanilla.submitBannerPatterns(this.sprites, matrixStack, 15728880, OverlayTexture.NO_OVERLAY, state.flag(), 0.0F, true, state.baseColor(), state.resultBannerPatterns());
    }

    @Override
    protected @NonNull String getTextureLabel() {
        return "custom banner";
    }
}