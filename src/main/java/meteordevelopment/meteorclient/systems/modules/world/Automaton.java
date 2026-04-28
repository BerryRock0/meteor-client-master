package meteordevelopment.meteorclient.systems.modules.world;

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
    private final SettingGroup sgScript = settings.createGroup("Script");

   private final Setting<Boolean> pre = sgControl.add(new BoolSetting.Builder()
        .name("pre")
        .description("Load script before tick.")
        .defaultValue(false)
        .build()
    );
    private final Setting<Boolean> post = sgControl.add(new BoolSetting.Builder()
        .name("post")
        .description("Load script after tick.")
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

    //Script
    private final Setting<String> script = sgScripts.add(new StringSetting.Builder()
        .name("script")
        .description("Setting actions list.")
        .build()
    );

    public final Setting<Integer> column = sgScript.add(new IntSetting.Builder()
        .name("column")
        .description("Reset column value.")
        .defaultValue(0)
        .build()
    );
    
    private final Setting<Boolean> handler = sgScript.add(new BoolSetting.Builder()
        .name("handler")
        .description("String to char, char to command.")
        .defaultValue(false)
        .build()
    );
    private final Setting<Boolean> stepper = sgScript.add(new BoolSetting.Builder()
        .name("stepper")
        .description("Steps on line.")
        .defaultValue(false)
        .build()
    );
    private final Setting<Boolean> action = sgScript.add(new BoolSetting.Builder()
        .name("action")
        .description("Doing action list.")
        .defaultValue(false)
        .build()
    );

    public int c;
    
    public Automaton()
    {
        super(Categories.Misc, "automaton", "Doing actions with instructions.");
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
            actions(action.get());
            execute(commands.get().charAt(c), handler.get());
            step(c!=script.get().length(), c==script.get().length(), stepper.get());
        }
        catch(Exception e)
        {} 
    }

    public void actions()
    {
        mc.options.forwardKey.setPressed(forward.get()); break;
        mc.options.backKey.setPressed(back.get()); break;
        mc.options.leftKey.setPressed(left.get()); break;
        mc.options.rightKey.setPressed(right.get()); break;
        mc.options.jumpKey.setPressed(jump.get()); break;
        mc.options.sneakKey.setPressed(sneak.get()); break;
        mc.options.useKey.setPressed(use.get()); break;
        mc.options.attackKey.setPressed(attack.get()); break;   
    }
    
    public void step(boolean i, boolean d, boolean s)
    {
        if(s)
        {
            if (i) c++;
            if (d) c--;
        }
    }

    private void execute( char c, boolean b)
    {   
        if (b)
        switch (c)
        {
            case '0': return;
            case '1': c = 0; break;          
            case '2': forward.set(!forward.get()); break;
            case '3': back.set(!back.get()); break;
            case '4': left.set(!left.get()); break;
            case '5': right.set(!right.get()); break;
            case '6': jump.set(!jump.get()); break;
            case '7': sneak.set(!sneak.get()); break;
            case '8': use.set(!use.get()); break;
            case '9': attack.set(!attack.get()); break;
            default: break;
        }
    }

    public WWidget getWidget(GuiTheme theme)
    {
        WButton reset = theme.button("Reset");
        reset.action = () -> c = 0;
        return reset;
    }
}	
