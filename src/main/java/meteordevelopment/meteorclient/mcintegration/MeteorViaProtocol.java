/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.mcintegration;

import com.viaversion.viaversion.api.protocol.AbstractProtocol;
import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;
import com.viaversion.viaversion.api.protocol.packet.ServerboundPacketType;
import com.viaversion.viaversion.api.protocol.packet.provider.PacketTypesProvider;
import com.viaversion.viaversion.api.protocol.packet.provider.SimplePacketTypesProvider;
import com.viaversion.viaversion.protocols.v1_21_11to26_1.packet.ClientboundPacket26_1;
import com.viaversion.viaversion.protocols.v1_21_11to26_1.packet.ClientboundPackets26_1;
import com.viaversion.viaversion.protocols.v1_21_11to26_1.packet.ServerboundPacket26_1;
import com.viaversion.viaversion.protocols.v1_21_11to26_1.packet.ServerboundPackets26_1;
import com.viaversion.viaversion.protocols.v1_21_7to1_21_9.packet.ClientboundConfigurationPackets1_21_9;
import com.viaversion.viaversion.protocols.v1_21_7to1_21_9.packet.ServerboundConfigurationPackets1_21_9;

import static com.viaversion.viaversion.util.ProtocolUtil.packetTypeMap;

/**
 * The "base protocol" describing the packet structure of the running Minecraft client, so the
 * vendored ViaVersion engine knows how to parse and serialize this client's own packets.
 *
 * <p>Mirrors ViaFabric's {@code ViaFabricProtocol}. Minecraft 26.1 and 26.2 share the same packet
 * types (no new packets were introduced between them), so {@code ClientboundPacket26_1} is used for
 * both. Building for a Minecraft version that introduced new packets (26.3+) requires pointing this
 * at that version's packet types instead.</p>
 *
 * <p>The address-parsing and Fabric particle fix from ViaFabric are intentionally omitted: this
 * client drives the target server version through the Protocol screen ({@code Compat}), not through
 * a version suffix in the server address.</p>
 */
final class MeteorViaProtocol extends AbstractProtocol<ClientboundPacket26_1, ClientboundPacket26_1, ServerboundPacket26_1, ServerboundPacket26_1> {

    static final MeteorViaProtocol INSTANCE = new MeteorViaProtocol();

    private MeteorViaProtocol() {
        super(ClientboundPacket26_1.class, ClientboundPacket26_1.class, ServerboundPacket26_1.class, ServerboundPacket26_1.class);
    }

    @Override
    protected PacketTypesProvider<ClientboundPacket26_1, ClientboundPacket26_1, ServerboundPacket26_1, ServerboundPacket26_1> createPacketTypesProvider() {
        return new SimplePacketTypesProvider<>(
            packetTypeMap(unmappedClientboundPacketType, ClientboundPackets26_1.class, ClientboundConfigurationPackets1_21_9.class),
            packetTypeMap(mappedClientboundPacketType, ClientboundPackets26_1.class, ClientboundConfigurationPackets1_21_9.class),
            packetTypeMap(mappedServerboundPacketType, ServerboundPackets26_1.class, ServerboundConfigurationPackets1_21_9.class),
            packetTypeMap(unmappedServerboundPacketType, ServerboundPackets26_1.class, ServerboundConfigurationPackets1_21_9.class)
        );
    }

    @Override
    protected void applySharedRegistrations() {
        // Not for us; the translation protocols down the line already track connection states.
    }
}
