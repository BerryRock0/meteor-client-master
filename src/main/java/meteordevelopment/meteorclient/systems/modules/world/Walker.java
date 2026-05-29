
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

import net.minecraft.client.Minecraft;


public class Walker extends Module
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

	//Control
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

	public int character;

	public Walker()
	{
        super(Categories.World, "walker", "Allows you to programming your movement.");
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
			walk(movement.get());
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
            case '0': movement.set(!movement.get());
            case '1': forward.set(!forward.get());    
            case '2': back.set(!back.get());
            case '3': left.set(!left.get());
            case '4': right.set(!right.get());
            case '5': jump.set(!jump.get());
            case '6': sneak.set(!sneak.get());
            case '7': use.set(!use.get());
            case '8': attack.set(!attack.get());
            default: break;
        }
    }

	public void walk(boolean m)
	{
        if (m)
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
