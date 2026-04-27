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
import net.minecraft.world.BlockView;
import net.minecraft.block.ShapeContext;
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

    @Inject(at = @At("HEAD"), method = "getOutlineShape(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/ShapeContext;)Lnet/minecraft/util/shape/VoxelShape;", cancellable = true)
	private void onGetOutlineShape(BlockView view, BlockPos pos, ShapeContext context, CallbackInfoReturnable<VoxelShape> cir)
	{
	    if (Modules.get() == null) return;
        Collisions coll = Modules.get().get(Collisions.class);

		if(coll.emptyBlock(view.getBlockState(pos).getBlock()))
            cir.setReturnValue(Shapes.empty());
        
	    if(coll.fullBlock(view.getBlockState(pos).getBlock()))
            cir.setReturnValue(Shapes.block());
	}
	
	@Inject(at = @At("HEAD"), method = "getCollisionShape(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/ShapeContext;)Lnet/minecraft/util/shape/VoxelShape;", cancellable = true)
	private void onGetCollisionShape(BlockView view, BlockPos pos, ShapeContext context, CallbackInfoReturnable<VoxelShape> cir)
	{
        if (Modules.get() == null) return;
        Collisions coll = Modules.get().get(Collisions.class);

		if(coll.emptyPlayer(view.getBlockState(pos).getBlock()))
            cir.setReturnValue(Shapes.empty());
        
	    if(coll.fullPlayer(view.getBlockState(pos).getBlock()))
            cir.setReturnValue(Shapes.block());
	}
}
