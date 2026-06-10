/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.systems.modules.movement;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.pathing.NopPathManager;
import meteordevelopment.meteorclient.pathing.PathManagers;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.orbit.EventPriority;

public class AutoWalk extends Module
{
    public AutoWalk()
    {
        super(Categories.Movement, "auto-walk", "Automatically walks.");
    }

    @Override
    public void onActivate()
    {
        createGoal();
    }

    @Override
    public void onDeactivate()
    {
        PathManagers.get().stop();
    }

    @EventHandler(priority = EventPriority.HIGH)
    private void onTick(TickEvent.Pre event)
    {
        if (PathManagers.get() instanceof NopPathManager)
        {
            info("Smart mode requires Baritone");
            toggle();
        }
    }

    private void createGoal()
    {
        PathManagers.get().moveInDirection(mc.player.getYRot());
    }
}
