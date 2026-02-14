/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.mixininterface;
import net.minecraft.network.packet.c2s.login.LoginHelloC2SPacket;

public interface ILoginHelloC2SPacket {
    LoginHelloC2SPacket.name meteor$setName(String name);

    LoginHelloC2SPacket.profileId meteor$setUuid(UUID uuid);
}
