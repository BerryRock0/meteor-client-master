/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.systems.modules.misc;

import meteordevelopment.meteorclient.events.entity.player.InteractBlockEvent;
import meteordevelopment.meteorclient.events.meteor.KeyInputEvent;
import meteordevelopment.meteorclient.events.meteor.MouseClickEvent;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.render.Render2DEvent;
import meteordevelopment.meteorclient.settings.BlockListSetting;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.KeybindSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.misc.Keybind;
import meteordevelopment.meteorclient.utils.misc.input.KeyAction;
import meteordevelopment.meteorclient.utils.render.NametagUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundContainerSetContentPacket;
import net.minecraft.network.protocol.game.ClientboundOpenScreenPacket;
import net.minecraft.network.protocol.game.ServerboundContainerClosePacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Shows the contents of containers without opening them.
 *
 * <p>When the player interacts with a container, the server answers with an open screen packet
 * followed by the contents of the container. Both packets are swallowed here, so the screen never
 * opens, and the contents are rendered instead. The container is closed right away, so the chest
 * is not locked for anybody else.
 *
 * <p>Clicking the same container again opens it normally. Previews are kept until removed with the
 * delete button on the preview or the clear keybind.
 */
public class ChestPreview extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final SettingGroup sgRender = settings.createGroup("Render");

    private final Setting<List<Block>> containers = sgGeneral.add(new BlockListSetting.Builder()
        .name("containers")
        .description("Blocks whose contents are previewed.")
        .defaultValue(Blocks.CHEST, Blocks.BARREL, Blocks.SHULKER_BOX)
        .build()
    );

    private final Setting<Keybind> clearBind = sgGeneral.add(new KeybindSetting.Builder()
        .name("clear")
        .description("Key that removes all previews.")
        .defaultValue(Keybind.none())
        .build()
    );

    private final Setting<Mode> mode = sgRender.add(new EnumSetting.Builder<Mode>()
        .name("mode")
        .description("Where the contents are shown.")
        .defaultValue(Mode.World)
        .build()
    );

    private final Setting<Double> scale = sgRender.add(new DoubleSetting.Builder()
        .name("scale")
        .description("Scale of the preview.")
        .defaultValue(1)
        .min(0.25)
        .max(4)
        .build()
    );

    private final Setting<Boolean> showCount = sgRender.add(new BoolSetting.Builder()
        .name("show-count")
        .description("Shows how many of each item there are.")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> showEmpty = sgRender.add(new BoolSetting.Builder()
        .name("show-empty")
        .description("Shows containers that are empty.")
        .defaultValue(false)
        .build()
    );

    private final Setting<Integer> maxItems = sgRender.add(new IntSetting.Builder()
        .name("max-items")
        .description("Maximum amount of items that are shown.")
        .defaultValue(54)
        .min(1)
        .max(54)
        .build()
    );

    private final Setting<Integer> maxDistance = sgRender.add(new IntSetting.Builder()
        .name("max-distance")
        .description("Maximum distance the preview is shown at.")
        .defaultValue(32)
        .min(1)
        .max(128)
        .build()
    );

    /** One preview per container position. */
    private final Map<BlockPos, Preview> previews = new HashMap<>();
    /** The container that is currently being peeked at, -1 when there is none. */
    private int containerId = -1;
    /** The block position the peeking container belongs to. */
    private BlockPos peekPos;
    /** Real (non-peeked) containers opened by clicking a previewed block again. */
    private final Map<Integer, BlockPos> realContainers = new HashMap<>();
    /** The block the player interacted with last. */
    private BlockPos interacted;
    /** When the player interacted with that block. */
    private long interactedAt;

    private final List<ItemStack> visible = new ArrayList<>();
    private final Vector3d renderPos = new Vector3d();

    public ChestPreview() {
        super(Categories.Misc, "chest-preview", "Shows the contents of containers without opening them.");
    }

    @EventHandler
    private void onInteractBlock(InteractBlockEvent event) {
        interacted = event.result.getBlockPos();
        interactedAt = System.currentTimeMillis();
    }

    @EventHandler
    private void onReceivePacket(PacketEvent.Receive event) {
        if (event.packet instanceof ClientboundOpenScreenPacket packet) {
            BlockPos block = interacted;
            if (block == null || mc.level == null) return;

            // the container was clicked after it was previewed, let it open normally
            if (previews.containsKey(block)) {
                realContainers.put(packet.getContainerId(), block);
                return;
            }

            if (!peekable(packet.getType())) return;
            if (System.currentTimeMillis() - interactedAt > 1000) return;

            BlockState state = mc.level.getBlockState(block);
            if (!containers.get().contains(state.getBlock())) return;

            containerId = packet.getContainerId();
            peekPos = block;

            // the screen must not open
            event.cancel();
        }
        else if (event.packet instanceof ClientboundContainerSetContentPacket packet) {
            if (packet.containerId() == containerId && peekPos != null) {
                Preview preview = previews.computeIfAbsent(peekPos, pos -> new Preview());
                preview.items = packet.items();
                containerId = -1;
                peekPos = null;

                // release the container on the server, it was opened for the preview only
                if (mc.getConnection() != null) {
                    mc.getConnection().send(new ServerboundContainerClosePacket(packet.containerId()));
                }

                event.cancel();
            } else {
                BlockPos block = realContainers.remove(packet.containerId());
                // keep the preview up to date with the real contents
                if (block != null) {
                    Preview preview = previews.computeIfAbsent(block, pos -> new Preview());
                    preview.items = packet.items();
                }
            }
        }
    }

    @EventHandler
    private void onKeyInput(KeyInputEvent event) {
        if (event.action == KeyAction.Press && clearBind.get().matches(true, event.key(), event.modifiers())) {
            previews.clear();
        }
    }

    @EventHandler
    private void onMouseClick(MouseClickEvent event) {
        if (event.action != KeyAction.Press || event.button() != 0) return;
        if (mc.gui.screen() != null) return;

        double x = event.click.x();
        double y = event.click.y();

        var iterator = previews.entrySet().iterator();
        while (iterator.hasNext()) {
            Preview preview = iterator.next().getValue();
            if (!preview.buttonValid) continue;

            if (hits(x, y, preview.buttonX, preview.buttonY, preview.buttonSize)
                || hits(x, y, preview.buttonXAlt, preview.buttonYAlt, preview.buttonSizeAlt)) {
                iterator.remove();
                event.cancel();
                return;
            }
        }
    }

    private static boolean hits(double x, double y, double bx, double by, double size) {
        return x >= bx && x <= bx + size && y >= by && y <= by + size;
    }

    @EventHandler
    private void onRender2D(Render2DEvent event) {
        if (mc.level == null) {
            previews.clear();
            return;
        }

        int screenOffset = 0;

        for (Map.Entry<BlockPos, Preview> entry : previews.entrySet()) {
            Preview preview = entry.getValue();

            // drop previews of blocks that are no longer containers (broken, unloaded, ...)
            BlockState state = mc.level.getBlockState(entry.getKey());
            if (!containers.get().contains(state.getBlock())) {
                preview.removed = true;
                continue;
            }

            visible.clear();
            for (ItemStack stack : preview.items) {
                if (stack.isEmpty()) continue;
                if (visible.size() >= maxItems.get()) break;

                visible.add(stack);
            }

            if (visible.isEmpty() && !showEmpty.get()) {
                preview.buttonValid = false;
                continue;
            }

            if (mode.get() == Mode.World) renderWorld(event, entry.getKey(), preview);
            else screenOffset = renderScreen(event, preview, screenOffset);
        }

        previews.values().removeIf(preview -> preview.removed);
    }

    /** Renders the contents above the container. */
    private void renderWorld(Render2DEvent event, BlockPos pos, Preview preview) {
        preview.buttonValid = false;
        if (mc.player == null) return;
        if (mc.player.distanceToSqr(Vec3.atCenterOf(pos)) > (double) maxDistance.get() * maxDistance.get()) return;

        renderPos.set(Vec3.atCenterOf(pos).x, pos.getY() + 1, Vec3.atCenterOf(pos).z);
        if (!NametagUtils.to2D(renderPos, scale.get())) return;

        int columns = Math.max(1, Math.min(9, visible.size()));
        int rows = (visible.size() + columns - 1) / columns;
        double width = columns * 16.0;
        double height = rows * 16.0;

        NametagUtils.begin(renderPos, event.graphics);

        if (visible.isEmpty()) {
            event.graphics.text(mc.font, "Empty", (int) -mc.font.width("Empty") / 2, -8, 0xFFFFFFFF);
            NametagUtils.end(event.graphics);
            return;
        }

        for (int i = 0; i < visible.size(); i++) {
            int x = (int) (-width / 2 + (i % columns) * 16);
            int y = (int) (-height + (i / columns) * 16);

            event.graphics.item(visible.get(i), x, y);
            if (showCount.get()) event.graphics.itemDecorations(mc.font, visible.get(i), x, y, null);
        }

        // delete button, top-right corner of the grid
        int bx = (int) (width / 2 - 8);
        int by = (int) (-height - 8);
        event.graphics.text(mc.font, "×", bx + 1, by, 0xFFFF4444);

        NametagUtils.end(event.graphics);

        // Screen-space rect of the button, stored in both candidate coordinate spaces, since
        // the exact transform of NametagUtils.begin is not verified outside the game.
        preview.buttonX = renderPos.x + bx * NametagUtils.scale;
        preview.buttonY = renderPos.y + by * NametagUtils.scale;
        preview.buttonSize = 8 * NametagUtils.scale;

        double guiScale = mc.getWindow().getGuiScale();
        preview.buttonXAlt = (renderPos.x + bx * NametagUtils.scale) / guiScale;
        preview.buttonYAlt = (renderPos.y + by * NametagUtils.scale) / guiScale;
        preview.buttonSizeAlt = preview.buttonSize / guiScale;

        preview.buttonValid = true;
    }

    /** Renders the contents in panels at the top of the screen. */
    private int renderScreen(Render2DEvent event, Preview preview, int offset) {
        preview.buttonValid = false;
        int columns = Math.min(9, Math.max(visible.size(), 1));
        int rows = (visible.size() + columns - 1) / columns;
        int width = columns * 16;
        int x = (event.screenWidth - width) / 2;
        int y = 4 + offset;

        if (visible.isEmpty()) {
            event.graphics.text(mc.font, "Empty", x, y, 0xFFFFFFFF);
            return offset + 12;
        }

        for (int i = 0; i < visible.size(); i++) {
            int itemX = x + (i % columns) * 16;
            int itemY = y + (i / columns) * 16;

            event.graphics.item(visible.get(i), itemX, itemY);
            if (showCount.get()) event.graphics.itemDecorations(mc.font, visible.get(i), itemX, itemY, null);
        }

        // delete button, top-right corner of the panel
        event.graphics.text(mc.font, "×", x + width - 7, y, 0xFFFF4444);

        preview.buttonX = x + width - 8;
        preview.buttonY = y;
        preview.buttonSize = 8;
        preview.buttonXAlt = -1;
        preview.buttonYAlt = -1;
        preview.buttonSizeAlt = -1;
        preview.buttonValid = true;

        return y + rows * 16 - 4 + 2;
    }

    /** Only the generic container menus are peeked at: single and double chests. */
    private boolean peekable(MenuType<?> type) {
        return type == MenuType.GENERIC_9x3 || type == MenuType.GENERIC_9x6;
    }

    @Override
    public void onDeactivate() {
        // release the container that is still being peeked at
        if (containerId != -1 && mc.getConnection() != null) {
            mc.getConnection().send(new ServerboundContainerClosePacket(containerId));
        }

        containerId = -1;
        peekPos = null;
        realContainers.clear();
        previews.clear();
    }

    @Override
    public String getInfoString() {
        return previews.isEmpty() ? null : Integer.toString(previews.size());
    }

    private static class Preview {
        public List<ItemStack> items = List.of();

        /** Screen-space rect of the delete button, valid for the last rendered frame only. */
        public double buttonX, buttonY, buttonSize;
        /** Same rect in the alternative coordinate space (world mode only). */
        public double buttonXAlt, buttonYAlt, buttonSizeAlt;
        public boolean buttonValid;

        public boolean removed;
    }

    public enum Mode {
        World,
        Screen
    }
}
