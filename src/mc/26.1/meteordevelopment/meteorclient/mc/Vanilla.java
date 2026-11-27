/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.mc;

import com.mojang.authlib.Environment;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.platform.cursor.CursorType;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import it.unimi.dsi.fastutil.ints.IntList;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.mixin.FileCacheAccessor;
import meteordevelopment.meteorclient.mixin.MinecraftAccessor;
import meteordevelopment.meteorclient.mixin.SkinManagerAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.network.chat.LastSeenMessages;
import net.minecraft.network.chat.MessageSignature;
import net.minecraft.network.protocol.game.ClientboundLevelChunkWithLightPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ServerboundChatPacket;
import net.minecraft.network.protocol.game.ServerboundMoveVehiclePacket;
import net.minecraft.network.protocol.game.ServerboundSignUpdatePacket;
import net.minecraft.network.protocol.game.ServerboundUseItemOnPacket;
import net.minecraft.network.protocol.game.ServerboundSwingPacket;
import net.minecraft.util.Util;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.network.chat.LastSeenMessages;
import net.minecraft.network.chat.MessageSignature;
import net.minecraft.network.protocol.game.ClientboundLevelChunkWithLightPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ServerboundChatPacket;
import net.minecraft.network.protocol.game.ServerboundMoveVehiclePacket;
import net.minecraft.network.protocol.game.ServerboundSignUpdatePacket;
import net.minecraft.network.protocol.game.ServerboundUseItemOnPacket;
import net.minecraft.util.Util;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import java.time.Instant;

import static meteordevelopment.meteorclient.MeteorClient.mc;

/**
 * Bridges the API differences between the supported Minecraft versions.
 *
 * <p>This class is version specific, but every method has the same signature and the same
 * behaviour in all of them, so the shared code in {@code src/main/java} can use it without
 * knowing which Minecraft version it is running on. See COMPAT.md.
 */
public final class Vanilla {


    /** Resolves an item by identifier, or null when it does not exist in this version. */
    public static net.minecraft.world.item.Item item(String id) {
        try {
            Class<?> registryClass;
            try { registryClass = Class.forName("net.minecraft.core.registries.BuiltInRegistries"); }
            catch (ClassNotFoundException e) { registryClass = Class.forName("net.minecraft.core.Registry"); }
            Object registry = registryClass.getField("ITEM").get(null);
            java.lang.reflect.Method get = null;
            for (java.lang.reflect.Method m : registry.getClass().getMethods()) {
                if (m.getName().equals("get") && m.getParameterCount() == 1) { get = m; break; }
            }
            Class<?> rlClass;
            try { rlClass = Class.forName("net.minecraft.resources.ResourceLocation"); }
            catch (ClassNotFoundException e) { rlClass = Class.forName("net.minecraft.util.ResourceLocation"); }
            java.lang.reflect.Constructor<?> ctor = null;
            for (java.lang.reflect.Constructor<?> c : rlClass.getDeclaredConstructors()) {
                if (c.getParameterCount() == 1 && c.getParameterTypes()[0] == String.class) { c.setAccessible(true); ctor = c; break; }
            }
            if (get == null || ctor == null) return null;
            String full = id.contains(":") ? id : "minecraft:" + id;
            Object result = get.invoke(registry, ctor.newInstance(full));
            return result instanceof net.minecraft.world.item.Item i ? i : null;
        } catch (Throwable t) { return null; }
    }

    /** Resolves item identifiers to the items that exist in this version (reflective, cross-version). */
    public static java.util.Set<net.minecraft.world.item.Item> resolveItems(java.util.Set<String> ids) {
        java.util.Set<net.minecraft.world.item.Item> out = new java.util.HashSet<>();
        try {
            Class<?> registryClass;
            try {
                registryClass = Class.forName("net.minecraft.core.registries.BuiltInRegistries");
            } catch (ClassNotFoundException e) {
                registryClass = Class.forName("net.minecraft.core.Registry");
            }
            java.lang.reflect.Field field = registryClass.getField("ITEM");
            Object registry = field.get(null);
            java.lang.reflect.Method get = null;
            for (java.lang.reflect.Method m : registry.getClass().getMethods()) {
                if (m.getName().equals("get") && m.getParameterCount() == 1) { get = m; break; }
            }
            Class<?> rlClass;
            try { rlClass = Class.forName("net.minecraft.resources.ResourceLocation"); }
            catch (ClassNotFoundException e) { rlClass = Class.forName("net.minecraft.util.ResourceLocation"); }
            java.lang.reflect.Constructor<?> ctor = null;
            for (java.lang.reflect.Constructor<?> c : rlClass.getConstructors()) {
                if (c.getParameterCount() == 1 && c.getParameterTypes()[0] == String.class) { ctor = c; break; }
            }
            if (ctor == null) {
                for (java.lang.reflect.Constructor<?> c : rlClass.getDeclaredConstructors()) {
                    if (c.getParameterCount() == 1 && c.getParameterTypes()[0] == String.class) { c.setAccessible(true); ctor = c; break; }
                }
            }
            if (get == null || ctor == null) return out;
            for (String id : ids) {
                String full = id.contains(":") ? id : "minecraft:" + id;
                try {
                    Object rl = ctor.newInstance(full);
                    Object item = get.invoke(registry, rl);
                    if (item instanceof net.minecraft.world.item.Item i) out.add(i);
                } catch (Exception ignored) { }
            }
        } catch (Exception ignored) { }
        return out;
    }


    /** Mace smash damage bonus. Returns 0 in versions without the mace. */
    public static float maceDamageBonus(net.minecraft.world.entity.Entity target, float damage, net.minecraft.world.damagesource.DamageSource source) {
        return 0f;
    }

    /** True if the stack is a mace (the item only exists in newer versions). */
    public static boolean isMace(net.minecraft.world.item.ItemStack stack) {
        if (stack == null || stack.isEmpty()) return false;
        String id = typeId(stack.getItem());
        return id.equals("minecraft:mace") || id.equals("mace");
    }

    /** True if the stack is a trident. */
    public static boolean isTrident(net.minecraft.world.item.ItemStack stack) {
        if (stack == null || stack.isEmpty()) return false;
        String id = typeId(stack.getItem());
        return id.equals("minecraft:trident") || id.equals("trident");
    }

    /**
     * Resolves a Minecraft object (entity type, item, block entity type, item stack, ...) to its
     * namespaced identifier string. Uses reflection so it works with any version's registry API.
     */
    public static String typeId(Object value) {
        if (value == null) return "";

        try {
            Class<?> registryClass = null;
            Object registry = null;

            // Modern versions: net.minecraft.core.registries.BuiltInRegistries
            try {
                registryClass = Class.forName("net.minecraft.core.registries.BuiltInRegistries");
            } catch (ClassNotFoundException ignored) {
                // Older versions: net.minecraft.core.Registry with static fields
                registryClass = Class.forName("net.minecraft.core.Registry");
            }

            // Find the right registry field by trying common names and checking the value type.
            String[] fieldNames = { "ITEM", "ENTITY_TYPE", "BLOCK_ENTITY_TYPE" };
            for (String fieldName : fieldNames) {
                try {
                    java.lang.reflect.Field field = registryClass.getField(fieldName);
                    Object candidate = field.get(null);
                    if (candidate != null) {
                        java.lang.reflect.Method getKey = registryClass.getMethod("getKey", Object.class);
                        Object key = getKey.invoke(candidate, value);
                        if (key != null) return key.toString();
                    }
                } catch (ReflectiveOperationException ignored) {
                }
            }
        } catch (Exception ignored) {
        }

        return "";
    }

