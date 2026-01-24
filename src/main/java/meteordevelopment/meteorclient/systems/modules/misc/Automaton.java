package meteordevelopment.meteorclient.systems.modules.misc;

import java.util.List;

import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;

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

    public void main()
    {
        for(String cmd : commands.get())
          if(isActive())
            parseAndProceed(cmd);
    }

    public void parseAndProceed(String input)
    {
        String[] parts = input.trim().split("\\s+");

        switch (parts[0].toLowerCase())
        {
            case "set_yaw": mc.player.setYaw(Float.parseFloat(parts[1].toLowerCase()));
            case "set_pitch": mc.player.setPitch(Float.parseFloat(parts[1].toLowerCase()));
            case "press_forward": mc.options.forwardKey.setPressed(Boolean.parseBoolean(parts[1].toLowerCase()));
            case "press_back": mc.options.backKey.setPressed(Boolean.parseBoolean(parts[1].toLowerCase()));
            case "press_left": mc.options.leftKey.setPressed(Boolean.parseBoolean(parts[1].toLowerCase()));
            case "press_right": mc.options.rightKey.setPressed(Boolean.parseBoolean(parts[1].toLowerCase()));
            case "press_jump": mc.options.rightKey.setPressed(Boolean.parseBoolean(parts[1].toLowerCase()));
        }
    }
}	
