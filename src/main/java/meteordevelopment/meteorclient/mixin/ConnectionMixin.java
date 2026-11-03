/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.mixin;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.timeout.TimeoutException;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.compat.Compat;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.ServerConnectEndEvent;
import meteordevelopment.meteorclient.mcintegration.MeteorVia;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.misc.AntiPacketKick;
import meteordevelopment.meteorclient.systems.modules.world.HighwayBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.network.BandwidthDebugMonitor;
import net.minecraft.network.Connection;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.PacketListener;
import net.minecraft.network.ProtocolInfo;
import net.minecraft.network.SkipPacketEncoderException;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.BundlerInfo;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBundlePacket;
import net.minecraft.network.protocol.handshake.ClientIntentionPacket;
import net.minecraft.server.network.EventLoopGroupHolder;
import io.netty.buffer.ByteBuf;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.net.InetSocketAddress;
import java.util.Iterator;

@Mixin(Connection.class)
public abstract class ConnectionMixin {
    @Inject(method = "configureSerialization(Lio/netty/channel/ChannelPipeline;Lnet/minecraft/network/protocol/PacketFlow;ZLnet/minecraft/network/BandwidthDebugMonitor;)V", at = @At("RETURN"))
    private static void onConfigureSerialization(ChannelPipeline pipeline, PacketFlow side, boolean local, BandwidthDebugMonitor monitor, CallbackInfo ci) {
        MeteorVia.injectPipeline(pipeline, side);
    }

    @Inject(method = "channelRead0(Lio/netty/channel/ChannelHandlerContext;Lnet/minecraft/network/protocol/Packet;)V",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/network/Connection;genericsFtw(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/PacketListener;)V", shift = At.Shift.BEFORE), cancellable = true)
    private void onHandlePacket(ChannelHandlerContext ctx, Packet<?> packet, CallbackInfo ci) {
        if (packet instanceof ClientboundBundlePacket bundle) {
            for (Iterator<Packet<? super ClientGamePacketListener>> it = bundle.subPackets().iterator(); it.hasNext(); ) {
                if (MeteorClient.EVENT_BUS.post(new PacketEvent.Receive(it.next(), (Connection) (Object) this)).isCancelled())
                    it.remove();
            }
        } else if (MeteorClient.EVENT_BUS.post(new PacketEvent.Receive(packet, (Connection) (Object) this)).isCancelled())
            ci.cancel();
    }

    @Inject(method = "disconnect(Lnet/minecraft/network/chat/Component;)V", at = @At("HEAD"))
    private void disconnect(Component reason, CallbackInfo ci) {
        if (Modules.get().get(HighwayBuilder.class).isActive()) {
            MutableComponent text = Component.literal("%n%n%s[%sHighway Builder%s] Statistics:%n".formatted(ChatFormatting.GRAY, ChatFormatting.BLUE, ChatFormatting.GRAY));
            text.append(Modules.get().get(HighwayBuilder.class).getStatsText());

            ((MutableComponent) reason).append(text);
        }
    }

    @Inject(method = "connect(Ljava/net/InetSocketAddress;Lnet/minecraft/server/network/EventLoopGroupHolder;Lnet/minecraft/network/Connection;)Lio/netty/channel/ChannelFuture;", at = @At("HEAD"))
    private static void onConnect(InetSocketAddress address, EventLoopGroupHolder eventLoopGroupHolder, Connection connection, CallbackInfoReturnable<ChannelFuture> cir) {
        MeteorClient.EVENT_BUS.post(ServerConnectEndEvent.get(address));
    }

    @Inject(at = @At("HEAD"), method = "send(Lnet/minecraft/network/protocol/Packet;Lio/netty/channel/ChannelFutureListener;)V", cancellable = true)
    private void onSendPacketHead(Packet<?> packet, @Nullable ChannelFutureListener listener, CallbackInfo ci) {
        if (MeteorClient.EVENT_BUS.post(new PacketEvent.Send(packet, (Connection) (Object) this)).isCancelled()) {
            ci.cancel();
        }
    }

    @Inject(method = "send(Lnet/minecraft/network/protocol/Packet;Lio/netty/channel/ChannelFutureListener;)V", at = @At("TAIL"))
    private void onSendPacketTail(Packet<?> packet, @Nullable ChannelFutureListener listener, CallbackInfo ci) {
        MeteorClient.EVENT_BUS.post(new PacketEvent.Sent(packet, (Connection) (Object) this));
    }

    // Uses the protocol version from the compatibility layer (see Compat#setProtocolVersion) instead
    // of the one baked into the running Minecraft version whenever a connection is initiated.
    //
    // The replacement happens at the head of Connection#doSendPacket rather than at the call site in
    // Connection#sendPacket: sendPacket either invokes doSendPacket directly (when already on the
    // event loop) or defers the very same invocation to the event loop via a lambda. Handshakes are
    // sent off the event loop, so only hooking doSendPacket guarantees the overridden version
    // actually reaches the wire on both paths.
    @ModifyVariable(method = "doSendPacket", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private Packet<?> onSendPacketProtocolVersion(Packet<?> packet) {
        if (packet instanceof ClientIntentionPacket intention) {
            int version = Compat.getProtocolVersion();
            MeteorClient.LOG.info("Protocol version override: sending ClientIntentionPacket with version {}", version);
            return new ClientIntentionPacket(version, intention.hostName(), intention.port(), intention.intention());
        }
        return packet;
    }

    // Replaces the outbound protocol info with one that carries the protocol version from the
    // compatibility layer (see Compat#setProtocolVersion), so that the connection itself is bound to
    // the overridden protocol version and not only the sent ClientIntentionPacket.
    @ModifyArgs(
        method = "lambda$initiateServerboundConnection$0",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/network/Connection;setupOutboundProtocol(Lnet/minecraft/network/ProtocolInfo;)V")
    )
    private void onSetupOutboundProtocolVersion(Args args) {
        ProtocolInfo<?> protocolInfo = args.get(0);
        if (protocolInfo == null) {
            return;
        }
        args.set(0, protocolInfoForVersion(protocolInfo));
    }

    private ProtocolInfo<PacketListener> protocolInfoForVersion(ProtocolInfo<?> original) {
        return new ProtocolInfo<PacketListener>() {
            @Override
            public ConnectionProtocol id() { return original.id(); }

            @Override
            public PacketFlow flow() { return original.flow(); }

            @Override
            public StreamCodec<ByteBuf, Packet<? super PacketListener>> codec() { return (StreamCodec<ByteBuf, Packet<? super PacketListener>>) (Object) original.codec(); }

            @Override
            public BundlerInfo bundlerInfo() { return original.bundlerInfo(); }
        };
    }

    @Inject(method = "exceptionCaught", at = @At("HEAD"), cancellable = true)
    private void exceptionCaught(ChannelHandlerContext ctx, Throwable cause, CallbackInfo ci) {
        AntiPacketKick apk = Modules.get().get(AntiPacketKick.class);
        if (!(cause instanceof TimeoutException) && !(cause instanceof SkipPacketEncoderException) && apk.catchExceptions()) {
            if (apk.logExceptions.get()) apk.warning("Caught exception: %s", cause);
            ci.cancel();
        }
    }

}
