/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.utils.render;

import meteordevelopment.meteorclient.mc.Vanilla;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.sprite.SpriteGetter;
import net.minecraft.client.renderer.SubmitNodeCollector;
import org.jspecify.annotations.NonNull;

public class CustomBannerGuiElementRenderer extends PictureInPictureRenderer<CustomBannerGuiElementRenderState> {
    private final SpriteGetter sprites;
    private final MultiBufferSource.BufferSource bufferSource;

    public CustomBannerGuiElementRenderer(MultiBufferSource.BufferSource bufferSource, Object atlasManager) {
        super(bufferSource);
        this.bufferSource = bufferSource;
        this.sprites = getBannerSpriteGetter(atlasManager);
    }

    private SpriteGetter getBannerSpriteGetter(Object atlasManager) {
        if (atlasManager instanceof SpriteGetter) {
            return (SpriteGetter) atlasManager;
        }
        try {
            // Try AtlasManager.getBannerSpriteGetter()
            var method = atlasManager.getClass().getMethod("getBannerSpriteGetter");
            return (SpriteGetter) method.invoke(atlasManager);
        } catch (Exception e) {
            // Fallback: try to get from model manager
            try {
                var mc = net.minecraft.client.Minecraft.getInstance();
                var modelManager = mc.getModelManager();
                var method = modelManager.getClass().getMethod("getBannerSpriteGetter");
                return (SpriteGetter) method.invoke(modelManager);
            } catch (Exception ex) {
                throw new RuntimeException("Failed to get banner sprite getter", ex);
            }
        }
    }

    @Override
    public @NonNull Class<CustomBannerGuiElementRenderState> getRenderStateClass() {
        return CustomBannerGuiElementRenderState.class;
    }

    protected void renderToTexture(CustomBannerGuiElementRenderState state, PoseStack matrixStack) {
        Vanilla.lighting().setupFor(Lighting.Entry.ITEMS_FLAT);
        matrixStack.translate(0.0F, 0.25F, 0.0F);
        
        // Try to get collector from bufferSource or Minecraft instance
        SubmitNodeCollector collector = getCollector();
        if (collector != null) {
            Vanilla.submitBannerPatterns(this.sprites, matrixStack, collector, 15728880, OverlayTexture.NO_OVERLAY, state.flag(), 0.0F, true, state.baseColor(), state.resultBannerPatterns());
        }
    }

    private SubmitNodeCollector getCollector() {
        // Try to get collector from bufferSource
        try {
            var method = bufferSource.getClass().getMethod("getCollector");
            return (SubmitNodeCollector) method.invoke(bufferSource);
        } catch (Exception e) {
            // Try to get from Minecraft instance
            try {
                var mc = net.minecraft.client.Minecraft.getInstance();
                var gameRenderer = mc.gameRenderer;
                var method = gameRenderer.getClass().getMethod("getCollector");
                return (SubmitNodeCollector) method.invoke(gameRenderer);
            } catch (Exception ex) {
                return null;
            }
        }
    }

    @Override
    protected @NonNull String getTextureLabel() {
        return "custom banner";
    }
}