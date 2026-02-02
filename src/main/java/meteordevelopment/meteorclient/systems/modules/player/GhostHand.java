/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.systems.modules.player;

import java.util.List;
import java.util.Set;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.BlockListSetting;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import net.minecraft.block.Block;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class GhostHand extends Module {
	
    private final SettingGroup sgFullcube = settings.createGroup("FullCube");
    private final SettingGroup sgEmpty = settings.createGroup("Empty");
    
    public final Setting<List<Block>> fullcube = sgFullcube.add(new BlockListSetting.Builder()
        .name("full-cube")
        .description("What blocks should be added collision box.")
        .build()
    );
    
    public final Setting<Boolean> fullcubelist = sgFullcube.add(new BoolSetting.Builder()
        .name("fullcube-case")
        .description("Switches black/white case.")
        .defaultValue(false)
        .build()
    );
    
    public final Setting<Boolean> fullcubefinal = sgFullcube.add(new BoolSetting.Builder()
        .name("fullcube-final")
        .description("Switches black/white final.")
        .defaultValue(false)
        .build()
    );
    
    public final Setting<List<Block>> empty = sgEmpty.add(new BlockListSetting.Builder()
        .name("empty")
        .description("What blocks should be emptied.")
        .build()
    );

    public final Setting<Boolean> emptycase = sgEmpty.add(new BoolSetting.Builder()
        .name("empty-case")
        .description("Switches black/white case.")
        .defaultValue(false)
        .build()
    );

    public final Setting<Boolean> emptyfinal = sgEmpty.add(new BoolSetting.Builder()
        .name("empty-final")
        .description("Switches black/white final.")
        .defaultValue(false)
        .build()
    );
    
    public GhostHand() {
        super(Categories.Player, "ghost-hand", "Interact blocks through walls.");
    }

    public boolean inBlockList(Block block)
    {
        return isActive() && inList(block);
    }

    public boolean full(Block block)
    {
        if (fullcube.get().contains(block))
            return isActive() && fullcubecase.get();
        return isActive() && fullcubefinal.get();   
    }
    
    public boolean emp(Block block)
    {
        if (empty.get().contains(block))
            return isActive() && emptylist.get();
        return isActive() && emptyfinal.get();
    }
    
}
