/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.mc;

import java.net.URI;
import java.time.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;
import java.nio.*;
import java.nio.file.*;

import com.google.common.collect.*;

import com.mojang.math.*;
import com.mojang.blaze3d.*;
import com.mojang.blaze3d.vertex.*;
import com.mojang.blaze3d.platform.*;
import com.mojang.blaze3d.platform.cursor.*;

import com.mojang.authlib.*;
import com.mojang.authlib.services.*;
import com.mojang.authlib.yggdrasil.*;
import com.mojang.authlib.minecraft.*;
import org.lwjgl.*;
import org.lwjgl.sdl.*;
import org.lwjgl.glfw.*;

import it.unimi.dsi.fastutil.*;
import it.unimi.dsi.fastutil.longs.*;
import it.unimi.dsi.fastutil.ints.*;

import net.minecraft.resources.*;
import net.minecraft.util.*;
import net.minecraft.nbt.*;
import net.minecraft.core.*;
import net.minecraft.core.component.*;
import net.minecraft.data.registries.*;
import net.minecraft.network.chat.*;
import net.minecraft.network.protocol.*;
import net.minecraft.network.protocol.game.*;
import net.minecraft.world.*;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.*;
import net.minecraft.world.attribute.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.projectile.*;
import net.minecraft.world.phys.*;
import net.minecraft.world.level.*;
import net.minecraft.world.level.material.*;
import net.minecraft.world.level.dimension.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.saveddata.maps.*;
import net.minecraft.client.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.item.*;
import net.minecraft.client.renderer.chunk.*;
import net.minecraft.client.renderer.state.*;
import net.minecraft.client.renderer.feature.*;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.client.model.*;
import net.minecraft.client.input.*;
import net.minecraft.client.player.*;
import net.minecraft.client.multiplayer.*;
import net.minecraft.client.multiplayer.chat.*;
import net.minecraft.client.multiplayer.chat.report.*;
import net.minecraft.client.resources.*;
import net.minecraft.client.resources.model.*;
import net.minecraft.client.resources.model.sprite.*;
import net.minecraft.client.resources.model.geometry.*;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.components.toasts.*;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.screens.*;
import net.minecraft.client.gui.screens.social.*;
import net.minecraft.client.gui.screens.inventory.*;
import net.minecraft.client.gui.screens.inventory.tooltip.*;
import net.minecraft.server.*;
import net.minecraft.server.level.*;

import meteordevelopment.meteorclient.mixin.*;
import meteordevelopment.meteorclient.renderer.*;
import meteordevelopment.meteorclient.mixininterface.*;


import static meteordevelopment.meteorclient.MeteorClient.mc;

/**
 * Bridges the API differences between the supported Minecraft versions.
 *
 * <p>This class is version specific, but every method has the same signature and the same
 * behaviour in all of them, so the shared code in {@code src/main/java} can use it without
 * knowing which Minecraft version it is running on. See COMPAT.md.
 */
public final class Vanilla
{
    private Vanilla()
    {}

    // Player actions
    public static void swing(Player player, InteractionHand hand)
    {
        player.swing(hand, SwingAnimation.DEFAULT, false);
    }

    /** Swings for a real server-side player (singleplayer clone): broadcast to trackers happens inside swing(). */
    public static void swingClone(ServerPlayer player)
    {
        player.swing(InteractionHand.MAIN_HAND, SwingAnimation.DEFAULT, true);
    }

    /** Whether the player is mid-swing (the swing state API differs between versions). */
    public static boolean isSwinging(Player player)
    {
        return player.isSwinging();
    }

    // Blocks
    public static BlockPos copy(BlockPos pos)
    {
        return pos.immutable();
    }

    public static boolean bedDestroysOnUse(BedRule rule)
    {
        return rule.destroyOnUse();
    }

    // Collision
    public static boolean blocksMotion(BlockState state)
    {
        return state.isSolid();
    }

    public static Collection<EntityHitResult> getManyEntityHitResult(Level level, Entity entity, Vec3 from, Vec3 to, AABB box, Predicate<Entity> predicate, float tolerance, ClipContext.Block block, boolean onlyBlocks, boolean extra)
    {
        return ProjectileUtil.getManyEntityHitResult(level, entity, from, to, box, predicate, tolerance, block, onlyBlocks, extra);
    }

    public static List<BakedQuad> quads(ItemStackRenderState.LayerRenderState layer)
    {
        return ((LayerRenderStateAccessor) layer).meteor$getQuads().all();
    }

    // Registries
    public static HolderLookup.Provider worldLookup()
    {
        return VanillaRegistries.createWorldLookup();
    }

