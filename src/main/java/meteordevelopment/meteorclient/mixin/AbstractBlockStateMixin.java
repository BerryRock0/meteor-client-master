/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.mixin;

import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.render.NoRender;
import meteordevelopment.meteorclient.systems.modules.world.Collisions;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockBehaviour.BlockStateBase.class)
public abstract class AbstractBlockStateMixin
{
    @Inject(method = "getOffset", at = @At("HEAD"), cancellable = true)
    private void modifyPos(BlockPos pos, CallbackInfoReturnable<Vec3> cir)
    {
    
        if (Modules.get() == null) return;

        if (Modules.get().get(NoRender.class).noTextureRotations())
            cir.setReturnValue(Vec3.ZERO);
    }

    @Inject(at = @At("HEAD"), method = "getVisualShape(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/shapes/CollisionContext;)Lnet/minecraft/world/phys/shapes/VoxelShape;", cancellable = true)
	private void onGetVisualShape(BlockGetter view, BlockPos pos, CollisionContext context, CallbackInfoReturnable<VoxelShape> cir)
	{
	    if (Modules.get() == null) return;
        Collisions coll = Modules.get().get(Collisions.class);

		if(coll.emptyBlock(view.getBlockState(pos).getBlock()))
            cir.setReturnValue(Shapes.empty());
        
	    if(coll.fullBlock(view.getBlockState(pos).getBlock()))
            cir.setReturnValue(Shapes.block());
	}
	
	@Inject(at = @At("HEAD"), method = "getCollisionShape(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/shapes/CollisionContext;)Lnet/minecraft/world/phys/shapes/VoxelShape;", cancellable = true)
	private void onGetCollisionShape(BlockGetter view, BlockPos pos, CollisionContext context, CallbackInfoReturnable<VoxelShape> cir)
	{
        if (Modules.get() == null) return;
        Collisions coll = Modules.get().get(Collisions.class);

		if(coll.emptyPlayer(view.getBlockState(pos).getBlock()))
            cir.setReturnValue(Shapes.empty());
        
	    if(coll.fullPlayer(view.getBlockState(pos).getBlock()))
            cir.setReturnValue(Shapes.block());
	}
}
