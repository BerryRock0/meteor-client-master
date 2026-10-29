/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.systems.clones;

import java.util.Objects;
import java.util.UUID;

import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.Settings;
import meteordevelopment.meteorclient.settings.StringSetting;
import meteordevelopment.meteorclient.utils.misc.ISerializable;
import meteordevelopment.meteorclient.systems.modules.player.Clones;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

/**
 * A persistent description of one clone - the equivalent of a {@code MinerPlacer} worker unit.
 * Every aspect of a clone lives in this unit's own {@link Settings}, grouped by category and
 * edited in its own screen, so a set of configured units can be saved per world (by
 * {@link CloneUnits}) and brought back to life by enabling the {@link Clones} module, which runs
 * one live clone per unit.
 */
public class CloneUnit implements ISerializable<CloneUnit>
{
    public final Settings settings = new Settings();

    private final SettingGroup sgGeneral = settings.createGroup("General");
    private final SettingGroup sgAttributes = settings.createGroup("Attributes");
    private final SettingGroup sgBehavior = settings.createGroup("Behavior");
    private final SettingGroup sgServer = settings.createGroup("Server");

    public final Setting<String> name = sgGeneral.add(new StringSetting.Builder()
        .name("name")
        .description("The name of the clone.")
        .defaultValue("")
        .build()
    );

    public final Setting<String> helmet = sgGeneral.add(new StringSetting.Builder()
        .name("helmet")
        .description("Item (id) in the clone's helmet slot, e.g. minecraft:netherite_helmet. Empty means no helmet.")
        .defaultValue("")
        .build()
    );

    public final Setting<String> chestplate = sgGeneral.add(new StringSetting.Builder()
        .name("chestplate")
        .description("Item (id) in the clone's chestplate slot, e.g. minecraft:netherite_chestplate. Empty means no chestplate.")
        .defaultValue("")
        .build()
    );

    public final Setting<String> leggings = sgGeneral.add(new StringSetting.Builder()
        .name("leggings")
        .description("Item (id) in the clone's leggings slot, e.g. minecraft:netherite_leggings. Empty means no leggings.")
        .defaultValue("")
        .build()
    );

    public final Setting<String> boots = sgGeneral.add(new StringSetting.Builder()
        .name("boots")
        .description("Item (id) in the clone's boots slot, e.g. minecraft:netherite_boots. Empty means no boots.")
        .defaultValue("")
        .build()
    );
    
    public final Setting<String> mainHand = sgGeneral.add(new StringSetting.Builder()
        .name("main-hand")
        .description("Item (id) equipped in the clone's main hand, e.g. minecraft:netherite_sword. Empty means unarmed.")
        .defaultValue("")
        .build()
    );

    public final Setting<String> offHand = sgGeneral.add(new StringSetting.Builder()
        .name("off-hand")
        .description("Item (id) equipped in the clone's off hand, e.g. minecraft:shield. Empty means empty.")
        .defaultValue("")
        .build()
    );

	//Behaviour
    public final Setting<Clones.Behaviour> behaviour = sgBehavior.add(new EnumSetting.Builder<Clones.Behaviour>()
        .name("behaviour")
        .description("How the clone behaves.")
        .defaultValue(Clones.Behaviour.Stasis)
        .build()
    );

    public final Setting<Double> followDistance = sgBehavior.add(new DoubleSetting.Builder()
        .name("follow-distance")
        .description("How close the clone follows you before stopping.")
        .defaultValue(2)
        .build()
    );

    public final Setting<Double> wanderRadius = sgBehavior.add(new DoubleSetting.Builder()
        .name("wander-radius")
        .description("How far from its current position the clone wanders.")
        .defaultValue(10)
        .build()
    );
    
   public final Setting<Double> range = sgBehavior.add(new DoubleSetting.Builder()
        .name("range")
        .description("How far to look for mining or combat targets.")
        .defaultValue(6)
        .build()
    );

    public final Setting<Boolean> sprint = sgBehavior.add(new BoolSetting.Builder()
        .name("sprint")
        .description("Makes the clone sprint while moving.")
        .defaultValue(false)
        .build()
    );

