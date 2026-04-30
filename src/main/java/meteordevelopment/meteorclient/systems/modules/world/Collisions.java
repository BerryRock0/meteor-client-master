/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.systems.modules.world;

import java.util.Set;
import java.util.List;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;

public class Collisions extends Module {

    private final SettingGroup sgPlayer = settings.createGroup("Player");
    private final SettingGroup sgBlock = settings.createGroup("Block");
    private final SettingGroup sgEntity = settings.createGroup("Entity");
    private final SettingGroup sgOther = settings.createGroup("Other");
    
    public final Setting<List<Block>> fullBlock = sgBlock.add(new BlockListSetting.Builder()
        .name("full-block")
        .description("What blocks should be added collision box.")
        .build()
    );
    
    public final Setting<Boolean> fullBlockCase = sgBlock.add(new BoolSetting.Builder()
        .name("fullblock-case")
        .description("Switches black/white case.")
        .defaultValue(false)
        .build()
    );
    
    public final Setting<Boolean> fullBlockFinal = sgBlock.add(new BoolSetting.Builder()
        .name("fullblock-final")
        .description("Switches black/white final.")
        .defaultValue(false)
        .build()
    );
    
    public final Setting<List<Block>> emptyBlock = sgBlock.add(new BlockListSetting.Builder()
        .name("emptyblock")
        .description("What blocks should be emptied.")
        .build()
    );

    public final Setting<Boolean> emptyBlockCase = sgBlock.add(new BoolSetting.Builder()
        .name("emptyblock-case")
        .description("Switches black/white case.")
        .defaultValue(false)
        .build()
    );

    public final Setting<Boolean> emptyBlockFinal = sgBlock.add(new BoolSetting.Builder()
        .name("emptyblock-final")
        .description("Switches black/white final.")
        .defaultValue(false)
        .build()
    );    

    public final Setting<List<Block>> fullPlayer = sgPlayer.add(new BlockListSetting.Builder()
        .name("fullplayer-block")
        .description("What blocks should be added collision box.")
        .build()
    );
    
    public final Setting<Boolean> fullPlayerCase = sgPlayer.add(new BoolSetting.Builder()
        .name("fullplayer-case")
        .description("Switches black/white case.")
        .defaultValue(false)
        .build()
    );
    
    public final Setting<Boolean> fullPlayerFinal = sgPlayer.add(new BoolSetting.Builder()
        .name("fullplayer-final")
        .description("Switches black/white final.")
        .defaultValue(false)
        .build()
    );
    
    public final Setting<List<Block>> emptyPlayer = sgPlayer.add(new BlockListSetting.Builder()
        .name("emptyplayer")
        .description("What blocks should be emptied.")
        .build()
    );

    public final Setting<Boolean> emptyPlayerCase = sgPlayer.add(new BoolSetting.Builder()
        .name("emptyplayer-case")
        .description("Switches black/white case.")
        .defaultValue(false)
        .build()
    );

    public final Setting<Boolean> emptyPlayerFinal = sgPlayer.add(new BoolSetting.Builder()
        .name("emptyplayer-final")
        .description("Switches black/white final.")
        .defaultValue(false)
        .build()
    );

    public final Setting<List<Block>> emptyEntity = sgEntity.add(new BlockListSetting.Builder()
        .name("emptyentity")
        .description("What blocks should be emptied.")
        .build()
    );

    public final Setting<Boolean> emptyEntityCase = sgEntity.add(new BoolSetting.Builder()
        .name("emptyentity-case")
        .description("Switches black/white case.")
        .defaultValue(false)
        .build()
    );

    public final Setting<Boolean> emptyEntityFinal = sgEntity.add(new BoolSetting.Builder()
        .name("emptyentity-final")
        .description("Switches black/white final.")
        .defaultValue(false)
        .build()
    );

    private final Setting<Boolean> ignoreBorder = sgOther.add(new BoolSetting.Builder()
        .name("ignore-border")
        .description("Removes world border collision.")
        .defaultValue(false)
        .build()
    );

    public Collisions()
    {
        super(Categories.World, "collisions", "Adds collision boxes to certain blocks/areas.");
    }
    
    public boolean fullBlock(Block block)
    {
        if (fullBlock.get().contains(block))
            return isActive() && fullBlockCase.get();
        return isActive() && fullBlockFinal.get();   
    }
    
    public boolean emptyBlock(Block block)
    {
        if (emptyBlock.get().contains(block))
            return isActive() && emptyBlockCase.get();
        return isActive() && emptyBlockFinal.get();
    }

    public boolean fullPlayer(Block block)
    {
        if (fullPlayer.get().contains(block))
            return isActive() && fullPlayerCase.get();
        return isActive() && fullPlayerFinal.get();   
    }
    
    public boolean emptyPlayer(Block block)
    {
        if (emptyPlayer.get().contains(block))
            return isActive() && emptyPlayerCase.get();
        return isActive() && emptyPlayerFinal.get();
    }

    public boolean emptyEntity(Entity entity)
    {
        if(emptyEntity.get().contains(entity.getType()))
            return isActive() && emptyEntityCase.get();
        return isActive() && emptyEntityFinal.get();
    }

    public boolean ignoreBorder()
    {
        return isActive() && ignoreBorder.get();
    }
}
