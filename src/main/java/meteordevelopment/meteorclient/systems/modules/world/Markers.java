
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
import meteordevelopment.meteorclient.systems.cuboids.Frame;
import meteordevelopment.meteorclient.systems.cuboids.Frames;
import meteordevelopment.meteorclient.utils.Utils;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;

public class Markers extends Module
{
	public Markers()
	{
        super(Categories.World, "markers", "Allows you to create marker units.");
    }

    public Frame unit()
    {
        Frame value;
        for(Frame unit: Frames:get())
        value = unit;

        return value;
    }

    public boolean fullBlock(BlockPos pos)
    {
        if (inFrames(unit(), pos) && unit().fullBlock.get())
            return isActive() && unit().fullBlockCase.get();
        return isActive() && unit().fullBlockFinal.get();   
    }
    
    public boolean emptyBlock(BlockPos pos)
    {
        if (inFrames(unit(), pos) && unit().emptyBlock.get())
            return isActive() && unit().emptyBlockCase.get();
        return isActive() && unit().emptyBlockFinal.get();
    }

    public boolean fullPlayer(BlockPos pos)
    {
        if (inFrames(unit(), pos) && unit().fullPlayer.get())
            return isActive() && unit().fullPlayerCase.get();
        return isActive() && unit().fullPlayerFinal.get();   
    }
    
    public boolean emptyPlayer(BlockPos pos)
    {
        if (inFrames(unit(), pos) && unit().emptyPlayer.get())
            return isActive() && unit().emptyPlayerCase.get();
        return isActive() && unit().emptyPlayerFinal.get();
    }

    public boolean inFrames(Frame frame, BlockPos pos)
    {
		boolean x = pos.getX() >= frame.startPos.getX() && pos.getX() <= frame.endPos.getX();
		boolean y = pos.getY() >= frame.startPos.getY() && pos.getX() <= frame.endPos.getY();
		boolean z = pos.getZ() >= frame.startPos.getZ() && pos.getX() <= frame.endPos.getZ();
		
		return x&&y&&z;
	}

    @EventHandler
    private void onRender(Render3DEvent event)
    {
        for (Frame unit : Frames.get())
        if(unit.render.get())
            event.renderer.box(unit.startPos.get().getX(), unit.startPos.get().getY(), unit.startPos.get().getZ(), unit.endPos.get().getX(), unit.endPos.get().getY(), unit.endPos.get().getZ(), unit.sideColor.get(), unit.lineColor.get(), unit.shapeMode.get(), 0);
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

    private void initTable(GuiTheme theme, WTable table)
    {
        table.clear();

        for (Frame unit : Frames.get())
        {
            WButton edit = table.add(theme.button("Edit")).widget(); edit.action = () -> mc.gui.setScreen(new EditFrameScreen(theme, unit, () -> initTable(theme, table)));
            WButton delete = table.add(theme.button("Delete")).widget(); delete.action = () -> {Frames.get().remove(unit); initTable(theme, table);};

            table.row();
        }

        table.add(theme.horizontalSeparator()).expandX();
        table.row();

        WButton create = table.add(theme.button("Create")).expandX().widget(); create.action = () -> mc.gui.setScreen(new EditFrameScreen(theme, null, () -> initTable(theme, table)));
    }
	
	 private static class EditFrameScreen extends EditSystemScreen<Frame>
	 {
        public EditFrameScreen(GuiTheme theme, Frame value, Runnable reload) {
            super(theme, value, reload);
        }

        @Override
        public Frame create()
        {
            return new Frame.Builder().build();
        }

        @Override
        public boolean save()
        {
            Frames.get().add(value);
			return true;
        }

        @Override
        public Settings getSettings()
        {
            return value.settings;
        }
    }
}