    public final Setting<Boolean> swing = sgBehavior.add(new BoolSetting.Builder()
        .name("swing")
        .description("Makes the clone swing its arm while moving.")
        .defaultValue(true)
        .build()
    );


	//Attributes
    public final Setting<Double> armor = sgAttributes.add(new DoubleSetting.Builder().name("armor").description("").defaultValue(0).sliderRange(0,30).build());
	public final Setting<Double> armorToughness = sgAttributes.add(new DoubleSetting.Builder().name("armorToughness").description("").defaultValue(0).sliderRange(0,20).build());
    public final Setting<Double> attackDamage = sgAttributes.add(new DoubleSetting.Builder().name("attackDamage").description("").defaultValue(2).sliderRange(0,2048).build());
    public final Setting<Double> attackKnockback = sgAttributes.add(new DoubleSetting.Builder().name("attackKnockback").description("").defaultValue(0).sliderRange(0,5).build());
    public final Setting<Double> attackSpeed = sgAttributes.add(new DoubleSetting.Builder().name("attackSpeed").description("").defaultValue(4).sliderRange(0,1024).build());
    public final Setting<Double> blockBreakSpeed = sgAttributes.add(new DoubleSetting.Builder().name("blockBreakSpeed").description("").defaultValue(1).sliderRange(0,1024).build());
    public final Setting<Double> blockInteractionRange = sgAttributes.add(new DoubleSetting.Builder().name("blockInteractionRange").description("").defaultValue(4.5).sliderRange(0,64).build());
    public final Setting<Double> burningTime = sgAttributes.add(new DoubleSetting.Builder().name("burningTime").description("").defaultValue(0).sliderRange(0,1024).build());
    public final Setting<Double> cameraDistance = sgAttributes.add(new DoubleSetting.Builder().name("cameraDistance").description("").defaultValue(4).sliderRange(0,32).build());
	public final Setting<Double> entityInteractionRange = sgAttributes.add(new DoubleSetting.Builder().name("entityInteractionRange").description("").defaultValue(3).sliderRange(0,64).build());
    public final Setting<Double> explosionKnockbackResistance = sgAttributes.add(new DoubleSetting.Builder().name("explosionKnockbackResistance").description("").defaultValue(0).sliderRange(0,1).build());
    public final Setting<Double> fallDamageMultiplier = sgAttributes.add(new DoubleSetting.Builder().name("fallDamageMultiplier").description("").defaultValue(1).sliderRange(0,100).build());
    public final Setting<Double> flyingSpeed = sgAttributes.add(new DoubleSetting.Builder().name("flyingSpeed").description("").defaultValue(0.4).sliderRange(0,1024).build());
    public final Setting<Double> followRange = sgAttributes.add(new DoubleSetting.Builder().name("followRange").description("").defaultValue(32).sliderRange(0,2048).build());
    public final Setting<Double> gravity = sgAttributes.add(new DoubleSetting.Builder().name("gravity").description("").defaultValue(0.08).sliderRange(-1,1).build());
    public final Setting<Double> jumpStrength = sgAttributes.add(new DoubleSetting.Builder().name("jumpStrength").description("").defaultValue(0.42).sliderRange(0,32).build());
    public final Setting<Double> knockbackResistance = sgAttributes.add(new DoubleSetting.Builder().name("knockbackResistance").description("").defaultValue(0).sliderRange(-2,1).build());
	public final Setting<Double> luck = sgAttributes.add(new DoubleSetting.Builder().name("luck").description("").defaultValue(0).sliderRange(-1024,1024).build());
    public final Setting<Double> maxAbsorption = sgAttributes.add(new DoubleSetting.Builder().name("maxAbsorption").description("").defaultValue(0).sliderRange(0,2048).build());
    public final Setting<Double> maxHealth = sgAttributes.add(new DoubleSetting.Builder().name("maxHealth").description("").defaultValue(20).sliderRange(1,1024).build());
    public final Setting<Double> miningEfficiency = sgAttributes.add(new DoubleSetting.Builder().name("miningEfficiency").description("").defaultValue(0).sliderRange(0,1024).build());
    public final Setting<Double> movementEfficiency = sgAttributes.add(new DoubleSetting.Builder().name("movementEfficiency").description("").defaultValue(0).sliderRange(0,1).build());
    public final Setting<Double> movementSpeed = sgAttributes.add(new DoubleSetting.Builder().name("movementSpeed").description("").defaultValue(0.7).sliderRange(0,1024).build());
    public final Setting<Double> oxygenBonus = sgAttributes.add(new DoubleSetting.Builder().name("oxygenBonus").description("").defaultValue(0).sliderRange(0,1024).build());
    public final Setting<Double> safeFallDistance = sgAttributes.add(new DoubleSetting.Builder().name("safeFallDistance").description("").defaultValue(3).sliderRange(-1024,1024).build());
	public final Setting<Double> scale = sgAttributes.add(new DoubleSetting.Builder().name("scale").description("").defaultValue(1).sliderRange(0.0625,16).build());
    public final Setting<Double> sneakingSpeed = sgAttributes.add(new DoubleSetting.Builder().name("sneakingSpeed").description("").defaultValue(0.3).sliderRange(0,1).build());
    public final Setting<Double> spawnReinforcements = sgAttributes.add(new DoubleSetting.Builder().name("spawnReinforcements").description("").defaultValue(0).sliderRange(0,1).build());
    public final Setting<Double> stepHeight = sgAttributes.add(new DoubleSetting.Builder().name("stepHeight").description("").defaultValue(0.6).sliderRange(0,10).build());
    public final Setting<Double> submergedMiningSpeed = sgAttributes.add(new DoubleSetting.Builder().name("submergedMiningSpeed").description("").defaultValue(0.2).sliderRange(0,20).build());
    public final Setting<Double> sweepingDamageRatio = sgAttributes.add(new DoubleSetting.Builder().name("sweepingDamageRatio").description("").defaultValue(0).sliderRange(0,1).build());
    public final Setting<Double> temptRange = sgAttributes.add(new DoubleSetting.Builder().name("temptRange").description("").defaultValue(10).sliderRange(0,2048).build());
    public final Setting<Double> waterMovementEfficiency = sgAttributes.add(new DoubleSetting.Builder().name("waterMovementEfficiency").description("").defaultValue(0).sliderRange(0,1).build());
	public final Setting<Double> waypointReceiveRange = sgAttributes.add(new DoubleSetting.Builder().name("waypointReceiveRange").description("").defaultValue(0).sliderRange(0,60000000).build());
    public final Setting<Double> waypointTransmitRange = sgAttributes.add(new DoubleSetting.Builder().name("waypointTransmitRange").description("").defaultValue(0).sliderRange(0,60000000).build());


	//Server
    public final Setting<Boolean> serverClone = sgServer.add(new BoolSetting.Builder()
        .name("server-clone")
        .description("Spawns the clone as a real player on the integrated server.")
        .defaultValue(true)
        .build()
    );

    public final UUID uuid;

    private CloneUnit()
    {
        uuid = UUID.randomUUID();
    }

    public CloneUnit(Tag tag)
    {
        CompoundTag nbt = (CompoundTag) tag;
        uuid = nbt.read("uuid", UUIDUtil.CODEC).orElse(UUID.randomUUID());
        fromTag(nbt);
    }

    /**
     * Wraps one-off spawn parameters of the public {@code CloneManager#add} API into a unit
     * that is not stored in {@link CloneUnits}.
     */
    public static CloneUnit transientOf(String name, float health)
    {
        CloneUnit unit = new Builder().build();
        unit.name.set(name);
        unit.maxHealth.set((double) health);
        return unit;
    }

    public static class Builder
    {
        public CloneUnit build()
        {
            return new CloneUnit();
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
    public CloneUnit fromTag(CompoundTag tag)
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

        CloneUnit that = (CloneUnit) o;

        return Objects.equals(uuid, that.uuid);
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode(uuid);
    }
}
