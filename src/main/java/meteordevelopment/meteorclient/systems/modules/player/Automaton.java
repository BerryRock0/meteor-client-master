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
   public final Setting<Boolean> pre = sgControl.add(new BoolSetting.Builder()
        .name("Pre")
        .description("Load script before tick.")
        .defaultValue(false)
        .build()
    );
    public final Setting<Boolean> post = sgControl.add(new BoolSetting.Builder()
        .name("Post")
        .description("Load script after tick.")
        .defaultValue(false)
        .build()
    );
    public final Setting<Boolean> debug = sgExecute.add(new BoolSetting.Builder()
        .name("debug")
        .description("Print errors on logs.")
        .defaultValue(false)
        .build()
    );

    //Script
    public final Setting<StepDirections> stepDirections = sgScript.add(new EnumSetting.Builder<StepDirections>()
        .name("Step Direction")
        .description("Direction of stepping.")
        .defaultValue(StepDirections.None)
        .build()
    );
    public final Setting<String> script = sgScript.add(new StringSetting.Builder()
        .name("script")
        .description("Action commands. _0123456789")
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

    //Control
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
    public final Setting<Double> yaw = sgGeneral.add(new DoubleSetting.Builder()
        .name("yaw")
        .description("Entity yaw value.")
        .defaultValue(0)
        .sliderMin(-180)
        .sliderMax(180)
        .build()
    );

    public final Setting<Double> pitch = sgGeneral.add(new DoubleSetting.Builder()
        .name("pitch")
        .description("Entity pitch value.")
        .defaultValue(0)
        .sliderMin(-180)
        .sliderMax(180)
        .build()
    );

    public int x,y,s,c;
    public Automaton()
    {
        super(Categories.Player, "automaton", "Doing actions with instructions. ");
    }

    @EventHandler
    private void onPreTick(TickEvent.Pre event)
    {
        if (pre.get())
            engine();
    }

    
    @EventHandler
    private void onPostTick(TickEvent.Post event)
    {
        if (post.get())
            engine();
    }


    public void engine()
    {
        try
        {
            moves();
            execute(script.get().charAt(unit.c));
            step(stepper.get())
        }
        catch(Exception e)
        {}
      
        if (increment.get()) cmdindex++;
        if (decrement.get()) cmdindex--;
      
    }

    public void moves()
    {
      if(pitch.get()) mc.player.setXRot((float));
      if(yaw.get()) mc.player.setYRot((float));
      mc.options.forwardKey.setDown(forward.get());
      mc.options.backKey.setDown(back.get());
      mc.options.leftKey.setDown(left.get());
      mc.options.rightKey.setDown(right.get());
      mc.options.jumpKey.setDown(jump.get());
      mc.options.sneakKey.setDown(sneak.get());
      mc.options.attackKey.setDown(attack.get());
      mc.options.useKey.setDown(use.get());
    }

    public void execute(char c, boolean t)
    {   
      if(t)
        switch (c)
        {
            case '_': return;
            case '0': attack.set(!attack.get()); break;
            case '1': use.set(!use.get()); break;
            case '2': forward.set(!forward.get()); break;
            case '3': back.set(!back.get()); break;
            case '4': left.set(!left.get()); break;
            case '5': right.set(!right.get()); break;
            case '6': sneak.set(!sneak.get()); break;
            case '7': jump.set(!jump.get()); break;
            case '8': pitch.set(!pitch.get()); break;
            case '9': yaw.set(!yaw.get()); break;
            case '+': if(pitch.get()) ; if() ;
            case '-': if(pitch.get()) ; if() ;
            default: break;
        }
    }


  public void step()
  {
        switch (unit.stepDirections.get())
        {
            case None -> {}
            case Increment -> {unit.c++;}
            case Decrement -> {unit.c--;}
        }
  }

  public enum StepDirections
  {
      None,
      Increment,
      Decrement
  }
}
