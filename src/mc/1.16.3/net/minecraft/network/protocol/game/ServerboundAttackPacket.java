/*
 * Compatibility shim: 26.x attack packet replacement for the old attack/interact packet.
 */
package net.minecraft.network.protocol.game;

public record ServerboundAttackPacket(int entityId) implements net.minecraft.network.protocol.Packet {
    public void handle(net.minecraft.network.PacketListener listener) {}

    public net.minecraft.network.protocol.PacketType type() { return null; }

    public void write(io.netty.buffer.ByteBuf buf) {}
}
