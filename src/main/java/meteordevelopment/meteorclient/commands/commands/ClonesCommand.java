/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.commands.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;
import meteordevelopment.meteorclient.commands.arguments.CloneArgumentType;
import meteordevelopment.meteorclient.systems.modules.player.CloneUnit;
import meteordevelopment.meteorclient.utils.entity.clone.CloneEntity;
import meteordevelopment.meteorclient.utils.entity.clone.CloneManager;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import net.minecraft.client.multiplayer.ClientSuggestionProvider;

public class ClonesCommand extends Command {
    public ClonesCommand() {
        super("clones", "Manages character clones that you can use for testing.");
    }

    @Override
    public void build(LiteralArgumentBuilder<ClientSuggestionProvider> builder) {
        builder.then(literal("add")
            .executes(_ -> {
                CloneManager.add();
                return SINGLE_SUCCESS;
            })
            .then(argument("name", StringArgumentType.word())
                .executes(context -> {
                    CloneUnit unit = new CloneUnit.Builder().build();
                    unit.name.set(StringArgumentType.getString(context, "name"));
                    CloneManager.add(unit);
                    return SINGLE_SUCCESS;
                })
            )
        );

        builder.then(literal("remove")
            .then(argument("fp", CloneArgumentType.create())
                .executes(context -> {
                    CloneEntity fp = CloneArgumentType.get(context);
                    if (fp == null || !CloneManager.contains(fp)) {
                        error("Couldn't find a clone with that name.");
                        return SINGLE_SUCCESS;
                    }

                    CloneManager.remove(fp);
                    info("Removed clone %s.".formatted(fp.getName().getString()));

                    return SINGLE_SUCCESS;
                })
            )
        );

        builder.then(literal("clear")
            .executes(_ -> {
                CloneManager.clear();
                return SINGLE_SUCCESS;
            })
        );

        builder.then(literal("list")
            .executes(_ -> {
                info("--- Clones ((highlight)%s(default)) ---", CloneManager.count());
                CloneManager.forEach(fp -> ChatUtils.info("(highlight)%s".formatted(fp.getName().getString())));
                return SINGLE_SUCCESS;
            })
        );
    }
}
