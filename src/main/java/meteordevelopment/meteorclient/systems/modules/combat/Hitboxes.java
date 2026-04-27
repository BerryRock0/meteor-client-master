/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.systems.modules.combat;

import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.friends.Friends;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.MaceItem;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.phys.AABB;

import java.util.Set;

public class Hitboxes extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Set<EntityType<?>>> entities = sgGeneral.add(new EntityTypeListSetting.Builder()
        .name("entities")
        .description("Which entities to target.")
        .defaultValue(EntityType.PLAYER)
        .build()
    );

   public final Setting<Mode> mode = sgGeneral.add(new EnumSetting.Builder<Mode>()
        .name("mode")
        .description("Select hitbox expland mode.")
        .defaultValue(Mode.Margin)
        .build()
    );
    
    public final Setting<Double> value = sgGeneral.add(new DoubleSetting.Builder()
        .name("expand")
        .description("How much to expand the hitbox of the entity.")
        .defaultValue(0.5)
        .visible(() -> mode.get() == Mode.Margin)
        .build()
    );
    
    public final Setting<Vector3d> box = sgGeneral.add(new Vector3dSetting.Builder()
        .name("box")
        .description("Entites box.")
        .defaultValue(0, 0, 0)
        .visible(() -> mode.get() == Mode.Box)
        .build()
    );

    public Hitboxes() {
        super(Categories.Combat, "hitboxes", "Expands an entity's hitboxes.");
    }

    public double getEntityValue(Entity entity)
    {
        if (isActive() && entities.get().contains(entity.getType()))
			return value.get();

        return 0;
    }
    
    public AABB getEntityBox(Entity entity)
    {
		if (isActive())
			return new AABB(entity.boundingBox.minX - box.get().x, entity.boundingBox.minY - box.get().y, entity.boundingBox.minZ - box.get().z, entity.boundingBox.maxX + box.get().x, entity.boundingBox.maxY + box.get().y, entity.boundingBox.maxZ + box.get().z);
			
		return entity.boundingBox;		
	}
    
    public enum Mode
    {
        Margin,
        Box
    }
}
