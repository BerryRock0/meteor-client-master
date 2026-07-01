
package meteordevelopment.meteorclient.systems.modules.world;

import meteordevelopment.orbit.EventHandler;
import meteordevelopment.meteorclient.events.game.OpenScreenEvent;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.screens.EditSystemScreen;
import meteordevelopment.meteorclient.gui.widgets.WLabel;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.gui.widgets.containers.WTable;
import meteordevelopment.meteorclient.gui.widgets.pressable.WButton;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.minerplacers.MinerPlacer;
import meteordevelopment.meteorclient.systems.minerplacers.MinerPlacers;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.world.BlockUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;

public class WorkersModule extends Module
{
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final SettingGroup sgExecute = settings.createGroup("Execute");
    private final SettingGroup sgControl = settings.createGroup("Control");

    //Execute
    public final Setting<Boolean> pre = sgExecute.add(new BoolSetting.Builder()
        .name("pre")
        .description("Load script before tick.")
        .defaultValue(false)
        .build()
    );
    
    public final Setting<Boolean> post = sgExecute.add(new BoolSetting.Builder()
        .name("post")
        .description("Load script after tick.")
        .defaultValue(false)
        .build()
    );
    public final Setting<Boolean> debug = sgExecute.add(new BoolSetting.Builder()
        .name("debug")
        .description("Print errors on logs.")
        .defaultValue(false)
        .build()
    );
    
    //General
    public final Setting<UseHand> breakHand = sgGeneral.add(new EnumSetting.Builder<UseHand>()
        .name("break-hand")
        .description("Hand to break.")
        .defaultValue(UseHand.Main)
        .build()
    );
    
    public final Setting<UseHand> interactHand = sgGeneral.add(new EnumSetting.Builder<UseHand>()
        .name("interact-hand")
        .description("Hand to interact.")
        .defaultValue(UseHand.Main)
        .build()
    );

    public final Setting<Boolean> placingswing = sgGeneral.add(new BoolSetting.Builder()
        .name("placing-swing")
        .description("Doing placing swing.")
        .defaultValue(false)
        .build()
    );
    
    public final Setting<Boolean> breakingswing = sgGeneral.add(new BoolSetting.Builder()
        .name("breaking-swing")
        .description("Doing breaking swing.")
        .defaultValue(false)
        .build()
    );

    public int s,c;

	public WorkersModule()
	{
        super(Categories.World, "workers", "Allows you to create worker units.");
    }

    @EventHandler
    private void onTickPre(TickEvent.Pre event)
    {
        if (pre.get())
            engine();
    }
        
    @EventHandler
    private void onTickPre(TickEvent.Post event) 
    {
        if (post.get())
            engine();
    }

    @EventHandler
    private void onRender(Render3DEvent event)
    {
        for (MinerPlacer unit : MinerPlacers.get())
        if(unit.render.get())
            event.renderer.box(new BlockPos(unit.x, unit.y, unit.z), unit.sideColor.get(), unit.lineColor.get(), unit.shapeMode.get(), 0);
    }
    
	@Override
    public WWidget getWidget(GuiTheme theme)
    {
        if (!Utils.canUpdate())
            return theme.label("You need to be in a world.");

        WTable table = theme.table();
        initTable(theme, table);
        return table;
    }

    private void engine()
    {
        for (MinerPlacer unit : MinerPlacers.get())
        {
            try
            {
                work(unit, unit.breakBlock.get(), unit.interactBlock.get());
                translate(unit, unit.script.get().charAt(unit.c), unit.handler.get());
                step(unit, unit.stepper.get());
            }
            catch (Exception e)
            {if(debug.get()) e.printStackTrace();}
        }
    }    

    private void work(MinerPlacer unit, boolean a, boolean b)
    {
        if(a) BlockUtils.breakBlock(new BlockPos(unit.x, unit.y, unit.z), direction(unit, new BlockPos(unit.x, unit.y, unit.z)), usedBreakHand(), breakingswing.get());
        if(b) BlockUtils.interact(new BlockHitResult(new BlockPos(unit.x, unit.y, unit.z).getCenter(), direction(unit, new BlockPos(unit.x, unit.y, unit.z)), new BlockPos(unit.x, unit.y, unit.z), unit.insideBlock.get()), usedInteractHand(), placingswing.get());
    }

