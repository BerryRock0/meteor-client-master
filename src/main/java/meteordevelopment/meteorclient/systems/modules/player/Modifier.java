/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.systems.modules.player;

import java.util.Map;
import java.util.HashMap;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.*;

import meteordevelopment.orbit.EventHandler;

import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.events.world.TickEvent;

public class Modifier extends Module
{
    private final SettingGroup sgAttributes = settings.createGroup("Attributes");

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

    private final Map<Holder<Attribute>, Double> originalAttributeValues = new HashMap<>();

    public Modifier()
    {
        super(Categories.Player, "modifier", "Changes attributes of the local player.");
    }

    @Override
    public void onActivate()
    {
        saveOriginalAttributeValues();
        applyAttributes();
    }

    @Override
    public void onDeactivate()
    {
        restoreAttributeValues();
    }

    @EventHandler
    private void onTick(TickEvent.Pre event)
    {
        if (!isActive() || mc.player == null)
            return;
        applyAttributes();
    }

    private void applyAttributes()
    {
        setAttribute(Attributes.ARMOR, armor.get());
        setAttribute(Attributes.ARMOR_TOUGHNESS, armorToughness.get());
        setAttribute(Attributes.ATTACK_DAMAGE, attackDamage.get());
        setAttribute(Attributes.ATTACK_KNOCKBACK, attackKnockback.get());
        setAttribute(Attributes.ATTACK_SPEED, attackSpeed.get());
        setAttribute(Attributes.BLOCK_BREAK_SPEED, blockBreakSpeed.get());
        setAttribute(Attributes.BLOCK_INTERACTION_RANGE, blockInteractionRange.get());
        setAttribute(Attributes.BURNING_TIME, burningTime.get());
        setAttribute(Attributes.CAMERA_DISTANCE, cameraDistance.get());
        setAttribute(Attributes.ENTITY_INTERACTION_RANGE, entityInteractionRange.get());
        setAttribute(Attributes.EXPLOSION_KNOCKBACK_RESISTANCE, explosionKnockbackResistance.get());
        setAttribute(Attributes.FALL_DAMAGE_MULTIPLIER, fallDamageMultiplier.get());
        setAttribute(Attributes.FLYING_SPEED, flyingSpeed.get());
        setAttribute(Attributes.FOLLOW_RANGE, followRange.get());
        setAttribute(Attributes.GRAVITY, gravity.get());
        setAttribute(Attributes.JUMP_STRENGTH, jumpStrength.get());
        setAttribute(Attributes.KNOCKBACK_RESISTANCE, knockbackResistance.get());
        setAttribute(Attributes.LUCK, luck.get());
        setAttribute(Attributes.MAX_ABSORPTION, maxAbsorption.get());
        setAttribute(Attributes.MAX_HEALTH, maxHealth.get());
        setAttribute(Attributes.MINING_EFFICIENCY, miningEfficiency.get());
        setAttribute(Attributes.MOVEMENT_EFFICIENCY, movementEfficiency.get());
        setAttribute(Attributes.MOVEMENT_SPEED, movementSpeed.get());
        setAttribute(Attributes.OXYGEN_BONUS, oxygenBonus.get());
        setAttribute(Attributes.SAFE_FALL_DISTANCE, safeFallDistance.get());
        setAttribute(Attributes.SCALE, scale.get());
        setAttribute(Attributes.SNEAKING_SPEED, sneakingSpeed.get());
        setAttribute(Attributes.SPAWN_REINFORCEMENTS_CHANCE, spawnReinforcements.get());
        setAttribute(Attributes.STEP_HEIGHT, stepHeight.get());
        setAttribute(Attributes.SUBMERGED_MINING_SPEED, submergedMiningSpeed.get());
        setAttribute(Attributes.SWEEPING_DAMAGE_RATIO, sweepingDamageRatio.get());
        setAttribute(Attributes.TEMPT_RANGE, temptRange.get());
        setAttribute(Attributes.WATER_MOVEMENT_EFFICIENCY, waterMovementEfficiency.get());
        setAttribute(Attributes.WAYPOINT_RECEIVE_RANGE, waypointReceiveRange.get());
        setAttribute(Attributes.WAYPOINT_TRANSMIT_RANGE, waypointTransmitRange.get());
    }

    private void saveOriginalAttributeValues()
    {
        originalAttributeValues.clear();
        saveAttribute(Attributes.ARMOR);
        saveAttribute(Attributes.ARMOR_TOUGHNESS);
        saveAttribute(Attributes.ATTACK_DAMAGE);
        saveAttribute(Attributes.ATTACK_KNOCKBACK);
        saveAttribute(Attributes.ATTACK_SPEED);
        saveAttribute(Attributes.BLOCK_BREAK_SPEED);
        saveAttribute(Attributes.BLOCK_INTERACTION_RANGE);
        saveAttribute(Attributes.BURNING_TIME);
        saveAttribute(Attributes.CAMERA_DISTANCE);
        saveAttribute(Attributes.ENTITY_INTERACTION_RANGE);
        saveAttribute(Attributes.EXPLOSION_KNOCKBACK_RESISTANCE);
        saveAttribute(Attributes.FALL_DAMAGE_MULTIPLIER);
        saveAttribute(Attributes.FLYING_SPEED);
        saveAttribute(Attributes.FOLLOW_RANGE);
        saveAttribute(Attributes.GRAVITY);
        saveAttribute(Attributes.JUMP_STRENGTH);
        saveAttribute(Attributes.KNOCKBACK_RESISTANCE);
        saveAttribute(Attributes.LUCK);
        saveAttribute(Attributes.MAX_ABSORPTION);
        saveAttribute(Attributes.MAX_HEALTH);
        saveAttribute(Attributes.MINING_EFFICIENCY);
        saveAttribute(Attributes.MOVEMENT_EFFICIENCY);
        saveAttribute(Attributes.MOVEMENT_SPEED);
        saveAttribute(Attributes.OXYGEN_BONUS);
        saveAttribute(Attributes.SAFE_FALL_DISTANCE);
        saveAttribute(Attributes.SCALE);
        saveAttribute(Attributes.SNEAKING_SPEED);
        saveAttribute(Attributes.SPAWN_REINFORCEMENTS_CHANCE);
        saveAttribute(Attributes.STEP_HEIGHT);
        saveAttribute(Attributes.SUBMERGED_MINING_SPEED);
        saveAttribute(Attributes.SWEEPING_DAMAGE_RATIO);
        saveAttribute(Attributes.TEMPT_RANGE);
        saveAttribute(Attributes.WATER_MOVEMENT_EFFICIENCY);
        saveAttribute(Attributes.WAYPOINT_RECEIVE_RANGE);
        saveAttribute(Attributes.WAYPOINT_TRANSMIT_RANGE);
        
    }

    private void setAttribute(Holder<Attribute> attribute, double value)
    {
        AttributeInstance instance = mc.player.getAttribute(attribute);
        if (instance != null)
            instance.setBaseValue((float) value);
    }

    private void saveAttribute(Holder<Attribute> attribute)
    {
        AttributeInstance instance = mc.player.getAttribute(attribute);
        if (instance != null)
            originalAttributeValues.put(attribute, instance.getBaseValue());
    }

    private void restoreAttributeValues()
    {
        for (Map.Entry<Holder<Attribute>, Double> entry : originalAttributeValues.entrySet())
            setAttribute(entry.getKey(), entry.getValue());
        originalAttributeValues.clear();
    }
}
