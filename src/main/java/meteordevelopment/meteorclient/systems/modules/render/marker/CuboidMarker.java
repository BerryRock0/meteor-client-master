/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.systems.modules.render.marker;

import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import net.minecraft.core.BlockPos;

// TODO: Add outline and more modes
public class CuboidMarker extends BaseMarker
{
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final SettingGroup sgPlayer = settings.createGroup("Player");
    private final SettingGroup sgBlock = settings.createGroup("Block");
    private final SettingGroup sgRender = settings.createGroup("Render");

    public static final String type = "Cuboid";

    // General
    private final Setting<BlockPos> pos1 = sgGeneral.add(new BlockPosSetting.Builder()
        .name("pos-1")
        .description("1st corner of the cuboid")
        .build()
    );

    private final Setting<BlockPos> pos2 = sgGeneral.add(new BlockPosSetting.Builder()
        .name("pos-2")
        .description("2nd corner of the cuboid")
        .build()
    );

    // Collisions
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
    
    // Render
    private final Setting<Mode> mode = sgRender.add(new EnumSetting.Builder<Mode>()
        .name("mode")
        .description("What mode to use for this marker.")
        .defaultValue(Mode.Full)
        .build()
    );

    private final Setting<ShapeMode> shapeMode = sgRender.add(new EnumSetting.Builder<ShapeMode>()
        .name("shape-mode")
        .description("How the shapes are rendered.")
        .defaultValue(ShapeMode.Both)
        .build()
    );

    private final Setting<SettingColor> sideColor = sgRender.add(new ColorSetting.Builder()
        .name("side-color")
        .description("The color of the sides of the blocks being rendered.")
        .defaultValue(new SettingColor(0, 100, 255, 50))
        .build()
    );

    private final Setting<SettingColor> lineColor = sgRender.add(new ColorSetting.Builder()
        .name("line-color")
        .description("The color of the lines of the blocks being rendered.")
        .defaultValue(new SettingColor(0, 100, 255, 255))
        .build()
    );

    public CuboidMarker() {
        super(type);
    }

    @Override
    public String getTypeName() {
        return type;
    }

    @Override
    protected void render(Render3DEvent event)
    {
        int minX = Math.min(pos1.get().getX(), pos2.get().getX());
        int minY = Math.min(pos1.get().getY(), pos2.get().getY());
        int minZ = Math.min(pos1.get().getZ(), pos2.get().getZ());
        int maxX = Math.max(pos1.get().getX(), pos2.get().getX());
        int maxY = Math.max(pos1.get().getY(), pos2.get().getY());
        int maxZ = Math.max(pos1.get().getZ(), pos2.get().getZ());

        event.renderer.box(minX, minY, minZ, maxX , maxY, maxZ, sideColor.get(), lineColor.get(), shapeMode.get(), 0);
    }

    public boolean fullBlock(Block block, BlockPos pos)
    {
        if (fullBlock.get().contains(block))
            return isActive() && fullBlockCase.get();
        return isActive() && fullBlockFinal.get();   
    }
    
    public boolean emptyBlock(Block block, BlockPos pos)
    {
        if (emptyBlock.get().contains(block))
            return isActive() && emptyBlockCase.get();
        return isActive() && emptyBlockFinal.get();
    }

    public boolean fullPlayer(Block block, BlockPos pos)
    {
        if (fullPlayer.get().contains(block))
            return isActive() && fullPlayerCase.get();
        return isActive() && fullPlayerFinal.get();   
    }
    
    public boolean emptyPlayer(Block block, BlockPos pos)
    {
        if (emptyPlayer.get().contains(block))
            return isActive() && emptyPlayerCase.get();
        return isActive() && emptyPlayerFinal.get();
    }

    public enum Mode
    {
        Full
    }
}
