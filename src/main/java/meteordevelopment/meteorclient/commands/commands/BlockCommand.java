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

public class BlockCommand extends Command
{
  public BlockCommand()
  {
    super("block", "Block manipulator.", "block");
  }

  @Override
  public void build(LiteralArgumentBuilder<ClientSuggestionProvider> builder)
  {
    builder.then(literal("search")).executes(_ -> {return SINGLE_SUCCESS;})
    .then(literal("break")).executes(_ -> {return SINGLE_SUCCESS;})
    .then(literal("interact")).executes(_ -> {return SINGLE_SUCCESS;})
    .then(literal("place")).executes(_ -> {return SINGLE_SUCCESS;})
    .then(literal("remove")).executes(_ -> {return SINGLE_SUCCESS;});
  }
}