    /**
     * A swing the server is told about.
     *
     * <p>26.2 could send a swing packet; 26.3 removed the packet, so the closest equivalent
     * is the local swing.
     */
    public static void swingPacket(LocalPlayer player, InteractionHand hand)
    {
        player.swing(hand, SwingAnimation.DEFAULT, false);
    }

    public static void dropMainHand(LocalPlayer player)
    {
        player.drop(player.getMainHandItem(), true, Prediction.PREDICTED);
    }

    public static void registerSwingPacket(ImmutableMap.Builder<String, PacketType<? extends Packet<?>>> builder)
    {
        // 26.3 removed the swing packet: the client can no longer tell the server about a swing.
    }

    // Items
    public static boolean isFuel(Item item)
    {
        return item.components().get(DataComponents.COOKING_FUEL) != null;
    }

    // Input
    public static final InputConstants.Type KEY_TYPE = InputConstants.Type.KEYBOARD;

    public static final int SUPER_KEY_LEFT = InputConstants.KEY_LGUI;

    public static final int SUPER_KEY_RIGHT = InputConstants.KEY_RGUI;

    public static int scancode(net.minecraft.client.input.KeyEvent event)
    {
        return event.keycode();
    }

    public static void startTextInput(Object owner)
    {
        mc.textInputManager().startTextInput(owner);
    }

    public static void stopTextInput(Object owner)
    {
        mc.textInputManager().stopTextInput(owner);
    }

    public static void selectCursor(CursorType cursor)
    {
        cursor.select();
    }

    public static void releaseMouse(Window window, double x, double y)
    {
        InputConstants.releaseMouse(window, x, y);
    }

    // Transforms
    public static void rotate(PoseStack poseStack, Axis axis, float degrees)
    {
        poseStack.mulPose(axis.rotateDegrees(new org.joml.Matrix4f(), degrees));
    }

    // Entities
    public static boolean isEnderman(Entity entity)
    {
        return entity instanceof Enderman;
    }

    public static boolean isCreepy(Entity entity)
    {
        return ((Enderman) entity).isCreepy();
    }

    public static void tooltip(GuiGraphicsExtractor graphics, Font font, List<ClientTooltipComponent> components, int x, int y, ItemStack stack)
    {
        graphics.tooltip(font, components, x, y, DefaultTooltipPositioner.INSTANCE, stack.get(DataComponents.TOOLTIP_STYLE), false);
    }

    public static boolean isSameFluid(Fluid a, Fluid b)
    {
        return a.isSame(b);
    }

    public static ClientLevel createClientLevel(ClientPacketListener listener, ClientLevel.ClientLevelData data, ResourceKey<Level> dimension, Holder<DimensionType> dimensionType, int x, int z, LevelExtractor extractor, boolean isClientSide, int seaLevel)
    {
        return new ClientLevel(listener, data, dimension, dimensionType, x, z, extractor, isClientSide, 0L, seaLevel);
    }

    // Textures
    public static McTexture createTexture(Supplier<String> name, NativeImage image)
    {
        return new McTexture(new DynamicTexture(name, image));
    }

    public static void registerTexture(Identifier id, McTexture texture)
    {
        mc.getTextureManager().register(id, (DynamicTexture) texture.handle());
    }

    public static void renderTexture(Renderer2D renderer, McTexture texture)
    {
        AbstractTexture t = (AbstractTexture) texture.handle();
        renderer.render(t.getTextureView(), t.getSampler());
    }

    public static int dyeDiffuseColor(DyeColor dye)
    {
        return dye.getTextureDiffuseColor();
    }