    private Vanilla() {}

    // Player actions

    public static void swing(Player player, InteractionHand hand) {
        player.swing(hand);
    }

    /** Swings for a real server-side player (singleplayer clone): broadcast to trackers happens inside swing(). */
    public static void swingClone(net.minecraft.server.level.ServerPlayer player) {
        player.swing(InteractionHand.MAIN_HAND);
    }

    /** Whether the player is mid-swing (the swing state API differs between versions). */
    public static boolean isSwinging(Player player) {
        return player.swinging;
    }

    // Blocks

    public static BlockPos copy(BlockPos pos) {
        return new BlockPos(pos);
    }

    public static boolean bedDestroysOnUse() {
        return true;
    }

    // Collision

    public static boolean blocksMotion(net.minecraft.world.level.block.state.BlockState state) {
        return state.blocksMotion();
    }

    public static java.util.Collection<net.minecraft.world.phys.EntityHitResult> getManyEntityHitResult(
        net.minecraft.world.level.Level level,
        net.minecraft.world.entity.Entity entity,
        net.minecraft.world.phys.Vec3 from,
        net.minecraft.world.phys.Vec3 to,
        net.minecraft.world.phys.AABB box,
        java.util.function.Predicate<net.minecraft.world.entity.Entity> predicate,
        float tolerance,
        net.minecraft.world.level.ClipContext.Block block,
        boolean onlyBlocks,
        boolean extra
    ) {
        for (java.lang.reflect.Method m : net.minecraft.world.entity.projectile.ProjectileUtil.class.getMethods()) {
            if (!m.getName().equals("getManyEntityHitResult")) continue;
            Class<?>[] ps = m.getParameterTypes();
            Object[] args = switch (ps.length) {
                case 7 -> new Object[]{level, entity, from, to, box, predicate, extra};
                case 8 -> new Object[]{level, entity, from, to, box, predicate, onlyBlocks, extra};
                case 9 -> new Object[]{level, entity, from, to, box, predicate, tolerance, block, onlyBlocks};
                case 10 -> new Object[]{level, entity, from, to, box, predicate, tolerance, block, onlyBlocks, extra};
                default -> null;
            };
            if (args != null) {
                try { return (java.util.Collection<net.minecraft.world.phys.EntityHitResult>) m.invoke(null, args); }
                catch (Exception ignored) {}
            }
        }
        return java.util.Collections.emptyList();
    }

    public static java.util.List<net.minecraft.client.resources.model.geometry.BakedQuad> quads(
        net.minecraft.client.renderer.item.ItemStackRenderState.LayerRenderState layer
    ) {
        return layer.prepareQuadList();
    }

    // Registries

    public static HolderLookup.Provider worldLookup() {
        return VanillaRegistries.createLookup();
    }

    /**
     * A swing the server is told about.
     *
     * <p>26.2 could send a swing packet; 26.3 removed the packet, so the closest equivalent
     * is the local swing.
     */
    public static void swingPacket(net.minecraft.client.player.LocalPlayer player, net.minecraft.world.InteractionHand hand) {
        mc.getConnection().send(new net.minecraft.network.protocol.game.ServerboundSwingPacket(hand));
    }

    public static void dropMainHand(net.minecraft.client.player.LocalPlayer player) {
        player.drop(true);
    }

    public static void registerSwingPacket(com.google.common.collect.ImmutableMap.Builder<String, net.minecraft.network.protocol.PacketType<? extends net.minecraft.network.protocol.Packet<?>>> builder) {
        builder.put("ServerboundSwingPacket", net.minecraft.network.protocol.game.GamePacketTypes.SERVERBOUND_SWING);
    }

    // Items

    public static boolean isFuel(Item item) {
        return mc.getConnection().fuelValues().fuelItems().contains(item);
    }

    // Input

    public static final InputConstants.Type KEY_TYPE = InputConstants.Type.KEYSYM;

    public static final int SUPER_KEY_LEFT = InputConstants.KEY_LSUPER;

    public static final int SUPER_KEY_RIGHT = InputConstants.KEY_RSUPER;

    public static int scancode(net.minecraft.client.input.KeyEvent event) {
        return event.scancode();
    }

    /** 26.2 text input API takes no owner. */
    public static void startTextInput(Object owner) {
        mc.textInputManager().startTextInput();
    }

    /** 26.2 text input API takes no owner. */
    public static void stopTextInput(Object owner) {
        mc.textInputManager().stopTextInput();
    }

    public static void selectCursor(com.mojang.blaze3d.platform.cursor.CursorType cursor) {
        cursor.select(mc.getWindow());
    }

    public static void releaseMouse(Window window, double x, double y) {
        InputConstants.grabOrReleaseMouse(window, InputConstants.CURSOR_NORMAL, x, y);
    }

    // Transforms

    public static void rotate(com.mojang.blaze3d.vertex.PoseStack poseStack, com.mojang.math.Axis axis, float degrees) {
        poseStack.mulPose(axis.rotationDegrees(degrees));
    }

    // Entities

    public static boolean isEnderman(net.minecraft.world.entity.Entity entity) {
        return entity instanceof net.minecraft.world.entity.monster.EnderMan;
    }

    public static boolean isCreepy(net.minecraft.world.entity.Entity entity) {
        return ((net.minecraft.world.entity.monster.EnderMan) entity).isCreepy();
    }

    public static void tooltip(net.minecraft.client.gui.GuiGraphicsExtractor graphics,
                               net.minecraft.client.gui.Font font,
                               java.util.List<net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent> components,
                               int x, int y, net.minecraft.world.item.ItemStack stack) {
        graphics.tooltip(font, components, x, y, net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner.INSTANCE, stack.get(net.minecraft.core.component.DataComponents.TOOLTIP_STYLE));
    }

    public static boolean isSameFluid(net.minecraft.world.level.material.Fluid a, net.minecraft.world.level.material.Fluid b) {
        return a.isSame(b);
    }

    public static net.minecraft.client.multiplayer.ClientLevel createClientLevel(
        net.minecraft.client.multiplayer.ClientPacketListener listener,
        net.minecraft.client.multiplayer.ClientLevel.ClientLevelData data,
        net.minecraft.resources.ResourceKey<net.minecraft.world.level.Level> dimension,
        net.minecraft.core.Holder<net.minecraft.world.level.dimension.DimensionType> dimensionType,
        int x, int z,
        net.minecraft.client.renderer.LevelRenderer extractor,
        boolean isClientSide,
        int seaLevel
    ) {
        return new net.minecraft.client.multiplayer.ClientLevel(listener, data, dimension, dimensionType, x, z, extractor, isClientSide, 0L, seaLevel);
    }

    // Textures

    public static meteordevelopment.meteorclient.renderer.McTexture createTexture(java.util.function.Supplier<String> name, com.mojang.blaze3d.platform.NativeImage image) {
        return new meteordevelopment.meteorclient.renderer.McTexture(new net.minecraft.client.renderer.texture.DynamicTexture(name, image));
    }

    public static void registerTexture(net.minecraft.resources.Identifier id, meteordevelopment.meteorclient.renderer.McTexture texture) {
        mc.getTextureManager().register(id, (net.minecraft.client.renderer.texture.DynamicTexture) texture.handle());
    }

    public static void renderTexture(meteordevelopment.meteorclient.renderer.Renderer2D renderer, meteordevelopment.meteorclient.renderer.McTexture texture) {
        net.minecraft.client.renderer.texture.AbstractTexture t = (net.minecraft.client.renderer.texture.AbstractTexture) texture.handle();
        renderer.render(t.getTextureView(), t.getSampler());
    }

