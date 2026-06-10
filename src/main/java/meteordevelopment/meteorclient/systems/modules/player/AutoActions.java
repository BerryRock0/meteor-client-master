package meteordevelopment.meteorclient.systems.modules.player;

import meteordevelopment.orbit.EventHandler;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;

public class AutoActions extends Module
{
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final SettingGroup sgExecute = settings.createGroup("Execute");

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

    public final Setting<Boolean> forward = sgGeneral.add(new BoolSetting.Builder()
        .name("forward")
        .description("")
        .defaultValue(false)
        .build()
    );
    public final Setting<Boolean> back = sgGeneral.add(new BoolSetting.Builder()
        .name("back")
        .description("")
        .defaultValue(false)
        .build()
    );
    public final Setting<Boolean> left = sgGeneral.add(new BoolSetting.Builder()
        .name("left")
        .description("")
        .defaultValue(false)
        .build()
    );
    public final Setting<Boolean> right = sgGeneral.add(new BoolSetting.Builder()
        .name("right")
        .description("")
        .defaultValue(false)
        .build()
    );
    public final Setting<Boolean> jump = sgGeneral.add(new BoolSetting.Builder()
        .name("jump")
        .description("")
        .defaultValue(false)
        .build()
    );
    public final Setting<Boolean> sneak = sgGeneral.add(new BoolSetting.Builder()
        .name("sneak")
        .description("")
        .defaultValue(false)
        .build()
    );
    public final Setting<Boolean> attack = sgGeneral.add(new BoolSetting.Builder()
        .name("attack")
        .description("")
        .defaultValue(false)
        .build()
    );
    public final Setting<Boolean> use = sgGeneral.add(new BoolSetting.Builder()
        .name("use")
        .description("")
        .defaultValue(false)
        .build()
    );

	  public AutoActions()
	  {
        super(Categories.Player, "auto-actions", "Doing player related actions.");
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