    public static List<ItemStack> containerItemsDataComponentMap components)
    {
        return components.get(DataComponents.CONTAINER).itemCopies().toList();
    }

    public static NativeImage mapImage(MapTextureManagerAccessor accessor, MapId id, MapItemSavedData state)
    {
        return accessor.meteor$invokeGetOrCreateMapInstance(id, state).texture.getPixels();
    }

    /** 26.2 and 26.3 hand out the image of the map texture, which owns it. */
    public static void closeMapImage(NativeImage image)
    {}

    // Keyboard
    public static final int MENU_KEY = SDLScancode.SDL_SCANCODE_MENU;

    public static final int WORLD_2_KEY = SDLScancode.SDL_SCANCODE_INTERNATIONAL2;

    public static boolean matchesKey(int input, int scancode, boolean key, InputConstants.Key inputKey)
    {
        boolean isKey = inputKey.getType() != InputConstants.Type.MOUSE;
        return isKey == key && input == inputKey.getValue();
    }

    public static boolean isKeyDown(int key)
    {
        return InputConstants.isKeyDown(key);
    }

    public static boolean isMouseButtonKey(int key)
    {
        return key >= 1 && key <= 8;
    }

    public static boolean isMouseButtonPressed(int button)
    {
        if (button < 1 || button > 8) return false;
        int state = SDLMouse.SDL_GetMouseState(BufferUtils.createFloatBuffer(1), BufferUtils.createFloatBuffer(1));
        return (state & (1 << (button - 1))) != 0;
    }

    public static BakedQuad.MaterialInfo translucentMaterialInfo(BakedQuad.MaterialInfo info)
    {
        return new BakedQuad.MaterialInfo(info.sprite(), ChunkSectionLayer.TRANSLUCENT, info.itemRenderType(), info.itemGlintRenderType(), info.itemGlintSpecialRenderType(), info.tintIndex(), info.shadeDirectionOverride(), info.flags());
    }

    public static <S> void submitBannerPatterns(SpriteGetter sprites, PoseStack poseStack, SubmitNodeCollector collector, int light, int overlay,Model<S> model, S state, boolean renderOverlay, DyeColor color, BannerPatternLayers patterns)
    {
        BannerRenderer.submitPatterns(sprites, poseStack, collector, light, overlay, model, state, renderOverlay, color, patterns);
    }

    public static <S> void submitBannerModel(SubmitNodeCollector collector, Model<? super S> model, S state, PoseStack poseStack, int light, int overlay, int outlineColor, SpriteId sprite, SpriteGetter sprites, int color, Object crumblingOverlay)
    {
        collector.submitModel(model, state, poseStack, light, overlay, outlineColor, sprite, sprites, color);
    }

    /**
     * 26.3 removed the swing packet, so a critical only holds back the attack.
     */
    public static boolean requiresSwingPacket()
    {
        return false;
    }

    public static boolean isSwingPacket(Object packet)
    {
        return false;
    }

    public static void sendSwingPacket(Object packet)
    {}

    // Authentication (authlib 10)
    public static Object createAuthService()
    {
        return MinecraftServicesDiscoveryService.create(mc.getProxy(), false);
    }

    public static void applyLoginEnvironment(Object authService)
    {
        MinecraftServicesDiscoveryService service = (MinecraftServicesDiscoveryService) authService;
        MinecraftAccessor mca = (MinecraftAccessor) mc;
        SignatureValidator.from(service.getServicesKeySet(), ServicesKeyType.PROFILE_KEY);
        SkinManager.TextureCache skinCache = ((SkinManagerAccessor) mc.getSkinManager()).meteor$getSkinTextures();
        Path skinCachePath = ((FileCacheAccessor) skinCache).meteor$getRoot();
        mca.meteor$setServices(Services.create(service, mc.gameDirectory));
        mca.meteor$setSkinManager(new SkinManager(skinCachePath, mc.services(), new SkinTextureDownloader(mc.getProxy(), mc.getTextureManager(), mc), mc));
    }

    public static void setSession(User session)
    {
        MinecraftAccessor mca = (MinecraftAccessor) mc;
        mca.meteor$setUser(session);

       MinecraftServicesDiscoveryService service = MinecraftServicesDiscoveryService.create(mc.getProxy(), false);

       UserApiService apiService = service.createUserApiService(session.getAccessToken());
       FriendsService friendsService = service.createFriendsService(session.getAccessToken());
       RemoteFriendListUpdateHandler handler = new RemoteFriendListUpdateHandler(friendsService, mc);
       mca.meteor$setUserApiService(apiService);
       mca.meteor$setPlayerSocialManager(new PlayerSocialManager(mc, apiService, friendsService, handler));
       mca.meteor$setProfileKeyPairManager(ProfileKeyPairManager.create(apiService, session, mc.gameDirectory.toPath()));
       mca.meteor$setReportingContext(ReportingContext.create(ReportEnvironment.local(), apiService));
       mca.meteor$setProfileFuture(CompletableFuture.supplyAsync(() -> mc.services().sessionService().fetchProfile(mc.getUser().getProfileId(), true), Util.ioPool()));
    }

    public static Object createAlteningEnvironment()
    {
        return new com.mojang.authlib.Environment("https://authserver.thealtening.com", "The Altening");
    }

    public static Object createAlteningService(Object environment)
    {
        return MinecraftServicesDiscoveryService.create(mc.getProxy(), false, (com.mojang.authlib.Environment) environment);
    }

    public static String alteningAuthUrl(Object environment)
    {
        return ((Environment) environment).discoveryUrl() + "/authenticate";
    }

    // Packets
    public static ServerboundChatPacket chat(String message, Instant instant, long seen, MessageSignature signature, LastSeenMessages.Update update)
    {
        return new ServerboundChatPacket(message, instant, seen, Optional.ofNullable(signature), update);
    }

    public static ServerboundSignUpdatePacket signUpdate(BlockPos pos, String[] lines)
    {
        return new ServerboundSignUpdatePacket(pos, List.of(lines), SignTextSlot.FRONT);
    }

    public static String[] signLines(ServerboundSignUpdatePacket packet)
    {
        return packet.lines().toArray(String[]::new);
    }

    public static int chunkX(ClientboundLevelChunkWithLightPacket packet)
    {
        return packet.x();
    }

    public static int chunkZ(ClientboundLevelChunkWithLightPacket packet)
    {
        return packet.z();
    }

    public static IntList removeEntityIds(ClientboundRemoveEntitiesPacket packet)
    {
        return packet.entityIds();
    }

    public static Vec3 movingPosition(ServerboundMoveVehiclePacket packet)
    {
        return packet.movingTo().position();
    }

    public static BlockHitResult hitResult(ServerboundUseItemOnPacket packet)
    {
        return packet.hitResult();
    }

    // Misc
    public static void openUri(String url)
    {
        Blaze3D.openUri(java.net.URI.create(url));
    }
 
    // Gui / screens (26.2 moved screen management onto the gui manager)
    public static Screen screen()
    {
        return mc.gui.screen();
    }

    public static void setScreen(Screen screen)
    {
        mc.gui.setScreen(screen);
    }

    public staticOverlay overlay()
    {
        return mc.gui.overlay();
    }

    public static ToastManager toastManager()
    {
        return mc.gui.toastManager();
    }

    public static ChatComponent chatComponent()
    {
        return mc.gui.hud.getChat();
    }

    // Game renderer
    public static RenderTarget mainRenderTarget()
    {
        return mc.gameRenderer.mainRenderTarget();
    }

    public static GameRenderState gameRenderState()
    {
        return mc.gameRenderer.gameRenderState();
    }

    public static net.minecraft.client.Camera mainCamera()
    {
        return mc.gameRenderer.mainCamera();
    }

    public static Lighting lighting()
    {
        return mc.gameRenderer.lighting();
    }

    // Items
    public static boolean isShulker(Item item)
    {
        if (item == Items.SHULKER_BOX) return true;

        ColorCollection<Item> dyed = Items.DYED_SHULKER_BOX;
        return item == dyed.white()
            || item == dyed.orange()
            || item == dyed.magenta()
            || item == dyed.lightBlue()
            || item == dyed.yellow()
            || item == dyed.lime()
            || item == dyed.pink()
            || item == dyed.gray()
            || item == dyed.lightGray()
            || item == dyed.cyan()
            || item == dyed.purple()
            || item == dyed.blue()
            || item == dyed.brown()
            || item == dyed.green()
            || item == dyed.red()
            || item == dyed.black();
    }

    // Packets
    public static void registerSpectatorActionPacket(ImmutableMap.Builder<String, PacketType<? extends Packet<?>>> builder)
    {
        builder.put("ServerboundSpectatorActionPacket", GamePacketTypes.SERVERBOUND_SPECTATOR_ACTION);
    }
 
    // Chat
    public static ChatListener chatListener()
    {
        return mc.gui.chatListener();
    }

    // Block breaking progress
    public static void loadFromBucketTag(LivingEntity entity, CompoundTag tag)
    {
        ((Bucketable) entity).loadFromBucketTag(tag);
    }

    // Chunk rebuilds
    public static void allChanged()
    {
        mc.levelExtractor.allChanged();
    }

    public static Long2ObjectMap<SortedSet<BlockDestructionProgress>> destructionProgress()
    {
        return mc.level.destructionProgress();
    }

    // Weathered copper lookups (26.2+ exposes the oxidation families as collections)
    public static Block oxidizedCopper()
    {
        return Blocks.COPPER_BLOCK.weathering().oxidized();
    }

    public static Block oxidizedCutCopper()
    {
        return Blocks.CUT_COPPER.weathering().oxidized();
    }

    public static Block waxedUnaffectedCopper()
    {
        return Blocks.COPPER_BLOCK.waxed().unaffected();
    }

    public static Block waxedOxidizedCopper()
    {
        return Blocks.COPPER_BLOCK.waxed().oxidized();
    }

    public static Block waxedOxidizedCutCopper()
    {
        return Blocks.CUT_COPPER.waxed().oxidized();
    }

    public static Block waxedUnaffectedCopperBulb()
    {
        return Blocks.COPPER_BULB.waxed().unaffected();
    }
}
