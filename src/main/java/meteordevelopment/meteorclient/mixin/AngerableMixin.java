/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.world.Behaviors;

import net.minecraft.entity.mob.Angerable;

@Mixin(Angerable.class)
public abstract class AngerableMixin
{
  @Inject(method = setTarget, at = @At("HEAD"), cancellable = true)
  private void setTarget(@Nullable LivingEntity target, CallbackInfo ci)
  {
    Behaviors beh = Modules.get().get(Behaviors.class);
    if(beh.attackTask(target))
      Angerable.setTarget(target);
  }
  @Inject(method = setAttacker, at = @At("HEAD"), cancellable = true)
  private void setAttacker(@Nullable LivingEntity attacker, CallbackInfo ci)
  {
    Behaviors beh = Modules.get().get(Behaviors.class);
    if(beh.attackTask(attacker))
      Angerable.setAttacker(attacker);
  }
}
