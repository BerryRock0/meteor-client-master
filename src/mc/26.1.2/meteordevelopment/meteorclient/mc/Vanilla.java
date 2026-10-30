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
import com.mojang.blaze3d.pipeline.*;
import com.mojang.blaze3d.platform.*;
import com.mojang.blaze3d.platform.cursor.*;

import com.mojang.authlib.*;
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
        player.swing(hand);
    }

    /** Swings for a real server-side player (singleplayer clone): broadcast to trackers happens inside swing(). */
    public static void swingClone(ServerPlayer player)
    {
        player.swing(InteractionHand.MAIN_HAND);
    }

    /** Whether the player is mid-swing (the swing state API differs between versions). */
    public static boolean isSwinging(Player player)
    {
        return player.swinging;
    }

    // Blocks
    public static BlockPos copy(BlockPos pos)
    {
        return new BlockPos(pos);
    }

    public static boolean bedDestroysOnUse(BedRule rule)
    {
        return rule.explodes();
    }

    // Collision
    public static boolean blocksMotion(BlockState state)
    {
        return state.blocksMotion();
    }

    public static Collection<EntityHitResult> getManyEntityHitResult(Level level, Entity entity, Vec3 from, Vec3 to, AABB box, Predicate<Entity> predicate, float tolerance, ClipContext.Block block, boolean onlyBlocks, boolean extra)
    {
        return ProjectileUtil.getManyEntityHitResult(level, entity, from, to, box, predicate, tolerance, block, onlyBlocks);
    }

    public static List<BakedQuad> quads(ItemStackRenderState.LayerRenderState layer)
    {
        return layer.prepareQuadList();
    }

    // Registries
    public static HolderLookup.Provider worldLookup()
    {
        return VanillaRegistries.createLookup();
    }

    /**
     * A swing the server is told about.
     *
     * <p>26.2 could send a swing packet; 26.3 removed the packet, so the closest equivalent
     * is the local swing.
     */
    public static void swingPacket(LocalPlayer player, InteractionHand hand)
    {
        mc.getConnection().send(new ServerboundSwingPacket(hand));
    }

    public static void dropMainHand(LocalPlayer player) {
        player.drop(true);
    }

    public static void registerSwingPacket(ImmutableMap.Builder<String, PacketType<? extends Packet<?>>> builder)
    {
        builder.put("ServerboundSwingPacket", GamePacketTypes.SERVERBOUND_SWING);
    }

    // Items
    public static boolean isFuel(Item item)
    {
        return mc.getConnection().fuelValues().fuelItems().contains(item);
    }

    // Input
    public static final InputConstants.Type KEY_TYPE = InputConstants.Type.KEYSYM;

    public static final int SUPER_KEY_LEFT = InputConstants.KEY_LSUPER;

    public static final int SUPER_KEY_RIGHT = InputConstants.KEY_RSUPER;

    public static int scancode(net.minecraft.client.input.KeyEvent event)
    {
        return event.scancode();
    }

    /** 26.2 text input API takes no owner. */
    public static void startTextInput(Object owner)
    {
        mc.textInputManager().startTextInput();
    }

    /** 26.2 text input API takes no owner. */
    public static void stopTextInput(Object owner)
    {
        mc.textInputManager().stopTextInput();
    }

    public static void selectCursor(CursorType cursor)
    {
        cursor.select(mc.getWindow());
    }

    public static void releaseMouse(Window window, double x, double y)
    {
        InputConstants.grabOrReleaseMouse(window, InputConstants.CURSOR_NORMAL, x, y);
    }

    // Transforms
    public static void rotate(PoseStack poseStack, com.mojang.math.Axis axis, float degrees)
    {
        poseStack.mulPose(axis.rotationDegrees(degrees));
    }

    // Entities
    public static boolean isEnderman(Entity entity)
    {
        return entity instanceof EnderMan;
    }

    public static boolean isCreepy(Entity entity)
    {
        return ((EnderMan) entity).isCreepy();
    }

    public static void tooltip(GuiGraphicsExtractor graphics, Font font, List<ClientTooltipComponent> components, int x, int y, ItemStack stack)
    {
        graphics.tooltip(font, components, x, y, DefaultTooltipPositioner.INSTANCE, stack.get(DataComponents.TOOLTIP_STYLE));
    }

    public static boolean isSameFluid(Fluid a, Fluid b)
    {
        return a.isSame(b);
    }

    public static ClientLevel createClientLevel(ClientPacketListener listener, ClientLevel.ClientLevelData data, ResourceKey<Level> dimension, Holder<DimensionType> dimensionType, int x, int z, LevelRenderer extractor, boolean isClientSide, int seaLevel)
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

    public static List<ItemStack> containerItems(DataComponentMap components)
    {
        return components.get(DataComponents.CONTAINER).allItemsCopyStream().toList();
    }

    public static NativeImage mapImage(MapTextureManagerAccessor accessor, MapId id, MapItemSavedData state)
    {
        return accessor.meteor$invokeGetOrCreateMapInstance(id, state).texture.getPixels();
    }

    /** 26.2 and 26.3 hand out the image of the map texture, which owns it. */
    public static void closeMapImage(NativeImage image)
    {}

    // Keyboard
    public static final int MENU_KEY = GLFW.GLFW_KEY_MENU;

    public static final int WORLD_2_KEY = GLFW.GLFW_KEY_WORLD_2;

    public static boolean matchesKey(int input, int scancode, boolean key, InputConstants.Key inputKey)
    {
        boolean isKey = inputKey.getType() != InputConstants.Type.MOUSE;
        return isKey == key && inputKey.getType() == InputConstants.Type.SCANCODE ? scancode == inputKey.getValue() : input == inputKey.getValue();
    }

    public static boolean isKeyDown(int key)
    {
        return InputConstants.isKeyDown(mc.getWindow(), key);
    }

    public static boolean isMouseButtonKey(int key)
    {
        return key >= 0 && key < 8;
    }

    public static boolean isMouseButtonPressed(int button)
    {
        return GLFW.glfwGetMouseButton(mc.getWindow().handle(), button) != InputConstants.RELEASE;
    }

    public static BakedQuad.MaterialInfo translucentMaterialInfo(BakedQuad.MaterialInfo info)
    {
        return new BakedQuad.MaterialInfo(info.sprite(),ChunkSectionLayer.TRANSLUCENT, info.itemRenderType(), info.tintIndex(), info.shade(), info.lightEmission());
    }

    public static <S> void submitBannerPatterns(SpriteGetter sprites,PoseStack poseStack, SubmitNodeCollector collector, int light, int overlay, Model<S> model, S state, boolean renderOverlay, DyeColor color, BannerPatternLayers patterns)
    {
        BannerRenderer.submitPatterns(sprites, poseStack, collector, light, overlay, model, state, renderOverlay, color, patterns, null);
    }

    public static <S> void submitBannerModel(SubmitNodeCollector collector, Model<? super S> model, S state, PoseStack poseStack, int light, int overlay, int outlineColor, SpriteId sprite, SpriteGetter sprites, int color, Object crumblingOverlay)
    {
        collector.submitModel(model, state, poseStack, light, overlay, outlineColor, sprite, sprites, color, (net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay) crumblingOverlay);
    }

    /**
     * 26.2 still had a swing packet, so a critical had to hold it back together with the attack.
     */
    public static boolean requiresSwingPacket()
    {
        return true;
    }

    public static boolean isSwingPacket(Object packet)
    {
        return packet instanceof ServerboundSwingPacket;
    }

    public static void sendSwingPacket(Object packet)
    {
        mc.getConnection().send((ServerboundSwingPacket) packet);
    }

    // Authentication (authlib 9)
    public static Object createAuthService()
    {
        return new YggdrasilAuthenticationService(mc.getProxy());
    }

    public static void applyLoginEnvironment(Object authService)
    {
        YggdrasilAuthenticationService service = (YggdrasilAuthenticationService) authService;
        MinecraftAccessor mca = (MinecraftAccessor) mc;
        SignatureValidator.from(service.getServicesKeySet(), ServicesKeyType.PROFILE_KEY);
        resources.SkinManager.TextureCache skinCache = ((SkinManagerAccessor) mc.getSkinManager()).meteor$getSkinTextures();
        Path skinCachePath = ((FileCacheAccessor) skinCache).meteor$getRoot();
        mca.meteor$setServices(Services.create(service, mc.gameDirectory));
        mca.meteor$setSkinManager(new SkinManager(skinCachePath, mc.services(), new SkinTextureDownloader(mc.getProxy(), mc.getTextureManager(), mc), mc));
    }

    public static void setSession(net.minecraft.client.User session)
    {
        meteordevelopment.meteorclient.mixin.MinecraftAccessor mca = (MinecraftAccessor) mc;
        mca.meteor$setUser(session);

        YggdrasilAuthenticationService service = new YggdrasilAuthenticationService(mc.getProxy());
        UserApiService apiService = service.createUserApiService(session.getAccessToken());
        mca.meteor$setUserApiService(apiService);
        mca.meteor$setProfileKeyPairManager(ProfileKeyPairManager.create(apiService, session, mc.gameDirectory.toPath()));
        mca.meteor$setReportingContext(ReportingContext.create(ReportEnvironment.local(), apiService));
        mca.meteor$setProfileFuture(CompletableFuture.supplyAsync(() -> mc.services().sessionService().fetchProfile(mc.getUser().getProfileId(), true), Util.ioPool()));
    }

    public static Object createAlteningEnvironment()
    {
        return new com.mojang.authlib.Environment("https://sessionserver.thealtening.com", "https://authserver.thealtening.com", "https://api.mojang.com", "The Altening");
    }

    public static Object createAlteningService(Object environment)
    {
        return new YggdrasilAuthenticationService(mc.getProxy(), (com.mojang.authlib.Environment) environment);
    }

    public static String alteningAuthUrl(Object environment)
    {
        return ((com.mojang.authlib.Environment) environment).servicesHost() + "/authenticate";
    }

    // Packets
    public static ServerboundChatPacket chat(String message, Instant instant, long seen, MessageSignature signature, LastSeenMessages.Update update)
    {
        return new ServerboundChatPacket(message, instant, seen, signature, update);
    }

    public static ServerboundSignUpdatePacket signUpdate(BlockPos pos, String[] lines)
    {
        return new ServerboundSignUpdatePacket(pos, true, lines[0], lines[1], lines[2], lines[3]);
    }

    public static String[] signLines(ServerboundSignUpdatePacket packet)
    {
        return packet.getLines();
    }

    public static int chunkX(ClientboundLevelChunkWithLightPacket packet)
    {
        return packet.getX();
    }

    public static int chunkZ(ClientboundLevelChunkWithLightPacket packet)
    {
        return packet.getZ();
    }

    public static IntList removeEntityIds(ClientboundRemoveEntitiesPacket packet)
    {
        return packet.getEntityIds();
    }

    public static Vec3 movingPosition(ServerboundMoveVehiclePacket packet)
    {
        return packet.position();
    }

    public static BlockHitResult hitResult(ServerboundUseItemOnPacket packet)
    {
        return packet.getHitResult();
    }

    // Misc
    public static void openUri(String url)
    {
        Util.getPlatform().openUri(URI.create(url));
    }
 
    // Gui / screens (26.1 keeps screen management on Minecraft itself)
    public static Screen screen()
    {
        return mc.screen;
    }

    public static void setScreen(Screen screen)
    {
        mc.setScreen(screen);
    }

    public static Overlay overlay()
    {
        return mc.getOverlay();
    }

    public static ToastManager toastManager()
    {
        return mc.getToastManager();
    }

    public static ChatComponent chatComponent()
    {
        return mc.gui.getChat();
    }

    // Game renderer
    public static RenderTarget mainRenderTarget()
    {
        return mc.getMainRenderTarget();
    }

    public static GameRenderState gameRenderState()
    {
        return mc.gameRenderer.getGameRenderState();
    }

    public static Camera mainCamera()
    {
        return mc.gameRenderer.getMainCamera();
    }

    public static Lighting lighting()
    {
        return mc.gameRenderer.getLighting();
    }

    // Items
    public static boolean isShulker(Item item)
    {
        if (item == Items.SHULKER_BOX) return true;
        if (item == Items.WHITE_SHULKER_BOX) return true;
        if (item == Items.ORANGE_SHULKER_BOX) return true;
        if (item == Items.MAGENTA_SHULKER_BOX) return true;
        if (item == Items.LIGHT_BLUE_SHULKER_BOX) return true;
        if (item == Items.YELLOW_SHULKER_BOX) return true;
        if (item == Items.LIME_SHULKER_BOX) return true;
        if (item == Items.PINK_SHULKER_BOX) return true;
        if (item == Items.GRAY_SHULKER_BOX) return true;
        if (item == Items.LIGHT_GRAY_SHULKER_BOX) return true;
        if (item == Items.CYAN_SHULKER_BOX) return true;
        if (item == Items.PURPLE_SHULKER_BOX) return true;
        if (item == Items.BLUE_SHULKER_BOX) return true;
        if (item == Items.BROWN_SHULKER_BOX) return true;
        if (item == Items.GREEN_SHULKER_BOX) return true;
        if (item == Items.RED_SHULKER_BOX) return true;
        if (item == Items.BLACK_SHULKER_BOX) return true;
        return false;
    }

    // Packets (26.1 has no spectator action packet)
    public static void registerSpectatorActionPacket(ImmutableMap.Builder<String, PacketType<? extends Packet<?>>> builder)
    {}
 
    // Chat
    public static ChatListener chatListener()
    {
        return mc.getChatListener();
    }

    // Bucket entities
    public static void loadFromBucketTag(LivingEntity entity, CompoundTag tag)
    {
        ((Bucketable) entity).loadFromBucketTag(tag);
    }

    // Chunk rebuilds
    public static void allChanged()
    {
        mc.levelRenderer.allChanged();
    }

    public static Long2ObjectMap<SortedSet<BlockDestructionProgress>> destructionProgress()
    {
        return ((ILevelRenderer) (Object) mc.levelRenderer).meteor$destructionProgress();
    }

    // Weathered copper lookups (26.1 keeps the plain block constants)
    public static Block oxidizedCopper()
    {
        return Blocks.OXIDIZED_COPPER;
    }

    public static Block oxidizedCutCopper()
    {
        return Blocks.OXIDIZED_CUT_COPPER;
    }

    public static Block waxedUnaffectedCopper()
    {
        return Blocks.WAXED_COPPER_BLOCK;
    }

    public static Block waxedOxidizedCopper()
    {
        return Blocks.WAXED_OXIDIZED_COPPER;
    }

    public static Block waxedOxidizedCutCopper()
    {
        return Blocks.WAXED_OXIDIZED_CUT_COPPER;
    }

    public static Block waxedUnaffectedCopperBulb()
    {
        return Blocks.WAXED_COPPER_BULB;
    }
}
