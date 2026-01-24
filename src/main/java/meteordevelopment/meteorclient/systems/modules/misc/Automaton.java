package meteordevelopment.meteorclient.systems.modules.misc;

import java.util.List;

import meteordevelopment.orbit.EventHandler;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.events.world.TickEvent;


public class Automaton extends Module
{
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<List<String>> commands = sgGeneral.add(new StringListSetting.Builder()
        .name("commands")
        .description("setting commands")
        .build()
    );

    public Automaton()
    {
        super(Categories.Misc, "automaton", "Doing actions with instructions.");
    }

    @EventHandler
    private void onTick(TickEvent.Post event)
    {
        for(String cmd : commands.get())
        {
            String[] parts = cmd.trim().split("\\s+");
            String command = parts[0].toLowerCase();
            String arg = parts[1];

            if(isActive())
            {
                    if (mc.player == null)
                        return;
        
                    switch (command)
                    {
                        case "yaw": mc.player.setYaw(Float.parseFloat(arg)); break;
                        case "pitch": mc.player.setPitch(Float.parseFloat(arg)); break;
                        case "forward": mc.options.forwardKey.setPressed(Boolean.parseBoolean(arg)); break;
                        case "back": mc.options.backKey.setPressed(Boolean.parseBoolean(arg)); break;
                        case "left": mc.options.leftKey.setPressed(Boolean.parseBoolean(arg)); break;
                        case "right": mc.options.rightKey.setPressed(Boolean.parseBoolean(arg)); break;
                        case "jump": mc.options.jumpKey.setPressed(Boolean.parseBoolean(arg)); break;
                        default: break;
                    }
            }
        }
    }
}	
