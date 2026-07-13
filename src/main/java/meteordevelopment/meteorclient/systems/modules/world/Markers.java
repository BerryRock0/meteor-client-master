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

    public Shape visualShape(BlockPos pos)
    {
        for(Frame frame: Frames.get())
        {
            if(frame.blockMode.get() == frame.blockMode.get().Empty) return Shape.empty();
            if(frame.blockMode.get() == frame.blockMode.get().Full) return Shape.full();
        }

        return Shape.absent();
    }

    
    public Shape collisionShape(BlockPos pos)
    {
        for(Frame frame: Frames.get())
        {
            if(frame.blockMode.get() == frame.Mode.get().Empty) return Shape.empty();
            if(frame.blockMode.get() == frame.blockMode.get().Full) return Shape.full();
        }

        return Shape.absent();
    }

    public boolean inFrames(Frame obj, BlockPos pos)
    {
        boolean x = pos.getX() >= obj.startPos.get().getX() && pos.getX() <= obj.endPos.get().getX();
        boolean y = pos.getY() >= obj.startPos.get().getY() && pos.getY() <= obj.endPos.get().getY();
        boolean z = pos.getZ() >= obj.startPos.get().getZ() && pos.getZ() <= obj.endPos.get().getZ();

	    return x && y && z;
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
