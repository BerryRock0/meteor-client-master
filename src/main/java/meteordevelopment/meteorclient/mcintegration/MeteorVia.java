/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.mcintegration;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.ViaManagerImpl;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.platform.ViaPlatformLoader;
import com.viaversion.viaversion.api.platform.providers.ViaProviders;
import com.viaversion.viaversion.api.protocol.version.VersionProvider;
import com.viaversion.viaversion.connection.UserConnectionImpl;
import com.viaversion.viaversion.platform.NoopInjector;
import com.viaversion.viaversion.platform.UserConnectionViaVersionPlatform;
import com.viaversion.viaversion.platform.ViaDecodeHandler;
import com.viaversion.viaversion.platform.ViaEncodeHandler;
import com.viaversion.viaversion.protocol.ProtocolPipelineImpl;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import meteordevelopment.meteorclient.MeteorClient;
import net.minecraft.network.HandlerNames;
import net.minecraft.network.protocol.PacketFlow;

import java.io.File;
import java.util.logging.Logger;

/**
 * Boots the vendored ViaVersion engine inside the Meteor client so connections can be translated to
 * other Minecraft versions.
 *
 * <p>ViaVersion's core ({@code src/via/java}) is Minecraft-independent. This class provides the
 * small amount of glue it needs to run as a client-side platform: a {@link Platform} (logging, data
 * folder, config), an {@link Injector}, and insertion of the {@link ViaEncodeHandler} /
 * {@link ViaDecodeHandler} pair into the Netty pipeline of every socket connection. The actual
 * client/server version decision is made by {@link MeteorVersionProvider}, driven by the Protocol
 * screen through {@code Compat}.</p>
 */
public final class MeteorVia {
    private static boolean initialized;

    private MeteorVia() {
    }

    /** Initialises ViaVersion once. Safe to call repeatedly; failures are logged, not thrown. */
    public static synchronized void init() {
        if (initialized) return;

        try {
            ViaManagerImpl manager = ViaManagerImpl.builder()
                .platform(new Platform())
                .injector(new NoopInjector())
                .loader(ViaPlatformLoader.NOOP)
                .build();

            // Sets Via.getManager(); must happen before manager.init().
            Via.init(manager);

            // Load the ViaVersion config (populates all fields, incl. packetTrackerConfig).
            // Without this the config fields stay null and packet tracking NPEs.
            Via.getManager().getPlatform().getConf().reload();

            manager.init();
            manager.onServerLoaded();

            // manager.init() registered ViaVersion's default VersionProvider (from the initial base
            // protocol). Replace it with ours, which honours the Protocol screen override.
            ViaProviders providers = manager.getProviders();
            providers.use(VersionProvider.class, new MeteorVersionProvider());

            initialized = true;
            MeteorClient.LOG.info("Vendored ViaVersion engine initialised");
        } catch (Throwable t) {
            // Never let a translation problem break the game; connections simply won't be translated.
            MeteorClient.LOG.warn("Failed to initialise vendored ViaVersion", t);
        }
    }

    /**
     * Inserts the ViaVersion encode/decode handlers into a socket connection's Netty pipeline.
     * Called from a {@code configureSerialization} mixin; guarded so a connection is only set up once
     * and only when ViaVersion initialised successfully.
     */
    public static void injectPipeline(ChannelPipeline pipeline, PacketFlow side) {
        if (!initialized) return;

        if (!(pipeline.channel() instanceof SocketChannel)) return;
        if (pipeline.get(ViaEncodeHandler.NAME) != null) return; // already injected

        UserConnection user = new UserConnectionImpl(pipeline.channel(), side == PacketFlow.CLIENTBOUND);
        new ProtocolPipelineImpl(user).add(MeteorViaProtocol.INSTANCE);

        boolean clientSide = user.isClientSide();
        pipeline.addBefore(
            clientSide ? HandlerNames.ENCODER : HandlerNames.OUTBOUND_CONFIG,
            ViaEncodeHandler.NAME,
            new ViaEncodeHandler(user)
        );
        pipeline.addBefore(
            clientSide ? HandlerNames.INBOUND_CONFIG : HandlerNames.DECODER,
            ViaDecodeHandler.NAME,
            new ViaDecodeHandler(user)
        );
    }

    /** The client-side platform: logging, data folder and config. Scheduling uses ViaVersion's own pool. */
    private static final class Platform extends UserConnectionViaVersionPlatform {
        private static final Logger LOGGER = Logger.getLogger("ViaVersion");

        Platform() {
            super(new File(MeteorClient.FOLDER, "viaversion"));
        }

        @Override
        public Logger createLogger(String name) {
            return LOGGER;
        }

        @Override
        public String getPlatformName() {
            return "Meteor Client";
        }

        @Override
        public String getPlatformVersion() {
            return MeteorClient.VERSION.toString();
        }
    }
}