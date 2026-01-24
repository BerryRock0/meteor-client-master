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
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<List<String>> commands = sgGeneral.add(new StringListSetting
        .Builder()
        .name("commands")
        .description("setting commands")
        .build()
    );
    
    public int cmdindex;
    public int delay;
    public int repeats;
    
    public Automaton()
    {
        super(Categories.Misc, "automaton", "Doing actions with instructions. Format: <action> <value argument> <execution ticks>. Actions: yaw, pitch, forward, back, left, right, jump, to.");
    }

    @EventHandler
    private void onTick(TickEvent.Post event)
    {
        String cmd = commands.get().get(cmdindex);
        String[] parts = cmd.trim().split("\\s+");
        String command = parts[0];
        String arg = parts[1];

        if (commands.get().isEmpty() || cmdindex >= commands.get().size() - 1)
        {
            cmdindex = 0;
            return;
        }

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
            case "use": mc.options.attackKey.setPressed(Boolean.parseBoolean(arg)); break;
            case "to": cmdindex = Integer.parseInt(arg); break;
            case "": break;
            default: break;
        }
    }

    public WWidget getWidget(GuiTheme theme)
    {
        WButton reset = theme.button("Reset");
        reset.action = () -> cmdindex = 0;

        return reset;
    }
}	
