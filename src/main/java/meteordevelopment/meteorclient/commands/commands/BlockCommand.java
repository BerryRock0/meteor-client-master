/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.commands.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import meteordevelopment.meteorclient.commands.Command;

public class BlockCommand extends Command
{
  public BlockCommand()
  {
    super("block", "Block manipulator.", "block");
  }

  @Override
  public void build(LiteralArgumentBuilder<ClientSuggestionProvider> builder)
  {
    builder.then(literal("help")).executes(_ -> {showHelp(this); return SINGLE_SUCCESS;});
    builder.then(literal("search")).executes(context -> {return SINGLE_SUCCESS;});
    builder.then(literal("break")).executes(context -> {return SINGLE_SUCCESS;});
    builder.then(literal("interact")).executes(context -> {return SINGLE_SUCCESS;});
    builder.then(literal("place")).executes(context -> {return SINGLE_SUCCESS;});
    builder.then(literal("remove")).executes(context -> {return SINGLE_SUCCESS;});
  }
}
