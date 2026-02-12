/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.render.Render2DEvent;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.misc.BetterChat;
import meteordevelopment.meteorclient.systems.modules.render.Freecam;
import meteordevelopment.meteorclient.systems.modules.render.NoRender;
import meteordevelopment.meteorclient.utils.Utils;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.Entity;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.util.profiler.Profilers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin
{
    @Shadow public abstract void clear();

    @Inject(method = "render", at = @At("TAIL"))
    private void onRender(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        context.createNewRootLayer();

        Profilers.get().push(MeteorClient.MOD_ID + "_render_2d");

        Utils.unscaledProjection();

        MeteorClient.EVENT_BUS.post(Render2DEvent.get(context, context.getScaledWindowWidth(), context.getScaledWindowWidth(), tickCounter.getTickProgress(true)));

        context.createNewRootLayer();
        Utils.scaledProjection();

        Profilers.get().pop();
    }

    @Inject(at = @At("HEAD"), method = "renderAirBubbles(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/entity/player/PlayerEntity;III)V", cancellable = true)
	private void onRenderAirBubbles(DrawContext context, PlayerEntity player, int heartCount, int top, int left, CallbackInfo ci)
	{
		if(Modules.get().get(NoRender.class).noAirBubbles()) ci.cancel();
	}
    
	@Inject(at = @At("HEAD"), method = "renderChat(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/client/render/RenderTickCounter;)V", cancellable = true)
	private void onRenderChat(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci)
	{
		if(Modules.get().get(NoRender.class).noChat()) ci.cancel();
	}
    
	@Inject(at = @At("HEAD"), method = "renderDebugHud(Lnet/minecraft/client/gui/DrawContext;)V", cancellable = true)
	private void onRenderDebugHud(DrawContext context, CallbackInfo ci)
	{
		if(Modules.get().get(NoRender.class).noDebugHud()) ci.cancel();
	}
    
	@Inject(at = @At("HEAD"), method = "renderDemoTimer(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/client/render/RenderTickCounter;)V", cancellable = true)
	private void onRenderDemoTimer(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci)
	{
		if(Modules.get().get(NoRender.class).noDemoTimer()) ci.cancel();
	}
    
	@Inject(at = @At("HEAD"), method = "renderFood(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/entity/player/PlayerEntity;II)V", cancellable = true)
	private void onRenderFood(DrawContext context, PlayerEntity player, int top, int right, CallbackInfo ci)
	{
		if(Modules.get().get(NoRender.class).noFood()) ci.cancel();
	}
    
	@Inject(at = @At("HEAD"), method = "renderHealthBar(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/entity/player/PlayerEntity;IIIIFIIIZ)V", cancellable = true)
	private void onRenderHealthBar(DrawContext context, PlayerEntity player, int x, int y, int lines, int regeneratingHeartIndex, float maxHealth, int lastHealth, int health, int absorption, boolean blinking, CallbackInfo ci)
	{
		if(Modules.get().get(NoRender.class).noHealthBar()) ci.cancel();
	}
    
	@Inject(at = @At("HEAD"), method = "renderHotbar(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/client/render/RenderTickCounter;)V", cancellable = true)
	private void onRenderHotbar(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci)
	{
		if(Modules.get().get(NoRender.class).noHotbar()) ci.cancel();
	}
    
	@Inject(at = @At("HEAD"), method = "renderHotbarItem(Lnet/minecraft/client/gui/DrawContext;IILnet/minecraft/client/render/RenderTickCounter;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/item/ItemStack;I)V", cancellable = true)
	private void onRenderHotbarItem(DrawContext context, int x, int y, RenderTickCounter tickCounter, PlayerEntity player, ItemStack stack, int seed, CallbackInfo ci)
	{
		if(Modules.get().get(NoRender.class).noHotbarItem()) ci.cancel();
	}
    
	@Inject(at = @At("HEAD"), method = "renderMountHealth(Lnet/minecraft/client/gui/DrawContext;)V", cancellable = true)
	private void onRenderMountHealth(DrawContext context, CallbackInfo ci)
	{
		if(Modules.get().get(NoRender.class).noMountHealth()) ci.cancel();
	}
    
	@Inject(at = @At("HEAD"), method = "renderSubtitlesHud(Lnet/minecraft/client/gui/DrawContext;Z)V", cancellable = true)
	private void onRenderSubtitlesHud(DrawContext context, boolean defer, CallbackInfo ci)
	{
		if(Modules.get().get(NoRender.class).noSubtitlesHud()) ci.cancel();
	}

    @Inject(method = "renderStatusEffectOverlay", at = @At("HEAD"), cancellable = true)
    private void onRenderStatusEffectOverlay(CallbackInfo info)
    {
        if (Modules.get().get(NoRender.class).noPotionIcons()) info.cancel();
    }

    @Inject(method = "renderPortalOverlay", at = @At("HEAD"), cancellable = true)
    private void onRenderPortalOverlay(DrawContext context, float nauseaStrength, CallbackInfo ci)
    {
        if (Modules.get().get(NoRender.class).noPortalOverlay()) ci.cancel();
    }

    @ModifyArgs(method = "renderMiscOverlays", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;renderOverlay(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/util/Identifier;F)V", ordinal = 0))
    private void onRenderPumpkinOverlay(Args args)
    {
        if (Modules.get().get(NoRender.class).noPumpkinOverlay()) args.set(2, 0f);
    }

    @ModifyArgs(method = "renderMiscOverlays", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;renderOverlay(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/util/Identifier;F)V", ordinal = 1))
    private void onRenderPowderedSnowOverlay(Args args)
    {
        if (Modules.get().get(NoRender.class).noPowderedSnowOverlay()) args.set(2, 0f);
    }

    @Inject(method = "renderVignetteOverlay", at = @At("HEAD"), cancellable = true)
    private void onRenderVignetteOverlay(DrawContext context, Entity entity, CallbackInfo ci)
    {
        if (Modules.get().get(NoRender.class).noVignette()) ci.cancel();
    }

    @Inject(method = "renderScoreboardSidebar(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/scoreboard/ScoreboardObjective;)V", at = @At("HEAD"), cancellable = true)
    private void onRenderScoreboardSidebar(DrawContext context, ScoreboardObjective objective, CallbackInfo ci)
    {
        if (Modules.get().get(NoRender.class).noScoreboard()) ci.cancel();
    }

    @Inject(method = "renderScoreboardSidebar(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/client/render/RenderTickCounter;)V", at = @At("HEAD"), cancellable = true)
    private void onRenderScoreboardSidebar(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci)
    {
        if (Modules.get().get(NoRender.class).noScoreboard()) ci.cancel();
    }

    @Inject(method = "renderSpyglassOverlay", at = @At("HEAD"), cancellable = true)
    private void onRenderSpyglassOverlay(DrawContext context, float scale, CallbackInfo ci)
    {
        if (Modules.get().get(NoRender.class).noSpyglassOverlay()) ci.cancel();
    }

    @ModifyExpressionValue(method = "renderCrosshair", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/Perspective;isFirstPerson()Z"))
    private boolean alwaysRenderCrosshairInFreecam(boolean firstPerson)
    {
        return Modules.get().isActive(Freecam.class) || firstPerson;
    }

    @Inject(method = "renderCrosshair", at = @At("HEAD"), cancellable = true)
    private void onRenderCrosshair(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci)
    {
        if (Modules.get().get(NoRender.class).noCrosshair()) ci.cancel();
    }

    @Inject(method = "renderTitleAndSubtitle", at = @At("HEAD"), cancellable = true)
    private void onRenderTitle(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci)
    {
        if (Modules.get().get(NoRender.class).noTitle()) ci.cancel();
    }

    @Inject(method = "renderHeldItemTooltip", at = @At("HEAD"), cancellable = true)
    private void onRenderHeldItemTooltip(DrawContext context, CallbackInfo ci)
    {
        if (Modules.get().get(NoRender.class).noHeldItemName()) ci.cancel();
    }

    @Inject(method = "clear", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/ChatHud;clear(Z)V"), cancellable = true)
    private void onClear(CallbackInfo info)
    {
        if (Modules.get().get(BetterChat.class).keepHistory())
        {
            info.cancel();
        }
    }

    @Inject(method = "renderNauseaOverlay", at = @At("HEAD"), cancellable = true)
    private void onRenderNausea(DrawContext context, float distortionStrength, CallbackInfo ci)
    {
        if (Modules.get().get(NoRender.class).noNausea()) ci.cancel();
    }
}
