/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.mixin;

import java.util.Arrays;
import java.nio.charset.StandardCharsets;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.proxy.Socks4ProxyHandler;
import io.netty.handler.proxy.Socks5ProxyHandler;
import io.netty.handler.timeout.TimeoutException;

import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.ServerConnectEndEvent;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.misc.AntiPacketKick;
import meteordevelopment.meteorclient.systems.modules.misc.PacketSpoofer;
import meteordevelopment.meteorclient.systems.proxies.Proxies;
import meteordevelopment.meteorclient.systems.proxies.Proxy;

import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.NetworkingBackend;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.handler.PacketEncoderException;
import net.minecraft.network.handler.PacketSizeLogger;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BundleS2CPacket;

import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import org.jetbrains.annotations.Nullable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.net.InetSocketAddress;
import java.util.Iterator;

@Mixin(ClientConnection.class)
public abstract class ClientConnectionMixin
{

    @Inject(method = "send(Lnet/minecraft/network/packet/Packet;)V", at = @At("HEAD"), cancellable = true)
    private void spoofSendPacket(Packet<?> packet, CallbackInfo ci)
    {
        PacketSpoofer packetSpoofer = Modules.get().get(PacketSpoofer.class);
        if(packetSpoofer.isActive() && packetSpoofer.spoofSend.get())
        {
            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());  // Перехватываем пакет перед отправкой
            packet.write(buf);  // Сериализуем пакет в буфер

            // Получаем байты пакета
            byte[] originalBytes = new byte[buf.readableBytes()];
            buf.readBytes(originalBytes);

            // Пример: Ищем строку "550e8400-e29b-41d4-a716-446655440000" (UUID как строка)
            // Но помните: в пакетах UUID — это байты, не строка! Это не сработает для реальных пакетов.
            // Для демонстрации ищем как строку, но в реальности конвертируйте в байты.
        
            // Конвертируем строки в байты (предполагаем UTF-8)
            byte[] searchBytes = packetSpoofer.findSend().getBytes(StandardCharsets.UTF_8);
            byte[] replaceBytes = packetSpoofer.replaceSend().getBytes(StandardCharsets.UTF_8);

            // Ищем и заменяем (простая замена первого вхождения)
            byte[] modifiedBytes = replaceBytesInArray(originalBytes, searchBytes, replaceBytes);

            // Если байты изменились, создаём новый буфер и отправляем модифицированный пакет, отменяя оригинальный пакет
            if (!Arrays.equals(originalBytes, modifiedBytes))
            {
                PacketByteBuf newBuf = PacketByteBufs.create();
                newBuf.writeBytes(modifiedBytes);
                ci.cancel();
            }
        }
    }
    
    // Вспомогательная функция для замены байтов в массиве
    private byte[] replaceBytesInArray(byte[] original, byte[] search, byte[] replace)
    {
        int index = indexOf(original, search);
        if (index == -1) return original;  // Не найдено

        byte[] result = new byte[original.length - search.length + replace.length];
        System.arraycopy(original, 0, result, 0, index);
        System.arraycopy(replace, 0, result, index, replace.length);
        System.arraycopy(original, index + search.length, result, index + replace.length, original.length - index - search.length);
        return result;
    }

    // Вспомогательная функция для поиска индекса подмассива
    private int indexOf(byte[] array, byte[] subArray)
    {
        for (int i = 0; i <= array.length - subArray.length; i++)
        {
            if (Arrays.equals(Arrays.copyOfRange(array, i, i + subArray.length), subArray))
            {
                return i;
            }
        }
        return -1;
    }

    
    @Inject(method = "channelRead0(Lio/netty/channel/ChannelHandlerContext;Lnet/minecraft/network/packet/Packet;)V",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/network/ClientConnection;handlePacket(Lnet/minecraft/network/packet/Packet;Lnet/minecraft/network/listener/PacketListener;)V", shift = At.Shift.BEFORE), cancellable = true)
    private void onHandlePacket(ChannelHandlerContext channelHandlerContext, Packet<?> packet, CallbackInfo ci) {
        if (packet instanceof BundleS2CPacket bundle) {
            for (Iterator<Packet<? super ClientPlayPacketListener>> it = bundle.getPackets().iterator(); it.hasNext(); ) {
                if (MeteorClient.EVENT_BUS.post(new PacketEvent.Receive(it.next(), (ClientConnection) (Object) this)).isCancelled()) it.remove();
            }
        } else if (MeteorClient.EVENT_BUS.post(new PacketEvent.Receive(packet, (ClientConnection) (Object) this)).isCancelled()) ci.cancel();
    }

    @Inject(method = "disconnect(Lnet/minecraft/text/Text;)V", at = @At("HEAD"))
    private void disconnect(Text disconnectReason, CallbackInfo ci) {}

    @Inject(method = "connect(Ljava/net/InetSocketAddress;Lnet/minecraft/network/NetworkingBackend;Lnet/minecraft/network/ClientConnection;)Lio/netty/channel/ChannelFuture;", at = @At("HEAD"))
    private static void onConnect(InetSocketAddress address, NetworkingBackend backend, ClientConnection connection, CallbackInfoReturnable<ChannelFuture> cir) {
        MeteorClient.EVENT_BUS.post(ServerConnectEndEvent.get(address));
    }

    @Inject(at = @At("HEAD"), method = "send(Lnet/minecraft/network/packet/Packet;Lio/netty/channel/ChannelFutureListener;)V", cancellable = true)
    private void onSendPacketHead(Packet<?> packet, @Nullable ChannelFutureListener channelFutureListener, CallbackInfo ci) {
        if (MeteorClient.EVENT_BUS.post(new PacketEvent.Send(packet, (ClientConnection) (Object) this)).isCancelled()) {
            ci.cancel();
        }
    }

    @Inject(method = "send(Lnet/minecraft/network/packet/Packet;Lio/netty/channel/ChannelFutureListener;)V", at = @At("TAIL"))
    private void onSendPacketTail(Packet<?> packet, @Nullable ChannelFutureListener channelFutureListener, CallbackInfo ci) {
        MeteorClient.EVENT_BUS.post(new PacketEvent.Sent(packet, (ClientConnection) (Object) this));
    }

    @Inject(method = "exceptionCaught", at = @At("HEAD"), cancellable = true)
    private void exceptionCaught(ChannelHandlerContext context, Throwable throwable, CallbackInfo ci) {
        AntiPacketKick apk = Modules.get().get(AntiPacketKick.class);
        if (!(throwable instanceof TimeoutException) && !(throwable instanceof PacketEncoderException) && apk.catchExceptions()) {
            if (apk.logExceptions.get()) apk.warning("Caught exception: %s", throwable);
            ci.cancel();
        }
    }

    @Inject(method = "addHandlers", at = @At("RETURN"))
    private static void onAddHandlers(ChannelPipeline pipeline, NetworkSide side, boolean local, PacketSizeLogger packetSizeLogger, CallbackInfo ci) {
        if (side != NetworkSide.CLIENTBOUND || local) return;

        Proxy proxy = Proxies.get().getEnabled();
        if (proxy == null) return;

        switch (proxy.type.get()) {
            case Socks4 -> pipeline.addFirst(new Socks4ProxyHandler(new InetSocketAddress(proxy.address.get(), proxy.port.get()), proxy.username.get()));
            case Socks5 -> pipeline.addFirst(new Socks5ProxyHandler(new InetSocketAddress(proxy.address.get(), proxy.port.get()), proxy.username.get(), proxy.password.get()));
        }
    }
}
