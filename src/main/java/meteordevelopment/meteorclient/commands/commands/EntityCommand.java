/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.commands.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import meteordevelopment.meteorclient.commands.Command;
import net.minecraft.client.multiplayer.ClientSuggestionProvider;

import net.minecraft.world.entity.*;

public class EntityCommand extends Command
{
  public EntityCommand()
  {
    super("entity", "Entity manipulator.", "entity");
  }

  @Override
  public void build(LiteralArgumentBuilder<ClientSuggestionProvider> builder)
  {
    builder.then(literal("list")).executes(context -> {list(); return SINGLE_SUCCESS;});
    builder.then(literal("create")).executes(context -> {return SINGLE_SUCCESS;});
    builder.then(literal("erase")).executes(context -> {return SINGLE_SUCCESS;});
    builder.then(literal("teleport")).executes(context -> {return SINGLE_SUCCESS;});
    builder.then(literal("tick")).executes(context -> {return SINGLE_SUCCESS;});  
    builder.then(literal("move")).executes(context -> {return SINGLE_SUCCESS;});
  }

  public void list()
  {
    for(Entity entity: mc.level.entitiesForRendering())
      info(entity.getUUID().toString());
  }
}
