package meteordevelopment.meteorclient.systems.cuboids;

import java.util.List;
import java.util.UUID;
import java.util.Objects;

import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.utils.player.Rotations;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.meteorclient.utils.misc.ISerializable;
import meteordevelopment.meteorclient.renderer.ShapeMode;

import net.minecraft.client.Minecraft;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.core.BlockPos;

// TODO: Add outline and more modes
public class Frame implements ISerializable<Frame>
{
    public final Settings settings = new Settings();

    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final SettingGroup sgPlayer = settings.createGroup("Player");
    private final SettingGroup sgBlock = settings.createGroup("Block");
    private final SettingGroup sgRender = settings.createGroup("Render");

    // General
    public final Setting<BlockPos> startPos = sgGeneral.add(new BlockPosSetting.Builder()
        .name("start-pos")
        .description("Start corner of the cuboid")
        .build()
    );

    public final Setting<BlockPos> endPos = sgGeneral.add(new BlockPosSetting.Builder()
        .name("end-pos")
        .description("End corner of the cuboid")
        .build()
    );
    
    
    // Render
    
    public final Setting<Boolean> render = sgRender.add(new BoolSetting.Builder()
        .name("render")
        .description("Renders a area overlay.")
        .defaultValue(false)
        .build()
    );

    public final Setting<ShapeMode> shapeMode = sgRender.add(new EnumSetting.Builder<ShapeMode>()
        .name("shape-mode")
        .description("How the shapes are rendered.")
        .defaultValue(ShapeMode.Both)
        .build()
    );

    public final Setting<SettingColor> sideColor = sgRender.add(new ColorSetting.Builder()
        .name("side-color")
        .description("The color of the sides of the blocks being rendered.")
        .defaultValue(new SettingColor(0, 0, 0, 0))
        .build()
    );

    public final Setting<SettingColor> lineColor = sgRender.add(new ColorSetting.Builder()
        .name("line-color")
        .description("The color of the lines of the blocks being rendered.")
        .defaultValue(new SettingColor(0, 0, 0, 0))
        .build()
    );

    // Collisions
    public final Setting<Boolean> fullBlock = sgBlock.add(new BoolSetting.Builder()
        .name("full-block")
        .description("What blocks should be added collision box.")
        .defaultValue(false)
        .build()
    );
    
    public final Setting<Boolean> fullBlockCase = sgBlock.add(new BoolSetting.Builder()
        .name("fullblock-case")
        .description("Switches black/white case.")
        .defaultValue(false)
        .build()
    );
    
    public final Setting<Boolean> fullBlockFinal = sgBlock.add(new BoolSetting.Builder()
        .name("fullblock-final")
        .description("Switches black/white final.")
        .defaultValue(false)
        .build()
    );
    
    public final Setting<Boolean> emptyBlock = sgBlock.add(new BoolSetting.Builder()
        .name("emptyblock")
        .description("What blocks should be emptied.")
        .defaultValue(false)
        .build()
    );

    public final Setting<Boolean> emptyBlockCase = sgBlock.add(new BoolSetting.Builder()
        .name("emptyblock-case")
        .description("Switches black/white case.")
        .defaultValue(false)
        .build()
    );

    public final Setting<Boolean> emptyBlockFinal = sgBlock.add(new BoolSetting.Builder()
        .name("emptyblock-final")
        .description("Switches black/white final.")
        .defaultValue(false)
        .build()
    );    

    public final Setting<Boolean> fullPlayer = sgPlayer.add(new BoolSetting.Builder()
        .name("fullplayer-block")
        .description("What blocks should be added collision box.")
        .defaultValue(false)
        .build()
    );
    
    public final Setting<Boolean> fullPlayerCase = sgPlayer.add(new BoolSetting.Builder()
        .name("fullplayer-case")
        .description("Switches black/white case.")
        .defaultValue(false)
        .build()
    );
    
    public final Setting<Boolean> fullPlayerFinal = sgPlayer.add(new BoolSetting.Builder()
        .name("fullplayer-final")
        .description("Switches black/white final.")
        .defaultValue(false)
        .build()
    );
    
    public final Setting<Boolean> emptyPlayer = sgPlayer.add(new BoolSetting.Builder()
        .name("emptyplayer")
        .description("What blocks should be emptied.")
        .defaultValue(false)
        .build()
    );

    public final Setting<Boolean> emptyPlayerCase = sgPlayer.add(new BoolSetting.Builder()
        .name("emptyplayer-case")
        .description("Switches black/white case.")
        .defaultValue(false)
        .build()
    );

    public final Setting<Boolean> emptyPlayerFinal = sgPlayer.add(new BoolSetting.Builder()
        .name("emptyplayer-final")
        .description("Switches black/white final.")
        .defaultValue(false)
        .build()
    );
    
    public final UUID uuid;
    
    
	private Frame()
	{
		uuid = UUID.randomUUID();
	}
	
    public Frame(Tag tag)
    {
		CompoundTag nbt = (CompoundTag) tag;
        uuid = nbt.read("uuid", UUIDUtil.CODEC).orElse(UUID.randomUUID());
        fromTag(nbt);
	}
	
	public static class Builder
	{
        public Frame build()
        {
            return new Frame();
        }
    }

    @Override
    public CompoundTag toTag()
    {
        CompoundTag tag = new CompoundTag();

        tag.store("uuid", UUIDUtil.CODEC, uuid);
        tag.put("settings", settings.toTag());

        return tag;
    }
    
     @Override
    public Frame fromTag(CompoundTag tag)
    {
        if (tag.contains("settings"))
            settings.fromTag(tag.getCompoundOrEmpty("settings"));

        return this;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
			return true;

        if (o == null || getClass() != o.getClass())
			return false;
        
        Frame that = (Frame) o;

        return Objects.equals(uuid, that.uuid);
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode(uuid);
    }
}
