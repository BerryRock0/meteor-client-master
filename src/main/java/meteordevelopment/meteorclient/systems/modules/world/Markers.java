package meteordevelopment.meteorclient.systems.modules.world;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;


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

import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;

public class Markers extends Module
{
	public Markers()
	{
        super(Categories.World, "markers", "Allows you to create marker units.");
    }

    @EventHandler
    private void onRender(Render3DEvent event)
    {
        for (Frame unit : Frames.get())
        if(unit.render.get())
            event.renderer.box(unit.startPos.get().getX(), unit.startPos.get().getY(), unit.startPos.get().getZ(), unit.endPos.get().getX(), unit.endPos.get().getY(), unit.endPos.get().getZ(), unit.sideColor.get(), unit.lineColor.get(), unit.shapeMode.get(), 0);
    }

    public boolean visualEmpty(BlockPos pos)
    { 
        for(Frame frame: Frames.get())
            if (pos != null)   
                return frame.visualMode.get() == frame.visualMode.get().Empty && inFrames(pos);
 
        return pos == null
    }
    public boolean visualFull(BlockPos pos)
    {
        for(Frame frame: Frames.get())
            if (pos != null)    
                return frame.visualMode.get() == frame.visualMode.get().Full && inFrames(pos);
 
        return pos == null
    }
    public boolean collisionEmpty(BlockPos pos)
    {
        for(Frame frame: Frames.get())
            if (pos != null)   
                return frame.collisionMode.get() == frame.collisionMode.get().Empty && inFrames(pos);

        return pos == null;
    }
    public boolean collisionFull(BlockPos pos)
    {
        for(Frame frame: Frames.get())
            if(pos != null)    
                return frame.collisionMode.get() == frame.collisionMode.get().Full && inFrames(pos);

        return pos == null;
    }

    public boolean inFrames(BlockPos pos)
    {
        for(Frame obj: Frames.get())
            if (pos != null)
                return (pos.getX() >= obj.startPos.get().getX() && pos.getX() <= obj.endPos.get().getX()) && (pos.getY() >= obj.startPos.get().getY() && pos.getY() <= obj.endPos.get().getY()) && (pos.getZ() >= obj.startPos.get().getZ() && pos.getZ() <= obj.endPos.get().getZ());

	    return pos == null;
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
