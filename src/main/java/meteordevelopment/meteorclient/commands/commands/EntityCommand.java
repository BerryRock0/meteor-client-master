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
    builder.then(literal("list")).executes(_ -> {list(); return SINGLE_SUCCESS;})
    .then(literal("create")).executes(_ -> {return SINGLE_SUCCESS;})
    .then(literal("erase")).executes(_ -> {return SINGLE_SUCCESS;})
    .then(literal("teleport")).executes(_ -> {return SINGLE_SUCCESS;})
    .then(literal("tick")).executes(_ -> {return SINGLE_SUCCESS;})
    .then(literal("move")).executes(_ -> {return SINGLE_SUCCESS;});
  }

  public void list()
  {
    for(Entity entity: mc.level.entitiesForRendering())
      info(entity.getUUID().toString());
  }
}
