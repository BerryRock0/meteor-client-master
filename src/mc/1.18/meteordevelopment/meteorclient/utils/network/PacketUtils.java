/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.utils.network;

import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.resources.Identifier;

import java.util.Collections;
import java.util.Set;

/** Compatibility stub: 26.x packet registry constants are unavailable on older versions. */
public final class PacketUtils {
    private static final Set<PacketType<? extends Packet<?>>> EMPTY = Collections.emptySet();

    private PacketUtils() {}

    public static Set<PacketType<? extends Packet<?>>> getPackets() { return EMPTY; }
    public static Set<PacketType<? extends Packet<?>>> getClientboundPackets() { return EMPTY; }
    public static Set<PacketType<? extends Packet<?>>> getServerboundPackets() { return EMPTY; }
    public static PacketType<? extends Packet<?>> getClientboundPacket(Identifier id) { return null; }
    public static PacketType<? extends Packet<?>> getServerboundPacket(Identifier id) { return null; }
    public static PacketType<? extends Packet<?>> getPacket(Identifier id) { return null; }
    public static PacketType<? extends Packet<?>> getPacket(String name) { return null; }
}
