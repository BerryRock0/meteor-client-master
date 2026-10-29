/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.commands.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;
import net.minecraft.client.multiplayer.ClientSuggestionProvider;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.level.GameType;

public class GamemodeCommand extends Command {
    public GamemodeCommand() {
        super("gamemode", "Changes your gamemode. Real in singleplayer, client-side on servers.", "gm");
    }

    @Override
    public void build(LiteralArgumentBuilder<ClientSuggestionProvider> builder) {
        for (GameType gameMode : GameType.values()) {
            builder.then(literal(gameMode.getName()).executes(_ -> {
                if (mc.player == null || mc.gameMode == null) {
                    error("You must be in a world to change your gamemode.");
                    return 0;
                }

                // Switch the client-side mode right away: block breaking, the creative
                // inventory screen and every prediction key off getPlayerMode().
                mc.gameMode.setLocalMode(gameMode);

                // Mirror the mode into the player abilities so everything that checks
                // isCreative()/hasInfiniteMaterials() - infinite items, creative flight,
                // instant building - behaves like the real thing instead of only looking
                // like it.
                applyAbilities(gameMode);

                // Singleplayer: change the actual gamemode on the integrated server, so it
                // is a real change - the server accepts everything vanilla would (creative
                // inventory gives real items, block actions are not rolled back, no fall
                // damage, ...). On multiplayer servers this stays client-side only.
                if (mc.hasSingleplayerServer() && mc.getSingleplayerServer() != null) {
                    ServerPlayer serverPlayer = mc.getSingleplayerServer().getPlayerList().getPlayer(mc.player.getUUID());
                    if (serverPlayer != null) serverPlayer.gameMode.changeGameModeForPlayer(gameMode);
                }

                info("Gamemode changed to " + gameMode.getName() + ".");
                return SINGLE_SUCCESS;
            }));
        }
    }

    private static void applyAbilities(GameType gameMode) {
        Abilities abilities = mc.player.getAbilities();

        switch (gameMode) {
            case CREATIVE -> {
                abilities.instabuild = true;   // isCreative(): infinite items, no consumption
                abilities.mayfly = true;       // allows creative flight
                abilities.invulnerable = true; // no damage
                abilities.mayBuild = true;     // can place/break
            }
            case SPECTATOR -> {
                abilities.instabuild = false;
                abilities.mayfly = true;
                abilities.flying = true;
                abilities.invulnerable = true;
                abilities.mayBuild = false;
            }
            default -> {
                abilities.instabuild = false;
                abilities.mayfly = false;
                abilities.flying = false;
                abilities.invulnerable = false;
                abilities.mayBuild = true;
            }
        }
    }
}
