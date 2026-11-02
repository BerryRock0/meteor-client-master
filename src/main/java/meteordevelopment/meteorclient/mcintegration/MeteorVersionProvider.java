/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.mcintegration;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.protocol.version.BaseVersionProvider;
import meteordevelopment.meteorclient.compat.Compat;

/**
 * Decides, for each new connection, which client version this Minecraft instance speaks and which
 * server version to translate to.
 *
 * <p>The client version is always the protocol version of the running Minecraft build (resolved
 * reflectively through {@link Compat}, so this class stays Minecraft-version independent). The
 * server version comes from the Protocol screen: {@link Compat#getProtocolVersion()} returns the
 * override the user picked, falling back to the running version when no override is set (i.e. no
 * translation).</p>
 */
final class MeteorVersionProvider extends BaseVersionProvider {

    @Override
    public ProtocolVersion getClientProtocol(UserConnection connection) {
        // Always report the real client version, regardless of the handshake version we sent.
        return ProtocolVersion.getProtocol(Compat.get().protocolVersion());
    }

    @Override
    public ProtocolVersion getClosestServerProtocol(UserConnection connection) {
        // The Protocol screen override, or the client's own version when no override is set.
        return ProtocolVersion.getProtocol(Compat.getProtocolVersion());
    }
}
