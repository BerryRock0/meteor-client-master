/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.systems.modules.render.marker;

import meteordevelopment.orbit.EventHandler;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.gui.widgets.pressable.WButton;
import net.minecraft.block.Block;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

// TODO: Add outline and more modes
public class CuboidMarker extends BaseMarker {
    public static final String type = "Cuboid";

    public enum Mode {
        Full
    }

    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final SettingGroup sgRender = settings.createGroup("Render");
    private final SettingGroup sgControl = settings.createGroup("Control");
    private final SettingGroup sgExecution = settings.createGroup("Execution");

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

    private final Setting<Boolean> mining = sgExecution.add(new BoolSetting.Builder()
        .name("Mining")
        .description("Break blocks in area")
        .defaultValue(false)
        .build()
    );
    private final Setting<Boolean> using = sgExecution.add(new BoolSetting.Builder()
        .name("Using")
        .description("Intreact blocks in area")
        .defaultValue(false)
        .build()
    );

    private final Setting<Boolean> pre = sgControl.add(new BoolSetting.Builder()
        .name("Pre")
        .description("Load script before tick.")
        .defaultValue(false)
        .build()
    );
    private final Setting<Boolean> post = sgControl.add(new BoolSetting.Builder()
        .name("Post")
        .description("Load script after tick.")
        .defaultValue(false)
        .build()
    );

    public int minX;
    public int minY;
    public int minZ;
    public int maxX;
    public int maxY;
    public int maxZ;

    public CuboidMarker() {
        super(type);
    }

    @Override
    public String getTypeName() {
        return type;
    }

    @Override
    protected void render(Render3DEvent event) {
        minX = Math.min(pos1.get().getX(), pos2.get().getX());
        minY = Math.min(pos1.get().getY(), pos2.get().getY());
        minZ = Math.min(pos1.get().getZ(), pos2.get().getZ());
        maxX = Math.max(pos1.get().getX(), pos2.get().getX());
        maxY = Math.max(pos1.get().getY(), pos2.get().getY());
        maxZ = Math.max(pos1.get().getZ(), pos2.get().getZ());

        event.renderer.box(minX, minY, minZ, maxX, maxY, maxZ, sideColor.get(), lineColor.get(), shapeMode.get(), 0);
    }
    @EventHandler
    private void onTickPre(TickEvent.Pre event)
    {
        if (pre.get())
        {
            if(mining.get()) mine();
            if(using.get()) interact();      
        }
    }
        
    @EventHandler
    private void onTickPre(TickEvent.Post event) 
    {
        if (post.get())
        { 
            if(mining.get()) mine();
            if(using.get()) interact(); 
        }
    }
    

    private void mine()
    {
        for(int x = minX; x < maxX; x++)
        for(int y = minY; y < maxY; y++)
        for(int z = minZ; z < maxZ; z++)
        BlockUtils.breakBlock(new BlockPos(x,y,z), false);
    }

    private void interact()
    {
        for(int x = minX; x < maxX; x++)
        for(int y = minY; y < maxY; y++)
        for(int z = minZ; z < maxZ; z++)
        {
            BlockPos pos = new BlockPos(x,y,z);
            BlockUtils.interact(new BlockHitResult(pos.toCenterPos(), BlockUtils.getDirection(pos), pos, true), Hand.MAIN_HAND, false); 
        }
    }
}
