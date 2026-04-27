/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.mixin;

import java.util.List;
import java.util.function.Consumer;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.entity.player.FinishUsingItemEvent;
import meteordevelopment.meteorclient.events.entity.player.StoppedUsingItemEvent;
import meteordevelopment.meteorclient.events.game.ItemStackTooltipEvent;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.world.Damages;
import meteordevelopment.meteorclient.systems.modules.world.Quantities;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.World;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import org.jetbrains.annotations.Nullable;

import static meteordevelopment.meteorclient.MeteorClient.mc;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
    @ModifyReturnValue(method = "getTooltipLines", at = @At("RETURN"))
    private List<Component> onGetTooltipLines(List<Component> original) {
        if (Utils.canUpdate()) {
            ItemStackTooltipEvent event = MeteorClient.EVENT_BUS.post(new ItemStackTooltipEvent((ItemStack) (Object) this, original));
            return event.list();
        }

        return original;
    }

    @Inject(method = "finishUsingItem", at = @At("HEAD"))
    private void onFinishUsingItem(Level level, LivingEntity livingEntity, CallbackInfoReturnable<ItemStack> cir) {
        if (livingEntity == mc.player) {
            MeteorClient.EVENT_BUS.post(FinishUsingItemEvent.get((ItemStack) (Object) this));
        }
    }

    @Inject(method = "releaseUsing", at = @At("HEAD"))
    private void onReleaseUsing(Level level, LivingEntity entity, int remainingTime, CallbackInfo ci) {
        if (entity == mc.player) {
            MeteorClient.EVENT_BUS.post(StoppedUsingItemEvent.get((ItemStack) (Object) this));
        }
    }

    @Inject(at = @At("HEAD"), method = "damage(ILnet/minecraft/server/world/ServerWorld;Lnet/minecraft/server/network/ServerPlayerEntity;Ljava/util/function/Consumer;)V", cancellable = true)
    private void isDamage(int amount, ServerWorld world, @Nullable ServerPlayerEntity player, Consumer<Item> breakCallback, CallbackInfo ci)
    {
        ItemStack itemStack = (ItemStack)(Object)this;
        Item thisObj = itemStack.getItem();
        if(Modules.get().get(Damages.class).inItemsList(thisObj))
        ci.cancel();
    }

    @Inject(at = @At("HEAD"), method = "increment(I)V", cancellable = true)
    private void incrementControl(int amount, CallbackInfo ci)
    {
        ItemStack itemStack = (ItemStack)(Object)this;
        Item thisObj = itemStack.getItem();
        if (Modules.get().get(Quantities.class).incr(thisObj)) ci.cancel();
    }

    @Inject(at = @At("HEAD"), method = "decrement(I)V", cancellable = true)
    private void decrementControl(int amount, CallbackInfo ci)
    {
        ItemStack itemStack = (ItemStack)(Object)this;
        Item thisObj = itemStack.getItem();
        if (Modules.get().get(Quantities.class).decr(thisObj)) ci.cancel();
    }
}