    public static int dyeDiffuseColor(net.minecraft.world.item.DyeColor dye) {
        return dye.getTextureDiffuseColor();
    }

    public static java.util.List<net.minecraft.world.item.ItemStack> containerItems(net.minecraft.core.component.DataComponentMap components) {
        return components.get(net.minecraft.core.component.DataComponents.CONTAINER).allItemsCopyStream().toList();
    }

    public static com.mojang.blaze3d.platform.NativeImage mapImage(
        meteordevelopment.meteorclient.mixin.MapTextureManagerAccessor accessor,
        net.minecraft.world.level.saveddata.maps.MapId id,
        net.minecraft.world.level.saveddata.maps.MapItemSavedData state
    ) {
        return accessor.meteor$invokeGetOrCreateMapInstance(id, state).texture.getPixels();
    }

    /** 26.2 and 26.3 hand out the image of the map texture, which owns it. */
    public static void closeMapImage(com.mojang.blaze3d.platform.NativeImage image) {
    }

    // Keyboard

    public static final int MENU_KEY = org.lwjgl.glfw.GLFW.GLFW_KEY_MENU;

    public static final int WORLD_2_KEY = org.lwjgl.glfw.GLFW.GLFW_KEY_WORLD_2;

    public static boolean matchesKey(int input, int scancode, boolean key, com.mojang.blaze3d.platform.InputConstants.Key inputKey) {
        boolean isKey = inputKey.getType() != com.mojang.blaze3d.platform.InputConstants.Type.MOUSE;
        return isKey == key && inputKey.getType() == com.mojang.blaze3d.platform.InputConstants.Type.SCANCODE ? scancode == inputKey.getValue() : input == inputKey.getValue();
    }

    public static boolean isKeyDown(int key) {
        return com.mojang.blaze3d.platform.InputConstants.isKeyDown(mc.getWindow(), key);
    }

    public static boolean isMouseButtonKey(int key) {
        return key >= 0 && key < 8;
    }

    public static boolean isMouseButtonPressed(int button) {
        return org.lwjgl.glfw.GLFW.glfwGetMouseButton(mc.getWindow().handle(), button) != com.mojang.blaze3d.platform.InputConstants.RELEASE;
    }

    public static net.minecraft.client.resources.model.geometry.BakedQuad.MaterialInfo translucentMaterialInfo(
        net.minecraft.client.resources.model.geometry.BakedQuad.MaterialInfo info
    ) {
        return new net.minecraft.client.resources.model.geometry.BakedQuad.MaterialInfo(
            info.sprite(),
            net.minecraft.client.renderer.chunk.ChunkSectionLayer.TRANSLUCENT,
            info.itemRenderType(),
            info.tintIndex(),
            info.shade(),
            info.lightEmission()
        );
    }

    public static <S> void submitBannerPatterns(
        net.minecraft.client.resources.model.sprite.SpriteGetter sprites,
        com.mojang.blaze3d.vertex.PoseStack poseStack,
        net.minecraft.client.renderer.SubmitNodeCollector collector,
        int light, int overlay,
        net.minecraft.client.model.Model<S> model, S state,
        boolean renderOverlay,
        net.minecraft.world.item.DyeColor color,
        net.minecraft.world.level.block.entity.BannerPatternLayers patterns
    ) {
        net.minecraft.client.renderer.blockentity.BannerRenderer.submitPatterns(sprites, poseStack, collector, light, overlay, model, state, renderOverlay, color, patterns, null);
    }

    public static <S> void submitBannerModel(
        net.minecraft.client.renderer.SubmitNodeCollector collector,
        net.minecraft.client.model.Model<? super S> model, S state,
        com.mojang.blaze3d.vertex.PoseStack poseStack,
        int light, int overlay, int outlineColor,
        net.minecraft.client.resources.model.sprite.SpriteId sprite,
        net.minecraft.client.resources.model.sprite.SpriteGetter sprites,
        int color,
        Object crumblingOverlay
    ) {
        collector.submitModel(model, state, poseStack, light, overlay, outlineColor, sprite, sprites, color, (net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay) crumblingOverlay);
    }

    /**
     * 26.2 still had a swing packet, so a critical had to hold it back together with the attack.
     */
    public static boolean requiresSwingPacket() {
        return true;
    }

    public static boolean isSwingPacket(Object packet) {
        return packet instanceof net.minecraft.network.protocol.game.ServerboundSwingPacket;
    }

    public static void sendSwingPacket(Object packet) {
        mc.getConnection().send((net.minecraft.network.protocol.game.ServerboundSwingPacket) packet);
    }

    // Authentication (authlib 9)

    public static Object createAuthService() {
        return new com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService(mc.getProxy());
    }

    public static void applyLoginEnvironment(Object authService) {
        com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService service =
            (com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService) authService;
        meteordevelopment.meteorclient.mixin.MinecraftAccessor mca = (meteordevelopment.meteorclient.mixin.MinecraftAccessor) mc;
        net.minecraft.util.SignatureValidator.from(service.getServicesKeySet(), com.mojang.authlib.yggdrasil.ServicesKeyType.PROFILE_KEY);
        net.minecraft.client.resources.SkinManager.TextureCache skinCache =
            ((meteordevelopment.meteorclient.mixin.SkinManagerAccessor) mc.getSkinManager()).meteor$getSkinTextures();
        java.nio.file.Path skinCachePath =
            ((meteordevelopment.meteorclient.mixin.FileCacheAccessor) skinCache).meteor$getRoot();
        mca.meteor$setServices(net.minecraft.server.Services.create(service, mc.gameDirectory));
        mca.meteor$setSkinManager(new net.minecraft.client.resources.SkinManager(skinCachePath, mc.services(),
            new net.minecraft.client.renderer.texture.SkinTextureDownloader(mc.getProxy(), mc.getTextureManager(), mc), mc));
    }

    public static void setSession(net.minecraft.client.User session) {
        meteordevelopment.meteorclient.mixin.MinecraftAccessor mca = (meteordevelopment.meteorclient.mixin.MinecraftAccessor) mc;
        mca.meteor$setUser(session);

        com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService service =
            new com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService(mc.getProxy());

        com.mojang.authlib.minecraft.UserApiService apiService = service.createUserApiService(session.getAccessToken());
        mca.meteor$setUserApiService(apiService);
        mca.meteor$setProfileKeyPairManager(net.minecraft.client.multiplayer.ProfileKeyPairManager.create(apiService, session, mc.gameDirectory.toPath()));
        mca.meteor$setReportingContext(net.minecraft.client.multiplayer.chat.report.ReportingContext.create(net.minecraft.client.multiplayer.chat.report.ReportEnvironment.local(), apiService));
        mca.meteor$setProfileFuture(java.util.concurrent.CompletableFuture.supplyAsync(
            () -> mc.services().sessionService().fetchProfile(mc.getUser().getProfileId(), true), net.minecraft.util.Util.ioPool()));
    }

    public static Object createAlteningEnvironment() {
        return new com.mojang.authlib.Environment("https://sessionserver.thealtening.com", "https://authserver.thealtening.com", "https://api.mojang.com", "The Altening");
    }

    public static Object createAlteningService(Object environment) {
        return new com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService(mc.getProxy(), (com.mojang.authlib.Environment) environment);
    }

    public static String alteningAuthUrl(Object environment) {
        return ((com.mojang.authlib.Environment) environment).servicesHost() + "/authenticate";
    }

    // Packets

