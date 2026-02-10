package meteordevelopment.meteorclient.systems.modules.world;

import java.util.List;

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
    private final SettingGroup sgSettings = settings.createGroup("Settings");
    private final SettingGroup sgScript = settings.createGroup("Script");
    private final SettingGroup sgRender = settings.createGroup("Render");

    private final Setting<BlockPos> zero = sgGeneral.add(new BlockPosSetting.Builder()
        .name("zero-pos")
        .description("Mining block position")
        .build()
    );
    private final Setting<Integer> lineint = sgGeneral.add(new IntSetting.Builder()
        .name("Line value")
        .description("Matrix line value.")
        .build()
    );
    private final Setting<Integer> columnint = sgGeneral.add(new IntSetting.Builder()
        .name("Column value")
        .description("Matrix column value.")
        .build()
    );
    private final Setting<Boolean> mining = sgGeneral.add(new BoolSetting.Builder()
        .name("breaking")
        .description("Break blocks in area.")
        .defaultValue(false)
        .build()
    );
    private final Setting<Boolean> interacting = sgGeneral.add(new BoolSetting.Builder()
        .name("interacting")
        .description("Intreact blocks in area.")
        .defaultValue(false)
        .build()
    );

    private final Setting<CardinalDirections> cardinaldirection = sgSettings.add(new EnumSetting.Builder<CardinalDirections>()
        .name("place-pirection")
        .description("Direction to use.")
        .defaultValue(CardinalDirections.Down)
        .build()
    );
    
    private final Setting<Boolean> insideBlock = sgSettings.add(new BoolSetting.Builder()
        .name("inside-block")
        .description("Inside block value.")
        .defaultValue(false)
        .build()
    );
    
    private final Setting<Boolean> pre = sgSettings.add(new BoolSetting.Builder()
        .name("pre")
        .description("Load script before tick.")
        .defaultValue(false)
        .build()
    );
    private final Setting<Boolean> post = sgSettings.add(new BoolSetting.Builder()
        .name("post")
        .description("Load script after tick.")
        .defaultValue(false)
        .build()
    );

    private final Setting<Boolean> run = sgScript.add(new BoolSetting.Builder()
        .name("run")
        .description("Fire script execution.")
        .defaultValue(false)
        .build()
    );
    
    private final Setting<Boolean> incrementIndex = sgScript.add(new BoolSetting.Builder()
        .name("increment")
        .description("Execute script from beginning to end.")
        .defaultValue(false)
        .build()
    );
    private final Setting<Boolean> decrementIndex = sgScript.add(new BoolSetting.Builder()
        .name("decrement")
        .description("Execute script from end to beginning.")
        .defaultValue(false)
        .build()
    );
    
    private final Setting<List<String>> script = sgScript.add(new StringListSetting.Builder()
        .name("script")
        .description("Minerplacer action commands.")
        .build()
    );

    private final Setting<Boolean> render = sgRender.add(new BoolSetting.Builder()
        .name("render")
        .description("Renders a block overlay where the obsidian will be placed.")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> placingswing = sgRender.add(new BoolSetting.Builder()
        .name("placing-swing")
        .description("Doing placing swing.")
        .defaultValue(true)
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
        .defaultValue(new SettingColor(0, 0, 0, 0))
        .build()
    );

    private final Setting<SettingColor> lineColor = sgRender.add(new ColorSetting.Builder()
        .name("line-color")
        .description("The color of the lines of the blocks being rendered.")
        .defaultValue(new SettingColor(0, 0, 0, 0))
        .build()
    );

    public int a = 0;
    public int i = 0;
    public int x,y,z;
    public BlockPos pos;
    public String input;
    public String[] matrix;
    public char[] ch;
    

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

        try
        {
            input = script.get().get(i);
            iterate(input);            
        }
        catch(Exception e)
        {}

        if(mining.get())
            BlockUtils.breakBlock(pos, false);
        if(interacting.get())
            BlockUtils.interact(new BlockHitResult(pos.toCenterPos(), direction(pos), pos, insideBlock.get()), Hand.MAIN_HAND, placingswing.get());   
    }

    public void iterate(String input)
    {
        ch = input.toCharArray();

        if(run.get())
            parseAndExecute(ch[a]);
        a++;
        
        if (a >= ch.length() - 1)
        {
            if (incrementIndex.get()) i++;
            if (decrementIndex.get()) i--;
            a = 0;
        }
    }
    
    private void parseAndExecute(char a)
    {   
        switch (a)
        {
            case 'X': x++; break;
            case 'Y': y++; break;
            case 'Z': z++; break;
            case 'x': x--; break;
            case 'y': y--; break;
            case 'z': z--; break;
            case 'I': i++; break;
            case 'i': i--; break;   
            case '^': i=0; break;
            default: break;
        }
    }

    public Direction direction(BlockPos pos)
    {
        switch (cardinaldirection.get())
        {
            case Auto ->{return BlockUtils.getDirection(pos);}
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
        WVerticalList main = theme.verticalList();
        WVerticalList pm = theme.verticalList();
        WVerticalList nm = theme.verticalList();
        WVerticalList set = theme.verticalList();

        main.add(pm).expandX().widget();
        main.add(nm).expandX().widget();
        main.add(set).expandX().widget();
        
        WHorizontalList a = pm.add(theme.horizontalList()).expandX().widget();
        WHorizontalList b = nm.add(theme.horizontalList()).expandX().widget();
        WHorizontalList c = set.add(theme.horizontalList()).expandX().widget();
        
        WButton ix = a.add(theme.button("x++")).expandX().widget(); ix.action = () -> x++;
        WButton iy = a.add(theme.button("y++")).expandX().widget(); iy.action = () -> y++;
        WButton iz = a.add(theme.button("z++")).expandX().widget(); iz.action = () -> z++;
        WButton dx = b.add(theme.button("x--")).expandX().widget(); dx.action = () -> x--;
        WButton dy = b.add(theme.button("y--")).expandX().widget(); dy.action = () -> y--;
        WButton dz = b.add(theme.button("z--")).expandX().widget(); dz.action = () -> z--;
        WButton sx = c.add(theme.button("Set_X")).expandX().widget(); sx.action = () -> {x=zero.get().getX();};
        WButton sy = c.add(theme.button("Set_Y")).expandX().widget(); sy.action = () -> {y=zero.get().getY();};
        WButton sz = c.add(theme.button("Set_Z")).expandX().widget(); sz.action = () -> {z=zero.get().getZ();};
        WButton reset = set.add(theme.button("Reset")).expandX().widget(); reset.action = () -> {i=0;};
        
        return main;
    }
    
    public enum CardinalDirections
    {
        Auto,
        Up,
        Down,
        North,
        South,
        East,
        West
    }
}
