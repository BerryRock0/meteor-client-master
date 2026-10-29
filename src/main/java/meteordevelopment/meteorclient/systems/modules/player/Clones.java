/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.systems.modules.player;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.screens.EditSystemScreen;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.gui.widgets.containers.WTable;
import meteordevelopment.meteorclient.gui.widgets.pressable.WButton;
import meteordevelopment.meteorclient.mc.Vanilla;
import meteordevelopment.meteorclient.systems.friends.Friends;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.render.Xray;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.entity.clone.CloneEntity;
import meteordevelopment.meteorclient.utils.entity.clone.CloneManager;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import meteordevelopment.meteorclient.MeteorClient;

import static meteordevelopment.meteorclient.MeteorClient.mc;

/**
 * Manages character clones as worker-style units, in the spirit of {@code WorkersModule}: every
 * clone is described by a persistent {@link CloneUnit} (name, health, behaviour, ...),
 * units are stored per world by {@link CloneUnits} and edited in their own screen. The
 * module is the engine: while it is enabled, every unit keeps one live clone spawned at your
 * position. Disabling the module despawns every clone.
 */
public class Clones extends Module
{
    public Clones()
    {
        super(Categories.Player, "clones", "Manages character clones as worker-style units: each unit describes one clone and is saved per world. Enable the module to run the units.");
    }

    public enum Behaviour
    {
        Stasis,
        Mirror,
        Follow,
        Wander,
        Mine,
        Fight
    }

    @Override
    public void onDeactivate()
    {
        CloneManager.clear();
    }

    /** The engine: every unit keeps one live clone spawned at your position. */
    @EventHandler
    private void onTick(TickEvent.Pre event)
    {
        if (!Utils.canUpdate())
			return;

        for (CloneUnit unit : CloneUnits.get())
        {
            if (CloneManager.get(unit) == null)
                CloneManager.add(unit);
        }
    }

    /** Nearest resource block within {@code range} of {@code origin}, or null. */
    public static BlockPos findNearestResource(Vec3 origin, double range)
    {
        if (MeteorClient.mc.level == null) return null;

        BlockPos best = null;
        double bestDist = range * range;
        int r = (int) Math.ceil(range);
        int ox = Mth.floor(origin.x()), oy = Mth.floor(origin.y()), oz = Mth.floor(origin.z());

        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int x = ox - r; x <= ox + r; x++)
        {
            for (int y = oy - r; y <= oy + r; y++)
            {
                for (int z = oz - r; z <= oz + r; z++)
                {
                    pos.set(x, y, z);
                    if (!Xray.ORES.contains(MeteorClient.mc.level.getBlockState(pos).getBlock())) continue;

                    double d = origin.distanceToSqr(x + 0.5, y + 0.5, z + 0.5);
                    if (d <= bestDist)
                    {
                        bestDist = d;
                        best = pos.immutable();
                    }
                }
            }
        }

        return best;
    }

    /** Nearest attackable living entity within {@code range} of {@code origin}, or null. */
    public static LivingEntity findNearestEnemy(Vec3 origin, double range)
    {
        if (MeteorClient.mc.level == null) return null;

        LivingEntity best = null;
        double bestDist = range * range;

        for (Entity e : MeteorClient.mc.level.entitiesForRendering())
        {
            if (!(e instanceof LivingEntity le)) continue;
            if (!le.isAlive() || le.isDeadOrDying()) continue;
            if (le == MeteorClient.mc.player) continue;
            if (le instanceof CloneEntity) continue;
            if (CloneManager.isClone(le)) continue;

            if (le instanceof Player p)
            {
                if (p.isCreative() || p.isSpectator()) continue;
                if (!Friends.get().shouldAttack(p)) continue;
            }

            double d = origin.distanceToSqr(le.position());
            if (d <= bestDist)
            {
                bestDist = d;
                best = le;
            }
        }

        return best;
    }

    // GUI - unit table, in the spirit of WorkersModule

    @Override
    public WWidget getWidget(GuiTheme theme)
    {
        if (!Utils.canUpdate())
			return theme.label("You need to be in a world.");

        WTable table = theme.table();
        initTable(theme, table);
        return table;
    }

    private void initTable(GuiTheme theme, WTable table)
    {
        table.clear();

        for (CloneUnit unit : CloneUnits.get())
        {
            table.add(theme.label(unit.name.get()));
            WButton spawn = table.add(theme.button("Spawn")).widget(); spawn.action = () -> {if (CloneManager.get(unit) == null) CloneManager.add(unit); initTable(theme, table);};
            WButton despawn = table.add(theme.button("Despawn")).widget(); despawn.action = () -> {CloneManager.remove(unit); initTable(theme, table);};
            WButton edit = table.add(theme.button("Edit")).widget(); edit.action = () -> mc.gui.setScreen(new EditCloneUnitScreen(theme, unit, () -> initTable(theme, table)));
            WButton delete = table.add(theme.button("Delete")).widget(); delete.action = () -> {CloneManager.remove(unit); CloneUnits.get().remove(unit); initTable(theme, table);};

            table.row();
        }

        table.add(theme.horizontalSeparator()).expandX();
        table.row();

        WButton create = table.add(theme.button("Create")).expandX().widget(); create.action = () -> mc.gui.setScreen(new EditCloneUnitScreen(theme, null, () -> initTable(theme, table)));
    }

    private static class EditCloneUnitScreen extends EditSystemScreen<CloneUnit>
    {
        public EditCloneUnitScreen(GuiTheme theme, CloneUnit value, Runnable reload)
        {
            super(theme, value, reload);
        }

        @Override
        public CloneUnit create()
        {
            return new CloneUnit.Builder().build();
        }

        @Override
        public boolean save()
        {
            CloneUnits.get().add(value);
            return true;
        }

        @Override
        public meteordevelopment.meteorclient.settings.Settings getSettings()
        {
            return value.settings;
        }
    }
}