    public static ServerboundChatPacket chat(String message, Instant instant, long seen, MessageSignature signature, LastSeenMessages.Update update) {
        return new ServerboundChatPacket(message, instant, seen, signature, update);
    }

    public static ServerboundSignUpdatePacket signUpdate(BlockPos pos, String[] lines) {
        return new ServerboundSignUpdatePacket(pos, true, lines[0], lines[1], lines[2], lines[3]);
    }

    public static String[] signLines(ServerboundSignUpdatePacket packet) {
        return packet.getLines();
    }

    public static int chunkX(ClientboundLevelChunkWithLightPacket packet) {
        return packet.getX();
    }

    public static int chunkZ(ClientboundLevelChunkWithLightPacket packet) {
        return packet.getZ();
    }

    public static IntList removeEntityIds(ClientboundRemoveEntitiesPacket packet) {
        return packet.getEntityIds();
    }

    public static Vec3 movingPosition(ServerboundMoveVehiclePacket packet) {
        return packet.position();
    }

    public static BlockHitResult hitResult(ServerboundUseItemOnPacket packet) {
        return packet.getHitResult();
    }

    // Misc

    public static void openUri(String url) {
        Util.getPlatform().openUri(java.net.URI.create(url));
    }
    // Gui / screens (26.1 keeps screen management on Minecraft itself)

    public static net.minecraft.client.gui.screens.Screen screen() {
        return mc.screen;
    }

    public static void setScreen(net.minecraft.client.gui.screens.Screen screen) {
        mc.setScreen(screen);
    }

    public static net.minecraft.client.gui.screens.Overlay overlay() {
        return mc.getOverlay();
    }

    public static net.minecraft.client.gui.components.toasts.ToastManager toastManager() {
        return mc.getToastManager();
    }

    public static net.minecraft.client.gui.components.ChatComponent chatComponent() {
        return mc.gui.getChat();
    }

    // Game renderer

    public static com.mojang.blaze3d.pipeline.RenderTarget mainRenderTarget() {
        return mc.getMainRenderTarget();
    }

    public static net.minecraft.client.renderer.state.GameRenderState gameRenderState() {
        return mc.gameRenderer.getGameRenderState();
    }

    public static net.minecraft.client.Camera mainCamera() {
        return mc.gameRenderer.getMainCamera();
    }

    public static com.mojang.blaze3d.platform.Lighting lighting() {
        return mc.gameRenderer.getLighting();
    }

    // Items

    public static boolean isShulker(Item item) {
        if (item == net.minecraft.world.item.Items.SHULKER_BOX) return true;
        if (item == net.minecraft.world.item.Items.WHITE_SHULKER_BOX) return true;
        if (item == net.minecraft.world.item.Items.ORANGE_SHULKER_BOX) return true;
        if (item == net.minecraft.world.item.Items.MAGENTA_SHULKER_BOX) return true;
        if (item == net.minecraft.world.item.Items.LIGHT_BLUE_SHULKER_BOX) return true;
        if (item == net.minecraft.world.item.Items.YELLOW_SHULKER_BOX) return true;
        if (item == net.minecraft.world.item.Items.LIME_SHULKER_BOX) return true;
        if (item == net.minecraft.world.item.Items.PINK_SHULKER_BOX) return true;
        if (item == net.minecraft.world.item.Items.GRAY_SHULKER_BOX) return true;
        if (item == net.minecraft.world.item.Items.LIGHT_GRAY_SHULKER_BOX) return true;
        if (item == net.minecraft.world.item.Items.CYAN_SHULKER_BOX) return true;
        if (item == net.minecraft.world.item.Items.PURPLE_SHULKER_BOX) return true;
        if (item == net.minecraft.world.item.Items.BLUE_SHULKER_BOX) return true;
        if (item == net.minecraft.world.item.Items.BROWN_SHULKER_BOX) return true;
        if (item == net.minecraft.world.item.Items.GREEN_SHULKER_BOX) return true;
        if (item == net.minecraft.world.item.Items.RED_SHULKER_BOX) return true;
        if (item == net.minecraft.world.item.Items.BLACK_SHULKER_BOX) return true;
        return false;
    }

    // Packets (26.1 has no spectator action packet)

    public static void registerSpectatorActionPacket(com.google.common.collect.ImmutableMap.Builder<String, net.minecraft.network.protocol.PacketType<? extends net.minecraft.network.protocol.Packet<?>>> builder) {
    }
    // Chat

    public static net.minecraft.client.multiplayer.chat.ChatListener chatListener() {
        return mc.getChatListener();
    }

    // Block breaking progress

    public static void loadFromBucketTag(net.minecraft.world.entity.LivingEntity entity, net.minecraft.nbt.CompoundTag tag) {
        ((net.minecraft.world.entity.animal.Bucketable) entity).loadFromBucketTag(tag);
    }

    // Chunk rebuilds

    public static void allChanged() {
        mc.levelRenderer.allChanged();
    }

    public static it.unimi.dsi.fastutil.longs.Long2ObjectMap<java.util.SortedSet<net.minecraft.server.level.BlockDestructionProgress>> destructionProgress() {
        return ((meteordevelopment.meteorclient.mixininterface.ILevelRenderer) (Object) mc.levelRenderer).meteor$destructionProgress();
    }


    // Weathered copper lookups (26.1 keeps the plain block constants)

    public static net.minecraft.world.level.block.Block oxidizedCopper() {
        return net.minecraft.world.level.block.Blocks.OXIDIZED_COPPER;
    }

    public static net.minecraft.world.level.block.Block oxidizedCutCopper() {
        return net.minecraft.world.level.block.Blocks.OXIDIZED_CUT_COPPER;
    }

    public static net.minecraft.world.level.block.Block waxedUnaffectedCopper() {
        return net.minecraft.world.level.block.Blocks.WAXED_COPPER_BLOCK;
    }

    public static net.minecraft.world.level.block.Block waxedOxidizedCopper() {
        return net.minecraft.world.level.block.Blocks.WAXED_OXIDIZED_COPPER;
    }

    public static net.minecraft.world.level.block.Block waxedOxidizedCutCopper() {
        return net.minecraft.world.level.block.Blocks.WAXED_OXIDIZED_CUT_COPPER;
    }

    public static net.minecraft.world.level.block.Block waxedUnaffectedCopperBulb() {
        return net.minecraft.world.level.block.Blocks.WAXED_COPPER_BULB;
    }

    // Banner sprite getter (uses reflection like 26.2)

    public static net.minecraft.client.resources.model.sprite.SpriteGetter bannerSpriteGetter() {
        try {
            var mc = net.minecraft.client.Minecraft.getInstance();
            var modelManager = mc.getModelManager();
            var method = modelManager.getClass().getMethod("getBannerSpriteGetter");
            return (net.minecraft.client.resources.model.sprite.SpriteGetter) method.invoke(modelManager);
        } catch (Exception ex) {
            throw new RuntimeException("Failed to get banner sprite getter", ex);
        }
    }

    // Atlas manager (uses reflection like 26.2)

    public static Object atlasManager() {
        try {
            var mc = net.minecraft.client.Minecraft.getInstance();
            var modelManager = mc.getModelManager();
            var method = modelManager.getClass().getMethod("getAtlasManager");
            return method.invoke(modelManager);
        } catch (Exception ex) {
            throw new RuntimeException("Failed to get atlas manager", ex);
        }
    }

