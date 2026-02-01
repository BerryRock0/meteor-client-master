/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.systems.modules.world;

import java.util.Set;
import java.util.List;

import net.minecraft.block.*;

import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;

public class Collisions extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final SettingGroup sgControl = settings.createGroup("Control");
    
    public final Setting<List<Block>> fullcube = sgGeneral.add(new BlockListSetting.Builder()
        .name("full-cube")
        .description("What blocks should be added collision box.")
        .build()
    );
    
    public final Setting<List<Block>> empty = sgGeneral.add(new BlockListSetting.Builder()
        .name("empty")
        .description("What blocks should be emptied.")
        .build()
    );

    public final Setting<Boolean> alwayszero = sgControl.add(new BoolSetting.Builder()
        .name("fullcube-final")
        .description("Switches black/white cases.")
        .defaultValue(false)
        .build()
    );
    
    public final Setting<Boolean> fullcubelist = sgControl.add(new BoolSetting.Builder()
        .name("fullcube-case")
        .description("Switches black/white list.")
        .defaultValue(false)
        .build()
    );
    public final Setting<Boolean> alwaysone = sgControl.add(new BoolSetting.Builder()
        .name("empty-final")
        .description("Switches black/white cases.")
        .defaultValue(false)
        .build()
    );
    
    public final Setting<Boolean> emptylist = sgControl.add(new BoolSetting.Builder()
        .name("empty-case")
        .description("Switches black/white list.")
        .defaultValue(false)
        .build()
    );

    private final Setting<Boolean> ignoreBorder = sgControl.add(new BoolSetting.Builder()
        .name("ignore-border")
        .description("Removes world border collision.")
        .defaultValue(false)
        .build()
    );

    public Collisions()
    {
        super(Categories.World, "collisions", "Adds collision boxes to certain blocks/areas.");
    }
    
    public boolean full(Block block)
    {
        if (fullcube.get().contains(block))
            return isActive() && fullcubelist.get();
        return isActive() && alwayszero.get();   
    }
    
    public boolean emp(Block block)
    {
        if (empty.get().contains(block))
            return isActive() && emptylist.get();
        return isActive() && alwaysone.get();
    }
    
    public boolean ignoreBorder()
    {
        return isActive() && ignoreBorder.get();
    }
}
