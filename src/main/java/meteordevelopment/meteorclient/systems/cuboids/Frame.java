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
    public final Setting<Mode> collisionMode = sgBlock.add(new EnumSetting.Builder<Mode>()
        .name("CollisionMode")
        .description("Direction of stepping.")
        .defaultValue(Mode.None)
        .build()
    );

    public final Setting<Mode> visualMode = sgPlayer.add(new EnumSetting.Builder<Mode>()
        .name("VisualMode")
        .description("Direction of stepping.")
        .defaultValue(Mode.None)
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

    public enum Mode
    {
        None,
        Empty,
        Full
    }
}
