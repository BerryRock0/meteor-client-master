package meteordevelopment.meteorclient.systems.modules.world;

import meteordevelopment.orbit.EventHandler;
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
    private final SettingGroup sgMiner = settings.createGroup("Miner");
    private final SettingGroup sgPlacer = settings.createGroup("Placer");

    private final SettingGroup sgControl = settings.createGroup("Control");
    private final SettingGroup sgExecution = settings.createGroup("Execution");


    private final Setting<BlockPos> mine = sgMiner.add(new BlockPosSetting.Builder()
        .name("mine-pos")
        .description("Mining block position")
        .build()
    );

    private final Setting<BlockPos> interact = sgPlacer.add(new BlockPosSetting.Builder()
        .name("interact-pos")
        .description("Interact block position")
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
        BlockPos minepos = new BlockPos(mine.get().getX(), mine.get().getY(), mine.get().getZ());
        BlockPos interactpos = new BlockPos(interact.get().getX(),interact.get().getY(), interact.get().getZ());
        
        if(mining.get())
            BlockUtils.breakBlock(minepos, false);
        if(using.get())
            BlockUtils.interact(new BlockHitResult(interactpos.toCenterPos(), BlockUtils.getDirection(interactpos), interactpos, true), Hand.MAIN_HAND, false);   
    }

}