    private void translate(MinerPlacer unit, char ch, boolean t)
    {
        if(t)
        switch (ch)
        {
            case '_': return;  
            case 'X': unit.x++; break;
            case 'Y': unit.y++; break;
            case 'Z': unit.z++; break;
            case 'S': s++; break;
            case 'C': c++; break; 
            case 'x': unit.x--; break;
            case 'y': unit.y--; break;
            case 'z': unit.z--; break;
            case 's': s--; break;
            case 'c': c--; break;
            case '~': InvUtils.swap(s, false); break;    
            case '%': InvUtils.move().from(c).to(s); break;
            case '\\':unit.x=unit.zero.get().getX(); break;    
            case '|': unit.y=unit.zero.get().getY(); break;
            case '/': unit.z=unit.zero.get().getZ(); break;
            case '&': unit.setColumn(unit.column.get()); break;    
            case '*': unit.handler.set(!unit.handler.get()); break;
            case '^': unit.stepper.set(!unit.stepper.get()); break;
            case '!': unit.breakBlock.set(!unit.breakBlock.get()); break;    
            case '?': unit.interactBlock.set(!unit.interactBlock.get()); break;
            case '-': BlockUtils.breakBlock(new BlockPos(unit.x, unit.y, unit.z), direction(unit, new BlockPos(unit.x, unit.y, unit.z)), usedBreakHand(), breakingswing.get()); break;
            case '+': BlockUtils.interact(new BlockHitResult(new BlockPos(unit.x, unit.y, unit.z).getCenter(), direction(unit, new BlockPos(unit.x, unit.y, unit.z)), new BlockPos(unit.x, unit.y, unit.z), unit.insideBlock.get()), usedInteractHand(), placingswing.get()); break;
            default: break;
        }
    }

    private void step(MinerPlacer unit, boolean s)
    {
        if(s)
        switch (unit.stepDirections.get())
        {
            case None -> {}
            case Increment -> {unit.c++;}
            case Decrement -> {unit.c--;}
        }
    }

    public Direction direction(MinerPlacer unit, BlockPos pos)
    {
        switch (unit.cardinaldirection.get())
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

    private void initTable(GuiTheme theme, WTable table)
    {
        table.clear();

        for (MinerPlacer unit : MinerPlacers.get())
        {
            WButton restart = table.add(theme.button("Restart")).widget(); restart.action = () -> {unit.setColumn(unit.column.get());};
            WButton ix = table.add(theme.button("+X")).widget(); ix.action = () -> unit.x++;
            WButton dx = table.add(theme.button("-x")).widget(); dx.action = () -> unit.x--;
            WButton iy = table.add(theme.button("+Y")).widget(); iy.action = () -> unit.y++;
            WButton dy = table.add(theme.button("-y")).widget(); dy.action = () -> unit.y--;
            WButton iz = table.add(theme.button("+Z")).widget(); iz.action = () -> unit.z++;
            WButton dz = table.add(theme.button("-z")).widget(); dz.action = () -> unit.z--;
            WButton tx = table.add(theme.button("θX")).widget(); tx.action = () -> {unit.x=unit.zero.get().getX();};
            WButton ty = table.add(theme.button("θY")).widget(); ty.action = () -> {unit.y=unit.zero.get().getY();};
            WButton tz = table.add(theme.button("θZ")).widget(); tz.action = () -> {unit.z=unit.zero.get().getZ();};
            WButton mine = table.add(theme.button("Mine")).widget(); mine.action = () -> {unit.breakBlock.set(!unit.breakBlock.get());};
            WButton interact = table.add(theme.button("Interact")).widget(); interact.action = () -> {unit.interactBlock.set(!unit.interactBlock.get());};
            WButton handle = table.add(theme.button("Handle")).widget(); handle.action = () -> {unit.handler.set(!unit.handler.get());};
            WButton step = table.add(theme.button("Step")).widget(); step.action = () -> {unit.stepper.set(!unit.stepper.get());};
            WButton edit = table.add(theme.button("Edit")).widget(); edit.action = () -> mc.gui.setScreen(new EditMinerPlacerScreen(theme, unit, () -> initTable(theme, table)));
            WButton delete = table.add(theme.button("Delete")).widget(); delete.action = () -> {MinerPlacers.get().remove(unit); initTable(theme, table);};

            table.row();
        }

        table.add(theme.horizontalSeparator()).expandX();
        table.row();

        WButton create = table.add(theme.button("Create")).expandX().widget(); create.action = () -> mc.gui.setScreen(new EditMinerPlacerScreen(theme, null, () -> initTable(theme, table)));
    }
	
	 private static class EditMinerPlacerScreen extends EditSystemScreen<MinerPlacer>
	 {
        public EditMinerPlacerScreen(GuiTheme theme, MinerPlacer value, Runnable reload) {
            super(theme, value, reload);
        }

        @Override
        public MinerPlacer create()
        {
            return new MinerPlacer.Builder().build();
        }

        @Override
        public boolean save()
        {
            MinerPlacers.get().add(value);
			return true;
        }

        @Override
        public Settings getSettings()
        {
            return value.settings;
        }
    }

	public InteractionHand usedInteractHand()
    {
        switch(interactHand.get())
        {
            case Main -> {return InteractionHand.MAIN_HAND;}
            case Off -> {return InteractionHand.OFF_HAND;}
        }
        return null;
    }

    public InteractionHand usedBreakHand()
    {
        switch(breakHand.get())
        {
            case Main -> {return InteractionHand.MAIN_HAND;}
            case Off -> {return InteractionHand.OFF_HAND;}
        }
        return null;
    }

    public enum UseHand
    {
        Main,
        Off
    }
}
