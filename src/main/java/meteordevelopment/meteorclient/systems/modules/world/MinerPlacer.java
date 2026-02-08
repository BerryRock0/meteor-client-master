package meteordevelopment.meteorclient.systems.modules.world;

import meteordevelopment.orbit.EventHandler;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.gui.widgets.containers.WHorizontalList;
import meteordevelopment.meteorclient.gui.widgets.containers.WVerticalList;
import meteordevelopment.meteorclient.gui.widgets.pressable.WButton;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.meteorclient.renderer.ShapeMode;

import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.hit.BlockHitResult;

public class MinerPlacer extends Module
{
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final SettingGroup sgControl = settings.createGroup("Control");
    private final SettingGroup sgExecution = settings.createGroup("Execution");
    private final SettingGroup sgVisual = settings.createGroup("Visual");
    
    private final Setting<BlockPos> zero = sgGeneral.add(new BlockPosSetting.Builder()
        .name("zero-pos")
        .description("Mining block position")
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
    private final Setting<CardinalDirections> cardinaldirection = sgExecution.add(new EnumSetting.Builder<CardinalDirections>()
        .name("Place-Direction")
        .description("Direction to use.")
        .defaultValue(CardinalDirections.Down)
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
    private final Setting<Boolean> render = sgVisual.add(new BoolSetting.Builder()
        .name("render")
        .description("Renders a block overlay where the obsidian will be placed.")
        .defaultValue(true)
        .build()
    );
    private final Setting<ShapeMode> shapeMode = sgVisual.add(new EnumSetting.Builder<ShapeMode>()
        .name("shape-mode")
        .description("How the shapes are rendered.")
        .defaultValue(ShapeMode.Both)
        .build()
    );

    private final Setting<SettingColor> sideColor = sgVisual.add(new ColorSetting.Builder()
        .name("side-color")
        .description("The color of the sides of the blocks being rendered.")
        .defaultValue(new SettingColor(0, 0, 0, 0))
        .build()
    );

    private final Setting<SettingColor> lineColor = sgVisual.add(new ColorSetting.Builder()
        .name("line-color")
        .description("The color of the lines of the blocks being rendered.")
        .defaultValue(new SettingColor(0, 0, 0, 0))
        .build()
    );
    
    public int x,y,z;
    public BlockPos pos;

    public MinerPlacer()
    {
        super(Categories.World, "MinerPlacer", "Break or Place in specific coordinate.");
    }

    @EventHandler
    private void onTickPre(TickEvent.Pre event)
    {
        if (pre.get())
            main();
    }
        
    @EventHandler
    private void onTickPre(TickEvent.Post event) 
    {
        if (post.get())
            main();
    }

    @EventHandler
    private void onRender(Render3DEvent event)
    {
        pos = new BlockPos(x,y,z);
        if(render.get())
            event.renderer.box(pos, sideColor.get(), lineColor.get(), shapeMode.get(), 0);
    }

    public void main()
    {   
        pos = new BlockPos(x,y,z);
        if(mining.get())
            BlockUtils.breakBlock(pos, false);
        if(using.get())
            BlockUtils.interact(new BlockHitResult(pos.toCenterPos(), direction(), pos, true), Hand.MAIN_HAND, false);   
    }

    public Direction direction()
    {
        switch (cardinaldirection.get())
        {
            case Up -> {return Direction.UP;}
            case Down -> {return Direction.DOWN;}
            case North -> {return Direction.NORTH;}
            case South -> {return Direction.SOUTH;}
            case East -> {return Direction.EAST;}
            case West -> {return Direction.WEST;}     
        }
        return null;
    }

    public WWidget getWidget(GuiTheme theme)
    {
        WVerticalList list = theme.verticalList();
        WHorizontalList b = list.add(theme.horizontalList()).expandX().widget();
        
        WButton ix = b.add(theme.button("x++")).expandX().widget(); ix.action = () -> x++;
        WButton iy = b.add(theme.button("y++")).expandX().widget(); iy.action = () -> y++;
        WButton iz = b.add(theme.button("z++")).expandX().widget(); iz.action = () -> z++;
        WButton dx = b.add(theme.button("x--")).expandX().widget(); dx.action = () -> x--;
        WButton dy = b.add(theme.button("y--")).expandX().widget(); dy.action = () -> y--;
        WButton dz = b.add(theme.button("z--")).expandX().widget(); dz.action = () -> z--;
        WButton set = list.add(theme.button("Set")).expandX().widget(); set.action = () -> {x=zero.get().getX(); y=zero.get().getY(); z=zero.get().getZ();};
        
        return list;
    }

    public enum CardinalDirections
    {
        Up,
        Down,
        North,
        South,
        East,
        West
    }
}
