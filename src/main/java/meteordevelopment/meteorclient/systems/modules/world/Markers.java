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
    
    public Frame obj;

	public Markers()
	{
        super(Categories.World, "markers", "Allows you to create marker units.");
    }

    public void engine()
    {
        for(Frame unit: Frames.get())
            return obj = unit;

        return obj;
    }

    public boolean fullBlock(BlockPos pos)
    {
        if (inFrames(obj, pos) && obj.fullBlock.get())
            return isActive() && obj.fullBlockCase.get();
        return isActive() && obj.fullBlockFinal.get();   
    }
    
    public boolean emptyBlock(BlockPos pos)
    {
        if (inFrames(obj, pos) && obj.emptyBlock.get())
            return isActive() && obj.emptyBlockCase.get();
        return isActive() && obj.emptyBlockFinal.get();
    }

    public boolean fullPlayer(BlockPos pos)
    {
        if (inFrames(obj, pos) && obj.fullPlayer.get())
            return isActive() && obj.fullPlayerCase.get();
        return isActive() && obj.fullPlayerFinal.get();   
    }
    
    public boolean emptyPlayer(BlockPos pos)
    {
        if (inFrames(obj, pos) && obj.emptyPlayer.get())
            return isActive() && obj.emptyPlayerCase.get();
        return isActive() && obj.emptyPlayerFinal.get();
    }

    public boolean inFrames(Frame frame, BlockPos pos)
    {
		boolean x = pos.getX() >= frame.startPos.get().getX() && pos.getX() <= frame.endPos.get().getX();
		boolean y = pos.getY() >= frame.startPos.get().getY() && pos.getX() <= frame.endPos.get().getY();
		boolean z = pos.getZ() >= frame.startPos.get().getZ() && pos.getX() <= frame.endPos.get().getZ();
		
		return x&&y&&z;
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
