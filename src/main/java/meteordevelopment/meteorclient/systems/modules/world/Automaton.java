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


    public final Setting<Boolean> forward = sgControl.add(new BoolSetting.Builder()
        .name("Forward")
        .description("")
        .defaultValue(false)
        .build()
    );
    public final Setting<Boolean> back = sgControl.add(new BoolSetting.Builder()
        .name("Back")
        .description("")
        .defaultValue(false)
        .build()
    );
    public final Setting<Boolean> left = sgControl.add(new BoolSetting.Builder()
        .name("Left")
        .description("")
        .defaultValue(false)
        .build()
    );
    public final Setting<Boolean> right = sgControl.add(new BoolSetting.Builder()
        .name("Right")
        .description("")
        .defaultValue(false)
        .build()
    );
    public final Setting<Boolean> jump = sgControl.add(new BoolSetting.Builder()
        .name("Jump")
        .description("")
        .defaultValue(false)
        .build()
    );
    public final Setting<Boolean> sneak = sgControl.add(new BoolSetting.Builder()
        .name("Sneak")
        .description("")
        .defaultValue(false)
        .build()
    );
    public final Setting<Boolean> attack = sgControl.add(new BoolSetting.Builder()
        .name("Attack")
        .description("")
        .defaultValue(false)
        .build()
    );
    public final Setting<Boolean> use = sgControl.add(new BoolSetting.Builder()
        .name("Use")
        .description("")
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

    private final Setting<String> commands = sgScripts.add(new StringSetting.Builder()
        .name("command")
        .description("Setting commands.")
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
            cmd = ;

            execute(commands.get().charAt(c));
            step(false, false, false);
        }
        catch(Exception e)
        {} 
    }

    public void step(boolean i, boolean d, boolean s)
    {
        if(s)
        {
            if (i) c++;
            if (d) c--;
        }
    }


    private void execute(char c)
    {
        if (mc.player == null)
            return;
        
        switch (c)
        {

            case '_': return;
            case ';': cmdindex  = break;
            case 'F': mc.options.forwardKey.setPressed() break;
            case 'B': mc.options.backKey.setPressed(); break;
            case 'L': mc.options.leftKey.setPressed(); break;
            case 'R': mc.options.rightKey.setPressed(); break;
            case 'J': mc.options.jumpKey.setPressed(); break;
            case 'S': mc.options.sneakKey.setPressed(); break;
            case 'U': mc.options.useKey.setPressed(); break;
            case 'A': mc.options.attackKey.setPressed(); break;
            case 'Y': mc.player.setYRot();
            case 'X': mc.player.setYRot();
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
