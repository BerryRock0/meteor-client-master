package meteordevelopment.meteorclient.systems.modules.world;

import java.util.List;
import java.util.ArrayList;

import meteordevelopment.orbit.EventHandler;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.gui.widgets.*;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.player.InvUtils;

import net.minecraft.client.Minecraft;


public class Slotter extends Module
{	
	private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final SettingGroup sgExecute = settings.createGroup("Execute");
    private final SettingGroup sgControl = settings.createGroup("Control");
    private final SettingGroup sgScript = settings.createGroup("Script");

    //Script
    public final Setting<String> script = sgScript.add(new StringSetting.Builder()
        .name("script")
        .description("Action commands. +-=XYZxyz_")
        .build()
    );
    
    public final Setting<Integer> column = sgScript.add(new IntSetting.Builder()
        .name("column")
        .description("Reset column value.")
        .defaultValue(0)
        .build()
    );
    public final Setting<Boolean> handler = sgScript.add(new BoolSetting.Builder()
        .name("handler")
        .description("String to char, char to command")
        .defaultValue(false)
        .build()
    );
    public final Setting<Boolean> stepper = sgScript.add(new BoolSetting.Builder()
        .name("stepper")
        .description("Steps on line.")
        .defaultValue(false)
        .build()
    );

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

	public int character;
	public int slot,cursor;

	public Slotter()
	{
        super(Categories.World, "slotter", "Allows you to programming slot movements.");
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

    private void engine()
    {
		try
        {
            translate(script.get().charAt(character), handler.get());
            step(character!=script.get().length(), character==script.get().length(), stepper.get());
        }
        catch (Exception e)
        {}
    }

	private void translate(char ch, boolean t)
    {
        if(t)
        switch (ch)
        {
            case '_': return;
            case 'S': slot++; break;
            case 's': slot--; break;
            case 'C': cursor++; break;
            case 'c': cursor--; break;  
            case '%': InvUtils.move().from(cursor).to(slot); break;
            case '@': InvUtils.swap(slot, false); break;
            default: break;
        }
    }

	private void step(boolean i, boolean d, boolean s)
    {
        if(s)
        {
            if (i) character++;
            if (d) character--;
        }
    }
}
