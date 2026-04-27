package meteordevelopment.meteorclient.systems.minerplacers;

import java.util.List;
import java.util.UUID;
import java.util.Objects;

import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.utils.player.Rotations;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import meteordevelopment.meteorclient.systems.modules.world.WorkersModule;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.meteorclient.utils.misc.ISerializable;
import meteordevelopment.meteorclient.renderer.ShapeMode;

import net.minecraft.client.Minecraft;
import net.minecraft.util.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.hit.BlockHitResult;

public class MinerPlacer implements ISerializable<MinerPlacer>
{
	public final Settings settings = new Settings();
	
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final SettingGroup sgSettings = settings.createGroup("Settings");
    private final SettingGroup sgScript = settings.createGroup("Script");
    private final SettingGroup sgRender = settings.createGroup("Render");

    public Setting<String> name = sgGeneral.add(new StringSetting.Builder()
        .name("name")
        .description("The name of the worker.")                                       
        .build()
    );
    
    public final Setting<BlockPos> zero = sgGeneral.add(new BlockPosSetting.Builder()
        .name("zero-pos")
        .description("Mining block position")
        .build()
    );
    
    public final Setting<Boolean> breakBlock = sgGeneral.add(new BoolSetting.Builder()
        .name("breaking-block")
        .description("Break blocks in area.")
        .defaultValue(false)
        .build()
    );
    public final Setting<Boolean> interactBlock = sgGeneral.add(new BoolSetting.Builder()
        .name("interacting-block")
        .description("Intreact blocks in area.")
        .defaultValue(false)
        .build()
    );
    
    public final Setting<CardinalDirections> cardinaldirection = sgSettings.add(new EnumSetting.Builder<CardinalDirections>()
        .name("place-pirection")
        .description("Direction to use.")
        .defaultValue(CardinalDirections.Auto)
        .build()
    );

    public final Setting<Boolean> insideBlock = sgSettings.add(new BoolSetting.Builder()
        .name("inside-block")
        .description("Inside block value.")
        .defaultValue(false)
        .build()
    );

    //Script
    public final Setting<String> script = sgScript.add(new StringSetting.Builder()
        .name("script")
        .description("Action commands. +-=XYZxyz_")
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

    //Render
    public final Setting<Boolean> render = sgRender.add(new BoolSetting.Builder()
        .name("render")
        .description("Renders a block overlay where the obsidian will be placed.")
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

    public int c,x,y,z;
    public final UUID uuid;

    private MinerPlacer()
    {
        uuid = UUID.randomUUID();
    }
    
    public MinerPlacer(NbtElement tag)
    {
	    NbtCompound nbt = (NbtCompound) tag;
        uuid = nbt.get("uuid", Uuids.INT_STREAM_CODEC).orElse(UUID.randomUUID());
        fromTag(nbt);	
	}

    public void setColumn(int column)
    {
        c = column;
    }

    public enum CardinalDirections
    {
        Auto,
        Up,
        Down,
        North,
        South,
        East,
        West
    }
    
	public static class Builder
	{
		private String name = "";

        public Builder name(String name)
        {
            this.name = name;
            return this;
        }

        public MinerPlacer build()
        {
            MinerPlacer minerPlacer = new MinerPlacer();
            
			if (!name.equals(minerPlacer.name.getDefaultValue()))
				minerPlacer.name.set(name);

            return minerPlacer;
        }
    }
    
    @Override
    public CompoundTag toTag()
    {
        CompoundTag tag = new CompoundTag();

        tag.put("uuid", UUIDUtil.CODEC, uuid);
        tag.put("settings", settings.toTag());

        return tag;
    }

    @Override
    public MinerPlacer fromTag(CompoundTag tag)
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
			
        MinerPlacer minerPlacer = (MinerPlacer) o;
        return Objects.equals(uuid, minerPlacer.uuid);
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode(uuid);
    }

    @Override
    public String toString()
    {
        return name.get();
    }
}
