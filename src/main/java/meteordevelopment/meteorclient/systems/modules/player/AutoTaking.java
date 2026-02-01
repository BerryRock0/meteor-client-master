
package meteordevelopment.meteorclient.systems.modules.player;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.slot.SlotActionType;

import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;

public class AutoTaking extends Module
{
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final SettingGroup sgControl = settings.createGroup("Control");

    private final Setting<Integer> taking_slot = sgGeneral.add(new IntSetting.Builder()
        .name("taking-slot")
        .description("The slot and which is being taken.")
        .range(0, 2147483647)                                                     
        .build()
    );

    private final Setting<Boolean> fullstack = sgControl.add(new BoolSetting.Builder()
        .name("fullstack")
        .description("Take full stack.")
        .defaultValue(false)                                                   
        .build()
    );

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

    ClientPlayerEntity ply;
	ClientPlayerInteractionManager im;
	PlayerInventory inv;
    int cS;
    
    public AutoTaking()
    {
        super(Categories.Player, "autotaking", "Taking items from slot.");
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
        if(mc.player == null || mc.interactionManager == null)
			return;
        
		ply = mc.player;
		im = mc.interactionManager;
		inv = mc.player.getInventory();
        tS = taking_slot.get();

        switch(mode.getSelected())
		{
			case MOVE -> im.clickSlot(ply.currentScreenHandler.syncId, tS, fullstack.get() ? 1 : 0, SlotActionType.QUICK_MOVE, ply);
			break;
			
			case DROP -> im.clickSlot(ply.currentScreenHandler.syncId, tS, fullstack.get() ? 1 : 0, SlotActionType.THROW, ply);
			break;
		}
    }
}
