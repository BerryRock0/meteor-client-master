/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.systems.modules.render.marker;

import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import net.minecraft.util.math.BlockPos;

// TODO: Add outline and more modes
public class CuboidMarker extends BaseMarker {
    public static final String type = "Cuboid";

    public enum Mode {
        Full
    }

    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final SettingGroup sgRender = settings.createGroup("Render");

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

    public int minX = Math.min(pos1.get().getX(), pos2.get().getX());
    public int minY = Math.min(pos1.get().getY(), pos2.get().getY());
    public int minZ = Math.min(pos1.get().getZ(), pos2.get().getZ());
    public int maxX = Math.max(pos1.get().getX(), pos2.get().getX());
    public int maxY = Math.max(pos1.get().getY(), pos2.get().getY());
    public int maxZ = Math.max(pos1.get().getZ(), pos2.get().getZ()); 

    public CuboidMarker() {
        super(type);
    }

    @Override
    public String getTypeName() {
        return type;
    }

    @Override
    protected void render(Render3DEvent event) {
  /*      int minX = Math.min(pos1.get().getX(), pos2.get().getX());
        int minY = Math.min(pos1.get().getY(), pos2.get().getY());
        int minZ = Math.min(pos1.get().getZ(), pos2.get().getZ());
        int maxX = Math.max(pos1.get().getX(), pos2.get().getX());
        int maxY = Math.max(pos1.get().getY(), pos2.get().getY());
        int maxZ = Math.max(pos1.get().getZ(), pos2.get().getZ()); */

        event.renderer.box(minX, minY, minZ, maxX, maxY, maxZ, sideColor.get(), lineColor.get(), shapeMode.get(), 0);
    }

    private void mine()
    {
        for(int x = minX; x < maxX; x++)
        for(int y = minY; y < maxY; y++)
        for(int z = minZ; z < maxZ; z++)
        BlockUtils.breakBlock(new BlockPos(x, y, z), swing.get());
    }

    public WWidget breakWidget(GuiTheme theme)
    {
        WButton mine = theme.button("Mine");
        mine.action = () -> mine();
        return mine;
    }
}
