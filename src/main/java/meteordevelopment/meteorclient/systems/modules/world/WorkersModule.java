
package meteordevelopment.meteorclient.systems.modules.world;

import java.util.List;
import java.util.ArrayList;

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
import meteordevelopment.meteorclient.gui.widgets.pressable.WCheckbox;
import meteordevelopment.meteorclient.gui.widgets.pressable.WConfirmedMinus;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.minerplacers.MinerPlacer;
import meteordevelopment.meteorclient.systems.minerplacers.MinerPlacers;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.world.BlockUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockHitResult;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;

public class WorkersModule extends Module
{
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final SettingGroup sgExecute = settings.createGroup("Execute");

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

	public WorkersModule()
	{
        super(Categories.World, "workers", "Allows you to create worker units.");
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

    private void main()
    {
        for (MinerPlacer unit : MinerPlacers.get())
        try
        {
            action(unit, unit.breakBlock.get(), unit.interactBlock.get());                
            translate(unit, unit.script.get().charAt(unit.c), unit.handler.get());
            step(unit, unit.c!=unit.script.get().length(), unit.c==unit.script.get().length(), unit.stepper.get());
        }
        catch (Exception e)
        {}
    }

    public void action(MinerPlacer unit, boolean a, boolean b)
    {
        if(a) BlockUtils.breakBlock(new BlockPos(unit.x, unit.y, unit.z), direction(unit, new BlockPos(unit.x, unit.y, unit.z)), usedBreakHand(), breakingswing.get());
        if(b) BlockUtils.interact(new BlockHitResult(new BlockPos(unit.x, unit.y, unit.z).toCenterPos(), direction(unit, new BlockPos(unit.x, unit.y, unit.z)), new BlockPos(unit.x, unit.y, unit.z), unit.insideBlock.get()), usedInteractHand(), placingswing.get());
    }

    public void step(MinerPlacer unit, boolean i, boolean d, boolean s)
    {
        if(s)
        {
            if (i) unit.c++;
            if (d) unit.c--;
        }
    }

    public void translate(MinerPlacer unit, char c, boolean t)
    {
        if(t)
        switch (c)
        {
            case '_': return;
            case 'X': unit.x++; break;
            case 'Y': unit.y++; break;
            case 'Z': unit.z++; break;
            case 'x': unit.x--; break;
            case 'y': unit.y--; break;
            case 'z': unit.z--; break;
            case '-': unit.breakBlock.set(!unit.breakBlock.get()); break;    
            case '+': unit.interactBlock.set(!unit.interactBlock.get()); break;
            case ';': unit.setColumn(unit.column.get()); break;
            default: break;
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
            WLabel name = table.add(theme.label(unit.name.get())).expandCellX().widget();
            WCheckbox mine = table.add(theme.checkbox(unit.breakBlock.get())).widget(); mine.action = () -> {unit.breakBlock.set(mine.checked);};
            WCheckbox place = table.add(theme.checkbox(unit.interactBlock.get())).widget(); place.action = () -> {unit.interactBlock.set(place.checked);};
            WCheckbox handle = table.add(theme.checkbox(unit.handler.get())).widget(); handle.action = () -> {unit.handler.set(handle.checked);};
            WCheckbox step = table.add(theme.checkbox(unit.stepper.get())).widget(); step.action = () -> {unit.stepper.set(step.checked);};
            WButton ix = table.add(theme.button("x++")).widget(); ix.action = () -> unit.x++;
            WButton iy = table.add(theme.button("y++")).widget(); iy.action = () -> unit.y++;
            WButton iz = table.add(theme.button("z++")).widget(); iz.action = () -> unit.z++;
            WButton dx = table.add(theme.button("x--")).widget(); dx.action = () -> unit.x--;
            WButton dy = table.add(theme.button("y--")).widget(); dy.action = () -> unit.y--;
            WButton dz = table.add(theme.button("z--")).widget(); dz.action = () -> unit.z--;
            WButton sx = table.add(theme.button("SX")).widget(); sx.action = () -> {unit.x=unit.zero.get().getX();};
            WButton sy = table.add(theme.button("SY")).widget(); sy.action = () -> {unit.y=unit.zero.get().getY();};
            WButton sz = table.add(theme.button("SZ")).widget(); sz.action = () -> {unit.z=unit.zero.get().getZ();};
            WButton sc = table.add(theme.button("SC")).widget(); sc.action = () -> {unit.setColumn(unit.column.get());};
            WButton edit = table.add(theme.button(GuiRenderer.EDIT)).widget(); edit.action = () -> mc.setScreen(new EditMinerPlacerScreen(theme, unit, () -> initTable(theme, table)));
            WConfirmedMinus remove = table.add(theme.confirmedMinus()).widget(); remove.action = () -> {MinerPlacers.get().remove(unit); initTable(theme, table);};
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
            if (value.name.get().isBlank())
				return false;

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
