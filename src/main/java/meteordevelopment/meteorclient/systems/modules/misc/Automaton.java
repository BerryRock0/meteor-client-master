package meteordevelopment.meteorclient.systems.modules.misc;

import java.util.List;

import meteordevelopment.orbit.EventHandler;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.gui.widgets.pressable.WButton;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.events.world.TickEvent;

public class Automaton extends Module
{
    private final SettingGroup sgControl = settings.createGroup("Control");
    private final SettingGroup sgScripts = settings.createGroup("Scripts");

   private final Setting<Boolean> pre = sgControl.add(new BoolSetting.Builder()
        .name("Pre")
        .description("Load script before tick.")
        .defaultValue(false)
        .build()
    );
    private final Setting<Boolean> post = sgControl.add(new BoolSetting.Builder()
        .name("Post")
        .description("Load script after tick.")
        .defaultValue(false)
        .build()
    );
    
    private final Setting<List<String>> commands = sgScripts.add(new StringListSetting
        .Builder()
        .name("commands")
        .description("setting commands")
        .build()
    );

    public int cmdindex;
    public int delay;
    
    public Automaton()
    {
        super(Categories.Misc, "automaton", "Doing actions with instructions. Format: <action> <value argument> <execution ticks>. Actions: yaw, pitch, forward, back, left, right, jump, attack, use, to.");
    }

     @Override
    public void onDeactivate()
    {
        cmdindex = 0;
        delay = 0;
    }

    @EventHandler
    private void onPreTick(TickEvent.Pre event)
    {
        if (pre.get())
            main();
    }

    
    @EventHandler
    private void onPostTick(TickEvent.Post event)
    {
        if (post.get())
            main();
    }

    public void main()
    {
        try
        {
            String cmd = commands.get().get(cmdindex);
            String[] parts = cmd.trim().split("\\s+");
            String command = parts[0];
            String arg = parts[1];

            if (delay < Integer.parseInt(parts[2]))
            {
                delay++;
                if(isActive())
                    execute(command, arg);
                return;
            }
            delay = 0;
            cmdindex++;  
        }
        catch(Exception e)
        {} 
    }

    private void execute(String command, String arg)
    {
        if (mc.player == null)
            return;
        
        switch (command.toLowerCase())
        {
            case "yaw": mc.player.setYaw(Float.parseFloat(arg)); break;
            case "pitch": mc.player.setPitch(Float.parseFloat(arg)); break;
            case "forward": mc.options.forwardKey.setPressed(Boolean.parseBoolean(arg)); break;
            case "back": mc.options.backKey.setPressed(Boolean.parseBoolean(arg)); break;
            case "left": mc.options.leftKey.setPressed(Boolean.parseBoolean(arg)); break;
            case "right": mc.options.rightKey.setPressed(Boolean.parseBoolean(arg)); break;
            case "jump": mc.options.jumpKey.setPressed(Boolean.parseBoolean(arg)); break;
            case "attack": mc.options.attackKey.setPressed(Boolean.parseBoolean(arg)); break;
            case "use": mc.options.useKey.setPressed(Boolean.parseBoolean(arg)); break;
            case "to": cmdindex = Integer.parseInt(arg); break;
            default: break;
        }
    }

    public WWidget getWidget(GuiTheme theme)
    {
        return theme.button("Reset").action() -> cmdindex = 0;
    }
}	
