/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.systems.modules.player;

import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;

import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.widgets.containers.WTable;
import meteordevelopment.meteorclient.gui.widgets.pressable.WButton;
import meteordevelopment.meteorclient.gui.widgets.WLabel;
import meteordevelopment.meteorclient.gui.widgets.WWidget;


public class Automaton extends Module
{
    private final SettingGroup sgExecute = settings.createGroup("Execute");
    private final SettingGroup sgControl = settings.createGroup("Control");
    private final SettingGroup sgScript = settings.createGroup("Script");

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
        .description("Action commands. _!?+-XYZSCxyzsc&@%*^:;")
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

    public final Setting<Boolean> move = sgControl.add(new BoolSetting.Builder()
        .name("move")
        .description("Enables player move.")
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

  public int c;

  public Automaton()
  {
    super(Categories.Player, "automaton", "Allows scripted inaccurate moving without presence at the keyboard.")
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

  	@Override
    public WWidget getWidget(GuiTheme theme)
    {
        if (!Utils.canUpdate())
            return theme.label("You need to be in a world.");

        WTable table = theme.table();
        initTable(theme, table);
        return table;
    }
  
  private void engine()
  {
          try
          {
              move(forward.get(), back.get(), left.get(), right.get(), jump.get(), sneak.get(), attack.get(), use.get(), move.get());
              translate(script.get().charAt(c), handler.get());
              step(stepper.get());
          }
          catch (Exception e)
          {if(debug.get()) e.printStackTrace();}
  }  

  
  private void translate(char ch)
  {
    switch (ch)
    {
        case '0': move.set(!move.get()); break;
        case '1': attack.set(!attack.get()); break;
        case '2': use.set(!use.get()); break;
        case '3': forward.set(!forward.get()); break;
        case '4': back.set(!back.get()); break;
        case '5': left.set(!left.get()); break;
        case '6': right.set(!right.get()); break;
        case '7': sneak.set(!sneak.get()); break;
        case '8': jump.set(!jump.get()); break;
    }
  }

  private void step(MinerPlacer unit, boolean s)
  {
      if(s)
      switch (unit.stepDirections.get())
      {
          case None -> {}
          case Increment -> {c++;}
          case Decrement -> {c--;}
      }
  }

    private void move(boolean a, boolean b, boolean c, boolean d, boolean e, boolean f, boolean g, boolean h, boolean t)
    {
        if(t)
        {
            mc.options.keyUp.setDown(a);
            mc.options.keyDown.setDown(b);
            mc.options.keyLeft.setDown(c);
            mc.options.keyRight.setDown(d);
            mc.options.keyJump.setDown(e);
            mc.options.keyShift.setDown(f);
            mc.options.keyAttack.setDown(g);
            mc.options.keyUse.setDown(h);
        }
    }

    public void setColumn(int column)
    {
        c = column;
    }

    private void initTable(GuiTheme theme, WTable table)
    {
        table.clear();

        table.add(theme.horizontalSeparator()).expandX();
        table.row();

        WButton reset = table.add(theme.button("Reset")).expandX().widget(); reset.action = () ->  {setColumn(column.get());};
    }

    public enum StepDirections
    {
        None,
        Increment,
        Decrement
    }
}
