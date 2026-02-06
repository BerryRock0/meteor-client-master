package meteordevelopment.meteorclient.systems.modules.world;

import meteordevelopment.orbit.EventHandler;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.gui.widgets.containers.WHorizontalList;
import meteordevelopment.meteorclient.gui.widgets.containers.WVerticalList;
import meteordevelopment.meteorclient.gui.widgets.pressable.WButton;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.utils.world.BlockUtils;

import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;

public class MinerPlacer extends Module
{
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final SettingGroup sgControl = settings.createGroup("Control");
    private final SettingGroup sgExecution = settings.createGroup("Execution");


    private final Setting<BlockPos> zero = sgGeneral.add(new BlockPosSetting.Builder()
        .name("zero-pos")
        .description("Mining block position")
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

    private final Setting<Boolean> mining = sgExecution.add(new BoolSetting.Builder()
        .name("Mining")
        .description("Break blocks in area")
        .defaultValue(false)
        .build()
    );
    private final Setting<Boolean> using = sgExecution.add(new BoolSetting.Builder()
        .name("Using")
        .description("Intreact blocks in area")
        .defaultValue(false)
        .build()
    );
    
    public int x;
    public int y;
    public int z;

    public BlockPos pos = new BlockPos(x,y,z);
    
    public MinerPlacer()
    {
        super(Categories.World, "MinerPlacer", "Break or Place in specific coordinate.");
    }

    @EventHandler
    private void onTickPre(TickEvent.Pre event)
    {
        if (pre.get())
            main();
    }
        
    @EventHandler
    private void onTickPre(TickEvent.Post event) 
    {
        if (post.get())
            main();
    }

    public void main()
    {   
        if(mining.get())
            BlockUtils.breakBlock(pos, false);
        if(using.get())
            BlockUtils.interact(new BlockHitResult(pos.toCenterPos(), BlockUtils.getDirection(pos), pos, true), Hand.MAIN_HAND, false);   
    }

    public WWidget getWidget(GuiTheme theme)
    {
        
        WVerticalList list = theme.verticalList();
        WHorizontalList b = list.add(theme.horizontalList()).expandX().widget();

        WButton ix = theme.button("x++"); ix.action = () -> x++;
        WButton iy = theme.button("y++"); iy.action = () -> y++;
        WButton iz = theme.button("z++"); iz.action = () -> z++;
        WButton dx = theme.button("x--"); dx.action = () -> x--;
        WButton dy = theme.button("y--"); dy.action = () -> y--;
        WButton dz = theme.button("z--"); dz.action = () -> z--;
        WButton set = theme.button("Set"); set.action = () -> {x=zero.get().getX(); y=zero.get().getY(); z=zero.get().getZ();};
        
        return list;
    }
}
