
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

    //Control
    public final Setting<Boolean> angle = sgControl.add(new BoolSetting.Builder()
        .name("angle")
        .description("")
        .defaultValue(false)
        .build()
    );
	public final Setting<Boolean> movement = sgControl.add(new BoolSetting.Builder()
        .name("movement")
        .description("")
        .defaultValue(false)
        .build()
    );
    public final Setting<Boolean> forward = sgControl.add(new BoolSetting.Builder()
        .name("forward")
        .description("")
        .defaultValue(false)
        .build()
    );
    public final Setting<Boolean> back = sgControl.add(new BoolSetting.Builder()
        .name("back")
        .description("")
        .defaultValue(false)
        .build()
    );
    public final Setting<Boolean> left = sgControl.add(new BoolSetting.Builder()
        .name("left")
        .description("")
        .defaultValue(false)
        .build()
    );
    public final Setting<Boolean> right = sgControl.add(new BoolSetting.Builder()
        .name("right")
        .description("")
        .defaultValue(false)
        .build()
    );
    public final Setting<Boolean> jump = sgControl.add(new BoolSetting.Builder()
        .name("jump")
        .description("")
        .defaultValue(false)
        .build()
    );
    public final Setting<Boolean> sneak = sgControl.add(new BoolSetting.Builder()
        .name("sneak")
        .description("")
        .defaultValue(false)
        .build()
    );
    public final Setting<Boolean> attack = sgControl.add(new BoolSetting.Builder()
        .name("attack")
        .description("")
        .defaultValue(false)
        .build()
    );
    public final Setting<Boolean> use = sgControl.add(new BoolSetting.Builder()
        .name("use")
        .description("")
        .defaultValue(false)
        .build()
    );

    public int s,c,x,y;

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
                work(unit, unit.breakBlock.get(), unit.interactBlock.get(), movement.get(), angle.get());
                translate(unit, unit.script.get().charAt(unit.c), unit.handler.get());
                step(unit, unit.c!=unit.script.get().length(), unit.c==unit.script.get().length(), unit.stepper.get());
            }
            catch (Exception e)
            {}
            
            if (unit.include.get()) break;
            if (unit.exclude.get()) continue;
        }
    }    

    private void work(MinerPlacer unit, boolean a, boolean b, boolean c, boolean d)
    {
        if(a) BlockUtils.breakBlock(new BlockPos(unit.x, unit.y, unit.z), direction(unit, new BlockPos(unit.x, unit.y, unit.z)), usedBreakHand(), breakingswing.get());
        if(b) BlockUtils.interact(new BlockHitResult(new BlockPos(unit.x, unit.y, unit.z).getCenter(), direction(unit, new BlockPos(unit.x, unit.y, unit.z)), new BlockPos(unit.x, unit.y, unit.z), unit.insideBlock.get()), usedInteractHand(), placingswing.get());

        if (c)
        {
            mc.options.keyUp.setDown(forward.get());
            mc.options.keyDown.setDown(back.get());
            mc.options.keyLeft.setDown(left.get());
            mc.options.keyRight.setDown(right.get());
            mc.options.keyShift.setDown(jump.get());
            mc.options.keyJump.setDown(sneak.get());
            mc.options.keyAttack.setDown(use.get());
            mc.options.keyUse.setDown(attack.get());  
        }

        if(d)
        {
            mc.player.setXRot((float)x);
            mc.player.setYRot((float)y); 
        }
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
            case 'H': x++; break;
            case 'V': y++; break;    
            case 'x': unit.x--; break;
            case 'y': unit.y--; break;
            case 'z': unit.z--; break;
            case 's': s--; break;
            case 'c': c--; break;
            case 'h': x--; break;
            case 'v': y--; break;
            case '0': movement.set(!movement.get());
            case '1': forward.set(!forward.get());    
            case '2': back.set(!back.get());
            case '3': left.set(!left.get());
            case '4': right.set(!right.get());
            case '5': jump.set(!jump.get());
            case '6': sneak.set(!sneak.get());
            case '7': use.set(!use.get());
            case '8': attack.set(!attack.get());
            case '9': angle.set(!angle.get());
            case '@': InvUtils.swap(s, false); break;    
            case '%': InvUtils.move().from(c).to(s); break;
            case '\\':unit.x=unit.zero.get().getX(); break;    
            case '|': unit.y=unit.zero.get().getY(); break;
            case '/': unit.z=unit.zero.get().getZ(); break;
            case '&': unit.setColumn(unit.column.get()); break;    
            case ':': unit.include.set(unit.include.get()); break;
            case ';': unit.exclude.set(unit.exclude.get()); break;
            case '*': unit.handler.set(!unit.handler.get()); break;
            case '^': unit.stepper.set(!unit.stepper.get()); break;
            case '-': unit.breakBlock.set(!unit.breakBlock.get()); break;    
            case '+': unit.interactBlock.set(!unit.interactBlock.get()); break;
            default: break;
        }
    }

    private void step(MinerPlacer unit, boolean i, boolean d, boolean s)
    {
        if(s)
        {
            if (i) unit.c++;
            if (d) unit.c--;
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
            WButton ix = table.add(theme.button("x++")).widget(); ix.action = () -> unit.x++;
            WButton dx = table.add(theme.button("x--")).widget(); dx.action = () -> unit.x--;
            WButton iy = table.add(theme.button("y++")).widget(); iy.action = () -> unit.y++;
            WButton dy = table.add(theme.button("y--")).widget(); dy.action = () -> unit.y--;
            WButton iz = table.add(theme.button("z++")).widget(); iz.action = () -> unit.z++;
            WButton dz = table.add(theme.button("z--")).widget(); dz.action = () -> unit.z--;
            WButton tx = table.add(theme.button("θX")).widget(); tx.action = () -> {unit.x=unit.zero.get().getX();};
            WButton ty = table.add(theme.button("θY")).widget(); ty.action = () -> {unit.y=unit.zero.get().getY();};
            WButton tz = table.add(theme.button("θZ")).widget(); tz.action = () -> {unit.z=unit.zero.get().getZ();};
            WButton edit = table.add(theme.button("Edit")).widget(); edit.action = () -> mc.setScreen(new EditMinerPlacerScreen(theme, unit, () -> initTable(theme, table)));
            WButton delete = table.add(theme.button("Delete")).widget(); delete.action = () -> {MinerPlacers.get().remove(unit); initTable(theme, table);};

            table.row();
        }

        table.add(theme.horizontalSeparator()).expandX();
        table.row();

        WButton create = table.add(theme.button("Create")).expandX().widget(); create.action = () -> mc.setScreen(new EditMinerPlacerScreen(theme, null, () -> initTable(theme, table)));
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
