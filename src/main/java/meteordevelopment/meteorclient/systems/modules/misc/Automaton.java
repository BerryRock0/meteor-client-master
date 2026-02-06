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
    private final SettingGroup sgExecution = settings.createGroup("Execution");
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

       private final Setting<Boolean> increment = sgExecution.add(new BoolSetting.Builder()
        .name("Increment")
        .description("Execute script from beginning to end.")
        .defaultValue(false)
        .build()
    );
    private final Setting<Boolean> decrement = sgExecution.add(new BoolSetting.Builder()
        .name("Decrement")
        .description("Execute script from end to beginning.")
        .defaultValue(false)
        .build()
    );

    private final Setting<List<String>> commands = sgScripts.add(new StringListSetting.Builder()
        .name("commands")
        .description("setting commands")
        .build()
    );

    public int cmdindex;
    public long delay;
    public String cmd;
    public String[] parts;
    public String command;
    public String arg;
    
    public Automaton()
    {
        super(Categories.Misc, "automaton", "Doing actions with instructions. Format: <action> <value argument> <execution ticks>. Actions: yaw, pitch, forward, back, left, right, jump, attack, use, to.");
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
            cmd = commands.get().get(cmdindex);
            parts = cmd.trim().split("\\s+");
            command = parts[0];
            arg = parts[1];

            if (delay < Long.parseLong(parts[2]))
            {
                delay++;
                if(isActive())
                    execute(command, arg);
                return;
            }
            delay = 0;
            
            if (increment.get()) cmdindex++;
            if (decrement.get()) cmdindex--;
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
            case "forward": mc.options.forwardKey.setPressed(Boolean.parseBoolean(arg)); break;
            case "back": mc.options.backKey.setPressed(Boolean.parseBoolean(arg)); break;
            case "left": mc.options.leftKey.setPressed(Boolean.parseBoolean(arg)); break;
            case "right": mc.options.rightKey.setPressed(Boolean.parseBoolean(arg)); break;
            case "jump": mc.options.jumpKey.setPressed(Boolean.parseBoolean(arg)); break;
            case "sneak": mc.options.sneakKey.setPressed(Boolean.parseBoolean(arg)); break;
            case "attack": mc.options.attackKey.setPressed(Boolean.parseBoolean(arg)); break;
            case "use": mc.options.useKey.setPressed(Boolean.parseBoolean(arg)); break;
            case "yaw": mc.player.setYaw(Float.parseFloat(arg)); break;
            case "pitch": mc.player.setPitch(Float.parseFloat(arg)); break;    
            case "to": cmdindex = Integer.parseInt(arg); break;
            default: break;
        }
    }

    public WWidget resetWidget(GuiTheme theme)
    {
        WButton reset = theme.button("Reset");
        reset.action = () -> cmdindex = 0;
        return reset;
    }
}	