    /** Universal registry-key identifier accessor (26.x uses identifier(), older versions location()). */
    public static net.minecraft.resources.Identifier identifier(net.minecraft.resources.ResourceKey<?> key) {
        Object location;
        try {
            location = key.getClass().getMethod("identifier").invoke(key);
        } catch (ReflectiveOperationException e) {
            try {
                location = key.getClass().getMethod("location").invoke(key);
            } catch (ReflectiveOperationException e2) {
                throw new RuntimeException("Cannot resolve identifier for " + key, e2);
            }
        }
        return net.minecraft.resources.Identifier.parse(location.toString());
    }

    /** Universal container-click handler (26.x uses ContainerInput, older versions ClickType). */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void handleContainerInput(int containerId, int slot, int button, Object type, net.minecraft.world.entity.player.Player player) {
        Object clickType = type;
        if (type != null && type.getClass().getName().equals("net.minecraft.world.inventory.ContainerInput")) {
            try {
                Class<?> enumClass = Class.forName("net.minecraft.world.inventory.ClickType");
                clickType = Enum.valueOf((Class<? extends Enum>) enumClass, ((Enum<?>) type).name());
            } catch (Exception e) {
                throw new RuntimeException("Cannot translate container input type", e);
            }
        }
        try {
            Class<?> typeClass = clickType == null ? Class.forName("net.minecraft.world.inventory.ClickType") : clickType.getClass();
            mc.gameMode.getClass().getMethod("handleContainerInput", int.class, int.class, int.class, typeClass, net.minecraft.world.entity.player.Player.class)
                .invoke(mc.gameMode, containerId, slot, button, clickType, player);
        } catch (Exception e) {
            throw new RuntimeException("Cannot handle container input", e);
        }
    }

    // Legacy modifier masks (26.x keeps these in InputConstants).
    public static final int MOD_SHIFT = 1;
    public static final int MOD_CONTROL = 2;
    public static final int MOD_ALT = 4;
    public static final int MOD_SUPER = 8;
    public static final int MOD_CAPS_LOCK = 16;
    public static final int MOD_NUM_LOCK = 32;

    // Universal accessors for fields that were renamed to methods in 26.x.
    public static String gameProfileName(com.mojang.authlib.GameProfile profile) {
        try { return (String) profile.getClass().getMethod("name").invoke(profile); }
        catch (ReflectiveOperationException e) {
            try { return (String) profile.getClass().getMethod("name").invoke(profile); }
            catch (Exception ignored) { return null; }
        }
    }

    public static java.util.UUID gameProfileId(com.mojang.authlib.GameProfile profile) {
        try { return (java.util.UUID) profile.getClass().getMethod("id").invoke(profile); }
        catch (ReflectiveOperationException e) {
            try { return (java.util.UUID) profile.getClass().getMethod("id").invoke(profile); }
            catch (Exception ignored) { return null; }
        }
    }

    public static int chunkX(net.minecraft.world.level.ChunkPos pos) { return chunkCoord(pos, "x"); }
    public static int chunkZ(net.minecraft.world.level.ChunkPos pos) { return chunkCoord(pos, "z"); }
    public static long chunkKey(int x, int z) { return (long) x << 32 | z & 0xffffffffL; }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static net.minecraft.core.Holder<net.minecraft.world.entity.EntityType<?>> typeHolder(net.minecraft.world.entity.Entity entity) {
        try { return (net.minecraft.core.Holder<net.minecraft.world.entity.EntityType<?>>) entity.getClass().getMethod("typeHolder").invoke(entity); }
        catch (ReflectiveOperationException e) {
            return ((net.minecraft.world.entity.EntityType) entity.getType()).builtInRegistryHolder();
        }
    }

    /** Universal key/mouse matching for the 26.x input event API. */


    /** Universal entity interaction (26.x takes a hit result). */

    /** Universal difficulty/permission helpers for APIs that moved in 26.x. */
    public static float moonBrightness() {
        try {
            java.lang.reflect.Field f = net.minecraft.world.level.dimension.DimensionType.class.getField("MOON_BRIGHTNESS_PER_PHASE");
            float[] values = (float[]) f.get(null);
            return values[0];
        } catch (Exception e) {
            return 0.2f;
        }
    }

    public static String permissionLevelString() {
        try {
            Object permissions = mc.player.getClass().getMethod("permissions").invoke(mc.player);
            for (String name : new String[] { "COMMANDS_OWNER", "COMMANDS_ADMIN", "COMMANDS_GAMEMASTER", "COMMANDS_MODERATOR" }) {
                try {
                    Object key = Class.forName("net.minecraft.server.permissions.Permissions").getField(name).get(null);
                    if ((boolean) permissions.getClass().getMethod("hasPermission", key.getClass()).invoke(permissions, key)) {
                        return switch (name) {
                            case "COMMANDS_OWNER" -> "4 (Owner)";
                            case "COMMANDS_ADMIN" -> "3 (Admin)";
                            case "COMMANDS_GAMEMASTER" -> "2 (Gamemaster)";
                            default -> "1 (Moderator)";
                        };
                    }
                } catch (ReflectiveOperationException ignored) {}
            }
        } catch (ReflectiveOperationException ignored) {}
        return "unknown";
    }

