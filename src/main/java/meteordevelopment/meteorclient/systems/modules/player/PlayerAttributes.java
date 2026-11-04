/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.systems.modules.player;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import meteordevelopment.orbit.EventHandler;

import java.util.HashMap;
import java.util.Map;

public class PlayerAttributes extends Module {
    private final SettingGroup sgAttributes = settings.createGroup("Attributes");

    private final Setting<Double> movementSpeed = sgAttributes.add(new DoubleSetting.Builder()
        .name("movement-speed")
        .description("Movement speed of the local player while this module is enabled.")
        .defaultValue(0.2)
        .min(0.0)
        .sliderRange(0.0, 1024.0)
        .build());

    private final Setting<Double> attackDamage = sgAttributes.add(new DoubleSetting.Builder()
        .name("attack-damage")
        .description("Attack damage of the local player while this module is enabled.")
        .defaultValue(1.0)
        .min(0.0)
        .sliderRange(0.0, 1024.0)
        .build());

    private final Setting<Double> jumpStrength = sgAttributes.add(new DoubleSetting.Builder()
        .name("jump-strength")
        .description("Jump strength of the local player while this module is enabled.")
        .defaultValue(0.42)
        .min(0.0)
        .sliderRange(0.0, 1024.0)
        .build());

    private final Setting<Double> maxHealth = sgAttributes.add(new DoubleSetting.Builder()
        .name("max-health")
        .description("Maximum health of the local player while this module is enabled.")
        .defaultValue(20.0)
        .min(1.0)
        .sliderRange(1.0, 1024.0)
        .build());

    private final Setting<Double> flyingSpeed = sgAttributes.add(new DoubleSetting.Builder()
        .name("flying-speed")
        .description("Flying speed of the local player while this module is enabled.")
        .defaultValue(0.4)
        .min(0.0)
        .sliderRange(0.0, 1024.0)
        .build());

    private final Setting<Double> fallDamageMultiplier = sgAttributes.add(new DoubleSetting.Builder()
        .name("fall-damage-multiplier")
        .description("Fall damage multiplier of the local player while this module is enabled.")
        .defaultValue(1.0)
        .min(0.0)
        .sliderRange(0.0, 1024.0)
        .build());

    private final Setting<Double> gravity = sgAttributes.add(new DoubleSetting.Builder()
        .name("gravity")
        .description("Gravity of the local player while this module is enabled.")
        .defaultValue(0.08)
        .min(-1.0)
        .sliderRange(-1.0, 1.0)
        .build());

    private final Map<Holder<Attribute>, Double> originalAttributeValues = new HashMap<>();

    public PlayerAttributes() {
        super(Categories.Player, "player-attributes", "Changes attributes of the local player.");
    }

    @Override
    public void onActivate() {
        saveOriginalAttributeValues();
        applyAttributes();
    }

    @Override
    public void onDeactivate() {
        restoreAttributeValues();
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        if (!isActive() || mc.player == null) return;
        applyAttributes();
    }

    private void saveOriginalAttributeValues() {
        originalAttributeValues.clear();
        saveAttribute(Attributes.MOVEMENT_SPEED);
        saveAttribute(Attributes.ATTACK_DAMAGE);
        saveAttribute(Attributes.JUMP_STRENGTH);
        saveAttribute(Attributes.MAX_HEALTH);
        saveAttribute(Attributes.FLYING_SPEED);
        saveAttribute(Attributes.FALL_DAMAGE_MULTIPLIER);
        saveAttribute(Attributes.GRAVITY);
    }

    private void restoreAttributeValues() {
        for (Map.Entry<Holder<Attribute>, Double> entry : originalAttributeValues.entrySet()) {
            setAttribute(entry.getKey(), entry.getValue());
        }
        originalAttributeValues.clear();
    }

    private void saveAttribute(Holder<Attribute> attribute) {
        AttributeInstance instance = mc.player.getAttribute(attribute);
        if (instance != null) originalAttributeValues.put(attribute, instance.getBaseValue());
    }

    private void applyAttributes() {
        setAttribute(Attributes.MOVEMENT_SPEED, movementSpeed.get());
        setAttribute(Attributes.ATTACK_DAMAGE, attackDamage.get());
        setAttribute(Attributes.JUMP_STRENGTH, jumpStrength.get());
        setAttribute(Attributes.MAX_HEALTH, maxHealth.get());
        setAttribute(Attributes.FLYING_SPEED, flyingSpeed.get());
        setAttribute(Attributes.FALL_DAMAGE_MULTIPLIER, fallDamageMultiplier.get());
        setAttribute(Attributes.GRAVITY, gravity.get());
    }

    private void setAttribute(Holder<Attribute> attribute, double value) {
        AttributeInstance instance = mc.player.getAttribute(attribute);
        if (instance != null) instance.setBaseValue((float) value);
    }
}
