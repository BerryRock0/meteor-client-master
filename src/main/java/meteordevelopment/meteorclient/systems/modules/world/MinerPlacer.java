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
import meteordevelopment.meteorclient.utils.player.Rotations;
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
    private final SettingGroup sgTimer = settings.createGroup("Timer");

    private final Setting<BlockPos> zero = sgGeneral.add(new BlockPosSetting.Builder()
        .name("zero-pos")
        .description("Mining block position")
        .build()
    );
    
    private final Setting<Boolean> breakBlock = sgGeneral.add(new BoolSetting.Builder()
        .name("breaking-block")
        .description("Break blocks in area.")
        .defaultValue(false)
        .build()
    );
    private final Setting<Boolean> interactBlock = sgGeneral.add(new BoolSetting.Builder()
        .name("interacting-block")
        .description("Intreact blocks in area.")
        .defaultValue(false)
        .build()
    );
    
    private final Setting<Rotate> rotate = sgSettings.add(new EnumSetting.Builder<Rotate>()
        .name("rotate")
        .description("Switch rotates mode.")
        .defaultValue(Rotate.None)
        .build()
    );
    
    private final Setting<CardinalDirections> cardinaldirection = sgSettings.add(new EnumSetting.Builder<CardinalDirections>()
        .name("place-pirection")
        .description("Direction to use.")
        .defaultValue(CardinalDirections.Down)
        .build()
    );

    private final Setting<UseHand> breakHand = sgSettings.add(new EnumSetting.Builder<UseHand>()
        .name("break-hand")
        .description("Hand to break.")
        .defaultValue(UseHand.Main)
        .build()
    );
    
    private final Setting<UseHand> interactHand = sgSettings.add(new EnumSetting.Builder<UseHand>()
        .name("interact-hand")
        .description("Hand to interact.")
        .defaultValue(UseHand.Main)
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


    
    //Script
    private final Setting<Boolean> run = sgScript.add(new BoolSetting.Builder()
        .name("run")
        .description("Fire script execution.")
        .defaultValue(false)
        .build()
    );

    private final Setting<Boolean> scriptincrement = sgScript.add(new BoolSetting.Builder()
        .name("always-increment")
        .description("Execute script from beginning to end.")
        .defaultValue(false)
        .build()
    );
    private final Setting<Boolean> scriptdecrement = sgScript.add(new BoolSetting.Builder()
        .name("always-decrement")
        .description("Execute script from end to beginning.")
        .defaultValue(false)
        .build()
    );

    private final Setting<Integer> line = sgScript.add(new IntSetting.Builder()
        .name("line")
        .description("Reset line value.")
        .defaultValue(0)
        .build()
    );

     private final Setting<Integer> column = sgScript.add(new IntSetting.Builder()
        .name("column")
        .description("Reset column value.")
        .defaultValue(0)
        .build()
    );
    
    private final Setting<List<String>> script = sgScript.add(new StringListSetting.Builder()
        .name("script")
        .description("Minerplacer action commands.")
        .build()
    );

    //Timer
    private final Setting<Integer> begindelay = sgScript.add(new IntSetting.Builder()
        .name("begin-delay")
        .description("Script timer begin value")
        .defaultValue(0)
        .build()
    );
    private final Setting<Integer> enddelay = sgScript.add(new IntSetting.Builder()
        .name("end-delay")
        .description("Script timer end value")
        .defaultValue(0)
        .build()
    );

    private final Setting<Boolean> timerincrement = sgScript.add(new BoolSetting.Builder()
        .name("timer-increment")
        .description("Execute script from beginning to end.")
        .defaultValue(false)
        .build()
    );
    private final Setting<Boolean> timerdecrement = sgScript.add(new BoolSetting.Builder()
        .name("timer-decrement")
        .description("Execute script from end to beginning.")
        .defaultValue(false)
        .build()
    );

    //Render
    private final Setting<Boolean> render = sgRender.add(new BoolSetting.Builder()
        .name("render")
        .description("Renders a block overlay where the obsidian will be placed.")
        .defaultValue(false)
        .build()
    );

    private final Setting<Boolean> placingswing = sgRender.add(new BoolSetting.Builder()
        .name("placing-swing")
        .description("Doing placing swing.")
        .defaultValue(false)
        .build()
    );
    private final Setting<Boolean> breakingswing = sgRender.add(new BoolSetting.Builder()
        .name("breaking-swing")
        .description("Doing breaking swing.")
        .defaultValue(false)
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

    public int c,l,t,x,y,z;
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

        switch(rotate.get())
        {
            case None -> {work();}
            case Client -> {clientAngle(); work();}
            case Packet -> {Rotations.rotate(Rotations.getYaw(pos), Rotations.getPitch(pos), () -> {work();});}    
        }

        try
        {
            if (t != (int)enddelay.get())
            {
                if(timerincrement.get()) t++;
                if(timerdecrement.get()) t--;
                return;
            }
            t = (int)begindelay.get(); 
            
            if (run.get())
                execute(script.get().get(l).charAt(c));
            if (c != script.get().get(l).length()-1) 
                step();
        }
        catch(Exception e)
        {}
    }

    private void work()
    {
        if(breakBlock.get())
            BlockUtils.breakBlock(pos, usedBreakHand(), breakingswing.get());

        if(interactBlock.get())
            BlockUtils.interact(new BlockHitResult(pos.toCenterPos(), direction(pos), pos, insideBlock.get()), usedInteractHand(), placingswing.get());
    }

    public void step()
    {
        if (scriptincrement.get()) c++;
        if (scriptdecrement.get()) c--;
    }

    public Hand usedInteractHand()
    {
        switch(interactHand.get())
        {
            case Main -> {return Hand.MAIN_HAND;}
            case Off -> {return Hand.OFF_HAND;}
        }
        return null;
    }

    public Hand usedBreakHand()
    {
        switch(interactHand.get())
        {
            case Main -> {return Hand.MAIN_HAND;}
            case Off -> {return Hand.OFF_HAND;}
        }
        return null;
    }

    private void clientAngle()
    {
        mc.player.setYaw((float)Rotations.getYaw(pos)); 
        mc.player.setPitch((float)Rotations.getPitch(pos));
    }
    
    private void execute(char c)
    {   
        switch (c)
        {
            case 'X': x++; break;
            case 'Y': y++; break;
            case 'Z': z++; break;
            case 'x': x--; break;
            case 'y': y--; break;
            case 'z': z--; break;
            case '\\': x=zero.get().getX(); break;
            case '|': y=zero.get().getY(); break;
            case '/': z=zero.get().getZ(); break;  
            case ';': c=0; break;
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

    public void setCursor(int line, int column)
    {
        l = line;
        c = column;
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
        WButton sc = set.add(theme.button("Set_Cursor")).expandX().widget(); sc.action = () -> {setCursor(line.get(), column.get());};
        
        return main;
    }

    public enum Rotate
    {
        None,
        Client,
        Packet
    }
    
    public enum UseHand
    {
        Main,
        Off
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