    /** Universal bundle / banner component accessors for 26.x data component changes. */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static java.util.List<net.minecraft.world.item.ItemStack> bundleItems(net.minecraft.world.item.component.BundleContents contents) {
        try {
            Object items = contents.getClass().getMethod("items").invoke(contents);
            java.util.List<net.minecraft.world.item.ItemStack> result = new java.util.ArrayList<>();
            for (Object item : (Iterable<Object>) items) {
                if (item instanceof net.minecraft.world.item.ItemStack stack) result.add(stack);
                else result.add((net.minecraft.world.item.ItemStack) item.getClass().getMethod("create").invoke(item));
            }
            return result;
        } catch (Exception e) {
            return java.util.Collections.emptyList();
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static net.minecraft.world.level.block.entity.BannerPatternLayers providedBannerPatterns(net.minecraft.world.item.ItemStack stack) {
        try {
            net.minecraft.core.component.DataComponentType<Object> type =
                (net.minecraft.core.component.DataComponentType<Object>) (Object) net.minecraft.core.component.DataComponents.PROVIDES_BANNER_PATTERNS;
            Object provided = stack.get(type);
            if (provided instanceof java.util.Collection<?> c && !c.isEmpty()) {
                Object first = c.iterator().next();
                return new net.minecraft.world.level.block.entity.BannerPatternLayers.Builder()
                    .add((net.minecraft.core.Holder) first, net.minecraft.world.item.DyeColor.WHITE).build();
            }
        } catch (Exception ignored) {}
        return net.minecraft.world.level.block.entity.BannerPatternLayers.EMPTY;
    }

    /** Universal identifier conversion from legacy ResourceLocation. */
    public static net.minecraft.resources.Identifier identifier(Object location) {
        return net.minecraft.resources.Identifier.parse(location.toString());
    }

    public static java.util.List<net.minecraft.resources.Identifier> identifiers(java.util.Collection<?> locations) {
        java.util.List<net.minecraft.resources.Identifier> result = new java.util.ArrayList<>();
        for (Object location : locations) result.add(identifier(location));
        return result;
    }

    /** Universal camera rotation accessors. */
    public static float cameraYRot(Object camera) {
        try { return (float) camera.getClass().getMethod("yRot").invoke(camera); }
        catch (ReflectiveOperationException e) {
            try { return (float) camera.getClass().getMethod("getYRot").invoke(camera); }
            catch (Exception ignored) { return 0f; }
        }
        catch (Exception e) { return 0f; }
    }

    public static float cameraXRot(Object camera) {
        try { return (float) camera.getClass().getMethod("xRot").invoke(camera); }
        catch (ReflectiveOperationException e) {
            try { return (float) camera.getClass().getMethod("getXRot").invoke(camera); }
            catch (Exception ignored) { return 0f; }
        }
        catch (Exception e) { return 0f; }
    }

    /** Universal spear tag check (ItemTags.SPEARS only exists on 26.x). */
    public static boolean isSpear(net.minecraft.world.item.ItemStack stack) {
        try {
            Object tag = net.minecraft.tags.ItemTags.class.getField("SPEARS").get(null);
            if (stack.is((net.minecraft.tags.TagKey<net.minecraft.world.item.Item>) tag)) return true;
        } catch (Exception ignored) {}
        Object key = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(stack.getItem());
        return key != null && identifier(key).getPath().endsWith("_spear");
    }

    /** Universal screen lifecycle helpers for renamed 26.x methods. */
    public static void screenResize(Object screen, int width, int height) {
        try { screen.getClass().getMethod("resize", net.minecraft.client.Minecraft.class, int.class, int.class).invoke(screen, mc, width, height); }
        catch (Exception e) {
            try { screen.getClass().getMethod("resize", int.class, int.class).invoke(screen, width, height); }
            catch (Exception ignored) {}
        }
    }

    public static void screenExtractRenderState(Object screen, Object graphics, int mouseX, int mouseY, float delta) {
        try {
            screen.getClass().getMethod("extractRenderState", graphics.getClass(), int.class, int.class, float.class)
                .invoke(screen, graphics, mouseX, mouseY, delta);
        } catch (Exception ignored) {}
    }

    /** Universal key mapping creation (26.x adds a Category argument). */
    public static net.minecraft.client.KeyMapping createKeyMapping(String name, Object type, int code) {
        try {
            Class<?> categoryClass = Class.forName("net.minecraft.client.KeyMapping$Category");
            Object category = categoryClass.getMethod("register", net.minecraft.resources.Identifier.class)
                .invoke(null, net.minecraft.resources.Identifier.parse("meteor-client"));
            return net.minecraft.client.KeyMapping.class.getConstructor(String.class, int.class, int.class, categoryClass)
                .newInstance(name, type, code, category);
        } catch (Exception e) {
            for (java.lang.reflect.Constructor<?> c : net.minecraft.client.KeyMapping.class.getConstructors()) {
                Class<?>[] ps = c.getParameterTypes();
                if (ps.length == 3 && ps[0] == String.class && ps[2] == int.class && ps[1].isInstance(type)) {
                    try { return (net.minecraft.client.KeyMapping) c.newInstance(name, type, code); }
                    catch (Exception ignored) { return null; }
                }
                if (ps.length == 4 && ps[0] == String.class && ps[3] == int.class) {
                    try { return (net.minecraft.client.KeyMapping) c.newInstance(name, ps[1].getEnumConstants()[0], code, ps[2].getEnumConstants()[0]); }
                    catch (Exception ignored) { return null; }
                }
            }
            return null;
        }
    }

    /** Universal camera access across versions. */
    public static net.minecraft.client.Camera camera() {
        net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
        if (mc.player != null) {
            try {
                return (net.minecraft.client.Camera) net.minecraft.client.player.LocalPlayer.class
                    .getMethod("getCamera").invoke(mc.player);
            } catch (Exception ignored) {}
        }
        try {
            return (net.minecraft.client.Camera) net.minecraft.client.renderer.GameRenderer.class
                .getMethod("getCamera").invoke(mc.gameRenderer);
        } catch (Exception e) {
            return new net.minecraft.client.Camera();
        }
    }

    /** Universal projection matrix access across versions. */
    public static org.joml.Matrix4f projectionMatrix() {
        try {
            return (org.joml.Matrix4f) com.mojang.blaze3d.systems.RenderSystem.class
                .getMethod("getProjectionMatrix").invoke(null);
        } catch (Exception e) {
            return new org.joml.Matrix4f();
        }
    }

    /** Universal reflection helpers for APIs that moved across versions. */
    public static byte[] fastByteArray(Object stream) {
        try {
            java.io.ByteArrayOutputStream out = (java.io.ByteArrayOutputStream) stream;
            return out.toByteArray();
        } catch (Exception e) {
            try {
                java.lang.reflect.Method m = stream.getClass().getMethod("toByteArray");
                return (byte[]) m.invoke(stream);
            } catch (Exception ignored) {
                return new byte[0];
            }
        }
    }

    public static net.minecraft.client.User createUser(String name, java.util.UUID uuid, String token) {
        for (java.lang.reflect.Constructor<?> c : net.minecraft.client.User.class.getConstructors()) {
            Class<?>[] p = c.getParameterTypes();
            try {
                if (p.length == 3) return (net.minecraft.client.User) c.newInstance(name, uuid, token);
                if (p.length == 6) {
                    Object type = p[5].getEnumConstants()[0];
                    return (net.minecraft.client.User) c.newInstance(name, uuid, token,
                        java.util.Optional.empty(), java.util.Optional.empty(), type);
                }
            } catch (Exception ignored) {}
        }
        return null;
    }

    public static net.minecraft.client.gui.screens.ChatScreen chatScreen(String command, boolean signal) {
        for (java.lang.reflect.Constructor<?> c : net.minecraft.client.gui.screens.ChatScreen.class.getConstructors()) {
            Class<?>[] p = c.getParameterTypes();
            try {
                if (p.length == 1) return (net.minecraft.client.gui.screens.ChatScreen) c.newInstance(command);
                if (p.length == 2) return (net.minecraft.client.gui.screens.ChatScreen) c.newInstance(command, signal);
            } catch (Exception ignored) {}
        }
        return null;
    }

    public static int keyEventCode(Object event) {
        for (String name : new String[] {"key", "getKeyCode", "code"}) {
            try {
                Object v = event.getClass().getMethod(name).invoke(event);
                if (v instanceof Number n) return n.intValue();
            } catch (Exception ignored) {}
        }
        return 0;
    }

    public static void sendSystemMessage(net.minecraft.world.entity.player.Player player, net.minecraft.network.chat.Component message) {
        try {
            player.getClass().getMethod("displayClientMessage", net.minecraft.network.chat.Component.class, boolean.class)
                .invoke(player, message, false);
        } catch (Exception ignored) {}
    }

    public static net.minecraft.world.item.ItemStack createItemStack(Object input, int count, boolean allowExtra) {
        for (java.lang.reflect.Method m : input.getClass().getMethods()) {
            if (!m.getName().equals("createItemStack")) continue;
            Class<?>[] p = m.getParameterTypes();
            try {
                if (p.length == 1) return (net.minecraft.world.item.ItemStack) m.invoke(input, count);
                if (p.length == 2) return (net.minecraft.world.item.ItemStack) m.invoke(input, count, allowExtra);
            } catch (Exception ignored) {}
        }
        return net.minecraft.world.item.ItemStack.EMPTY;
    }

    public static Object soundId(Object sound) {
        for (String name : new String[] {"getIdentifier", "id", "getSoundLocation"}) {
            try { return sound.getClass().getMethod(name).invoke(sound); } catch (Exception ignored) {}
        }
        return null;
    }

    public static boolean hasShiftDown(net.minecraft.client.Minecraft mc) {
        try { return (boolean) mc.getClass().getMethod("hasShiftDown").invoke(mc); }
        catch (Exception e) { return false; }
    }

    public static boolean clientLoaded(net.minecraft.client.multiplayer.ClientPacketListener listener) {
        try { return (boolean) listener.getClass().getMethod("hasClientLoaded").invoke(listener); }
        catch (Exception e) { return true; }
    }

    public static int packetEntityId(Object packet) {
        for (String name : new String[] {"id", "getEntityId"}) {
            try {
                Object v = packet.getClass().getMethod(name).invoke(packet);
                if (v instanceof Number n) return n.intValue();
            } catch (Exception ignored) {}
        }
        return -1;
    }

    public static net.minecraft.world.phys.Vec3 packetMovement(Object packet) {
        for (String name : new String[] {"movement", "getDeltaMovement", "getMovement"}) {
            try {
                Object v = packet.getClass().getMethod(name).invoke(packet);
                if (v instanceof net.minecraft.world.phys.Vec3 vec) return vec;
            } catch (Exception ignored) {}
        }
        return net.minecraft.world.phys.Vec3.ZERO;
    }

    public static boolean withinBlockInteractionRange(net.minecraft.world.entity.player.Player player,
                                                      net.minecraft.core.BlockPos pos, int distance) {
        try {
            return (boolean) player.getClass()
                .getMethod("isWithinBlockInteractionRange", net.minecraft.core.BlockPos.class, int.class)
                .invoke(player, pos, distance);
        } catch (Exception e) {
            return player.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) <= distance * distance;
        }
    }

    public static boolean inShallowWater(net.minecraft.world.entity.player.Player player) {
        try { return (boolean) player.getClass().getMethod("isInShallowWater").invoke(player); }
        catch (Exception e) { return false; }
    }

    public static boolean mobilityRestricted(net.minecraft.world.entity.player.Player player) {
        try { return (boolean) player.getClass().getMethod("isMobilityRestricted").invoke(player); }
        catch (Exception e) { return false; }
    }

    public static boolean enoughFood(net.minecraft.world.entity.player.Player player) {
        try {
            Object food = player.getClass().getMethod("getFoodData").invoke(player);
            return (boolean) food.getClass().getMethod("hasEnoughFood").invoke(food);
        } catch (Exception e) { return true; }
    }

    public static float itemSwapScale(net.minecraft.world.entity.player.Player player, float delta) {
        try {
            return (float) player.getClass().getMethod("getItemSwapScale", float.class).invoke(player, delta);
        } catch (Exception e) { return 1f; }
    }

    public static boolean canGlideThrough(net.minecraft.world.level.block.state.BlockState state) {
        try {
            Object tag = net.minecraft.tags.BlockTags.class.getField("CAN_GLIDE_THROUGH").get(null);
            return state.is((net.minecraft.tags.TagKey<net.minecraft.world.level.block.Block>) tag);
        } catch (Exception e) { return false; }
    }

    public static net.minecraft.resources.ResourceKey<net.minecraft.world.item.enchantment.Enchantment> lungeEnchantment() {
        try { return (net.minecraft.resources.ResourceKey<net.minecraft.world.item.enchantment.Enchantment>) net.minecraft.world.item.enchantment.Enchantments.class.getField("LUNGE").get(null); }
        catch (Exception e) { return null; }
    }

    public static net.minecraft.client.multiplayer.ClientPacketListener createClientPacketListener(
            net.minecraft.client.Minecraft mc, net.minecraft.network.Connection connection,
            com.mojang.authlib.GameProfile profile) {
        try {
            Class<?> cookieClass = Class.forName("net.minecraft.client.multiplayer.CommonListenerCookie");
            java.lang.reflect.Constructor<?> cc = cookieClass.getConstructors()[0];
            Object[] args = new Object[cc.getParameterCount()];
            Class<?>[] types = cc.getParameterTypes();
            for (int i = 0; i < types.length; i++) {
                if (types[i] == com.mojang.authlib.GameProfile.class) args[i] = profile;
                if (types[i] == net.minecraft.client.multiplayer.ServerData.class) args[i] = mc.getCurrentServer();
            }
            Object cookie = cc.newInstance(args);
            return net.minecraft.client.multiplayer.ClientPacketListener.class
                .getConstructor(net.minecraft.client.Minecraft.class, net.minecraft.network.Connection.class, cookieClass)
                .newInstance(mc, connection, cookie);
        } catch (Exception e) { return null; }
    }

    public static void sendToTrackingPlayers(Object chunks,
                                             net.minecraft.world.entity.Entity entity,
                                             net.minecraft.network.protocol.Packet<?> packet) {
        for (java.lang.reflect.Method m : chunks.getClass().getMethods()) {
            if (m.getName().equals("sendToTrackingPlayers") && m.getParameterCount() == 2) {
                try { m.invoke(chunks, entity, packet); return; } catch (Exception ignored) {}
            }
        }
    }

    public static int modifierMask() {
        return MOD_SHIFT | MOD_CONTROL | MOD_ALT | MOD_SUPER;
    }

    public static int debugEntries(net.minecraft.client.Minecraft mc) {
        try {
            Object v = mc.getClass().getField("debugEntries").get(mc);
            return v instanceof Number n ? n.intValue() : 0;
        } catch (Exception e) { return 0; }
    }

    public static void extractPanorama(Object screen, Object graphics, float delta) {
        try {
            screen.getClass().getMethod("extractPanorama", graphics.getClass(), float.class)
                .invoke(screen, graphics, delta);
        } catch (Exception ignored) {}
    }

    public static net.minecraft.world.phys.Vec3 cameraPosition() {
        net.minecraft.client.Camera cam = camera();
        if (cam == null) return net.minecraft.world.phys.Vec3.ZERO;
        for (String name : new String[] {"getPos", "getPosition"}) {
            try {
                Object v = cam.getClass().getMethod(name).invoke(cam);
                if (v instanceof net.minecraft.world.phys.Vec3 vec) return vec;
            } catch (Exception ignored) {}
        }
        return net.minecraft.world.phys.Vec3.ZERO;
    }

    public static org.joml.Matrix4f poseMatrix(Object pose) {
        for (String name : new String[] {"getPositionMatrix", "pose"}) {
            try {
                Object v = pose.getClass().getMethod(name).invoke(pose);
                if (v instanceof org.joml.Matrix4f m) return m;
            } catch (Exception ignored) {}
        }
        return new org.joml.Matrix4f();
    }

    public static boolean hasPermission(net.minecraft.world.entity.player.Player player) {
        try { return (boolean) player.getClass().getMethod("hasPermissions").invoke(player); }
        catch (Exception e) { return false; }
    }

    public static boolean waterEvaporates(Object level) { return false; }

    public static net.minecraft.core.Holder<net.minecraft.world.item.Item> directHolder(
            net.minecraft.world.item.Item item, Object components) {
        for (java.lang.reflect.Method m : net.minecraft.core.Holder.class.getMethods()) {
            if (m.getName().equals("direct") && m.getParameterCount() >= 1) {
                try {
                    return (net.minecraft.core.Holder<net.minecraft.world.item.Item>) m.invoke(null, item);
                } catch (Exception ignored) {}
            }
        }
        return item.builtInRegistryHolder();
    }

    public static boolean isOverlayVisible(net.minecraft.client.Minecraft mc) {
        try { return (boolean) mc.getClass().getField("debugEntries").get(mc).getClass().getMethod("isOverlayVisible").invoke(mc.getClass().getField("debugEntries").get(mc)); }
        catch (Exception e) { return false; }
    }

    public static net.minecraft.world.InteractionResult interact(net.minecraft.world.entity.player.Player player,
                                                                net.minecraft.world.entity.Entity entity,
                                                                net.minecraft.world.InteractionHand hand,
                                                                net.minecraft.world.phys.Vec3 hit) {
        return interact(player, entity, hand);
    }

    public static net.minecraft.world.InteractionResult interactWithHit(Object gameMode,
                                                                       net.minecraft.world.entity.player.Player player,
                                                                       net.minecraft.world.entity.Entity entity,
                                                                       net.minecraft.world.phys.EntityHitResult hit,
                                                                       net.minecraft.world.InteractionHand hand) {
        return interact(player, entity, hand);
    }


    public static net.minecraft.world.entity.EntityType<?> componentEntityType(Object component) {
        try { return (net.minecraft.world.entity.EntityType<?>) component.getClass().getMethod("type").invoke(component); }
        catch (Exception e) { return null; }
    }

    private static int chunkCoord(net.minecraft.world.level.ChunkPos pos, String name) {
        try { return (int) pos.getClass().getMethod(name).invoke(pos); }
        catch (Exception e) {
            try { return (int) pos.getClass().getField(name).get(pos); }
            catch (Exception ignored) { return 0; }
        }
    }

    public static boolean matchesKey(net.minecraft.client.KeyMapping mapping, net.minecraft.client.input.KeyEvent event) {
        for (java.lang.reflect.Method m : mapping.getClass().getMethods()) {
            if (m.getName().equals("matches") && m.getParameterCount() == 1 && m.getParameterTypes()[0].isInstance(event)) {
                try { return (boolean) m.invoke(mapping, event); } catch (Exception ignored) {}
            }
        }
        try {
            int key = (int) event.getClass().getMethod("key").invoke(event);
            int scancode = (int) event.getClass().getMethod("scancode").invoke(event);
            try {
                java.lang.reflect.Method m = mapping.getClass().getMethod("matches", int.class, int.class);
                return (boolean) m.invoke(mapping, key, scancode);
            } catch (Exception ignored) { return false; }
        } catch (Exception e) { return false; }
    }

    public static boolean matchesMouse(net.minecraft.client.KeyMapping mapping, net.minecraft.client.input.MouseButtonEvent event) {
        for (java.lang.reflect.Method m : mapping.getClass().getMethods()) {
            if (m.getName().equals("matchesMouse") && m.getParameterCount() == 1 && m.getParameterTypes()[0].isInstance(event)) {
                try { return (boolean) m.invoke(mapping, event); } catch (Exception ignored) {}
            }
        }
        return false;
    }

    public static net.minecraft.world.InteractionResult interact(net.minecraft.world.entity.player.Player player,
                                                                net.minecraft.world.entity.Entity entity,
                                                                net.minecraft.world.InteractionHand hand) {
        try {
            return (net.minecraft.world.InteractionResult) net.minecraft.client.Minecraft.getInstance().gameMode.getClass()
                .getMethod("interact", net.minecraft.world.entity.player.Player.class, net.minecraft.world.entity.Entity.class,
                    net.minecraft.world.InteractionHand.class)
                .invoke(net.minecraft.client.Minecraft.getInstance().gameMode, player, entity, hand);
        } catch (Exception e) {
            return net.minecraft.world.InteractionResult.PASS;
        }
    }

    public static net.minecraft.world.InteractionResult interactOn(net.minecraft.world.entity.player.Player player,
                                                                  net.minecraft.world.entity.Entity entity,
                                                                  net.minecraft.world.InteractionHand hand,
                                                                  net.minecraft.world.phys.Vec3 hit) {
        for (java.lang.reflect.Method m : player.getClass().getMethods()) {
            if (!m.getName().equals("interactOn")) continue;
            Class<?>[] p = m.getParameterTypes();
            try {
                if (p.length == 2) return (net.minecraft.world.InteractionResult) m.invoke(player, entity, hand);
                if (p.length == 3) return (net.minecraft.world.InteractionResult) m.invoke(player, entity, hand, hit);
            } catch (Exception ignored) {}
        }
        return net.minecraft.world.InteractionResult.PASS;
    }

    // Universal facades for APIs that appeared after 1.21.5.
    public static void sendGameModePacket(net.minecraft.world.level.GameType mode) {
        for (String name : new String[] {
            "net.minecraft.network.protocol.game.ServerboundChangeGameModePacket",
            "net.minecraft.network.protocol.game.ServerboundGameModePacket"
        }) {
            try {
                Class<?> type = Class.forName(name);
                Object packet = type.getConstructor(net.minecraft.world.level.GameType.class).newInstance(mode);
                mc.getConnection().send((net.minecraft.network.protocol.Packet<?>) packet);
                return;
            } catch (ReflectiveOperationException ignored) {
            }
        }
    }

    public static java.util.List<net.minecraft.network.chat.FormattedText> fontSplit(
        net.minecraft.client.gui.Font font, net.minecraft.network.chat.Component text, int width
    ) {
        for (String name : new String[] { "splitIgnoringLanguage", "split" }) {
            try {
                java.lang.reflect.Method m = font.getClass().getMethod(name, net.minecraft.network.chat.Component.class, int.class);
                @SuppressWarnings("unchecked")
                java.util.List<net.minecraft.network.chat.FormattedText> result =
                    (java.util.List<net.minecraft.network.chat.FormattedText>) m.invoke(font, text, width);
                return result;
            } catch (ReflectiveOperationException ignored) {
            }
        }
        return java.util.List.of(text);
    }

    @SuppressWarnings("unchecked")
    public static com.mojang.brigadier.tree.RootCommandNode<net.minecraft.commands.SharedSuggestionProvider> commandRoot(
        Object packet, net.minecraft.commands.CommandBuildContext context
    ) {
        for (java.lang.reflect.Method m : packet.getClass().getMethods()) {
            if (!m.getName().equals("getRoot")) continue;
            try {
                if (m.getParameterCount() == 1) {
                    return (com.mojang.brigadier.tree.RootCommandNode<net.minecraft.commands.SharedSuggestionProvider>) m.invoke(packet, context);
                }
                if (m.getParameterCount() == 2) {
                    return (com.mojang.brigadier.tree.RootCommandNode<net.minecraft.commands.SharedSuggestionProvider>) m.invoke(packet, context, null);
                }
            } catch (ReflectiveOperationException ignored) {
            }
        }
        throw new IllegalStateException("ClientboundCommandsPacket has no getRoot()");
    }

    public static java.util.List<net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent> tooltipComponents(
        java.util.List<net.minecraft.network.chat.Component> lines
    ) {
        java.util.List<net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent> out = new java.util.ArrayList<>();
        for (net.minecraft.network.chat.Component line : lines) {
            try {
                java.lang.reflect.Method create = net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent.class.getMethod("create", net.minecraft.network.chat.FormattedText.class);
                out.add((net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent) create.invoke(null, line));
                continue;
            } catch (ReflectiveOperationException ignored) {
            }
            try {
                java.lang.reflect.Method create = net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent.class.getMethod("create", net.minecraft.util.FormattedCharSequence.class);
                out.add((net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent) create.invoke(null, line.getVisualOrderText()));
            } catch (ReflectiveOperationException ignored) {
            }
        }
        return out;
    }

    public static net.minecraft.world.entity.Entity getEntity(net.minecraft.world.level.Level level, java.util.UUID uuid) {
        try {
            return level.getEntity(uuid);
        } catch (Throwable t) {
            try {
                java.lang.reflect.Method m = level.getClass().getMethod("getEntityByUuid", java.util.UUID.class);
                return (net.minecraft.world.entity.Entity) m.invoke(level, uuid);
            } catch (ReflectiveOperationException ignored) {
                return null;
            }
        }
    }
}