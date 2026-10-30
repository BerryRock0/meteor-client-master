/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.commands.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;
import meteordevelopment.meteorclient.utils.player.FakeGamemode;
import net.minecraft.client.multiplayer.ClientSuggestionProvider;
import net.minecraft.world.level.GameType;

public class GamemodeCommand extends Command {
    public GamemodeCommand() {
        super("gamemode", "Changes your gamemode. Really where the server allows it (always in singleplayer), otherwise an invisible client-side fake.", "gm");
    }

    @Override
    public void build(LiteralArgumentBuilder<ClientSuggestionProvider> builder) {
        for (GameType gameMode : GameType.values()) {
            builder.then(literal(gameMode.getName()).executes(_ -> {
                if (mc.player == null || mc.gameMode == null) {
                    error("You must be in a world to change your gamemode.");
                    return 0;
                }

                GameType confirmed = FakeGamemode.serverConfirmed();

                if (gameMode == confirmed) {
                    // The server already has this mode: make everything real - sync the
                    // local state and drop any fake that may still be armed.
                    FakeGamemode.disarm();
                    info("Gamemode changed to " + gameMode.getName() + ".");
                } else {
                    // Real attempt first (vanilla flow in singleplayer, the vanilla
                    // gamemode packet on servers); while the server has not granted the
                    // mode, an invisible client-side fake keeps it working locally.
                    FakeGamemode.arm(gameMode);
                    if (confirmed == null) info("Gamemode changed to " + gameMode.getName() + ".");
                    else info("Requested " + gameMode.getName() + ": real if the server allows it, otherwise an invisible client-side fake.");
                }

                return SINGLE_SUCCESS;
            }));
        }
    }
}
