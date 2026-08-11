/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.systems.modules.world;

import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;

public class Behaviors extends Module 
{
  private final SettingGroup sgAttack = settings.createGroup("Attack");
  
  public final Setting<String> target = sgAttack.add(new StringSetting.Builder()
      .name("Target")
      .description("UUID of scapegoat.")
      .build()
  );

    public Behaviors()
    {
       super(Categories.World, "behaviors", "Controls mobs behaviors.");
    }

    public boolean attackTask(Entity entity)
    {
        return isActive() && isUnwanted(entity);
    }
  
    public boolean isUnwanted(Entity entity)
    {
        return target.get() == entity.getUUID().toString();
    }
}
