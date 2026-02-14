/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.systems.modules.misc;

import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.misc.text.RunnableClickEvent;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.packet.BrandCustomPayload;
import net.minecraft.network.packet.c2s.common.CustomPayloadC2SPacket;
import net.minecraft.network.packet.c2s.common.ResourcePackStatusC2SPacket;
import net.minecraft.network.packet.s2c.common.ResourcePackSendS2CPacket;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.Strings;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

public class PacketSpoofer extends Module {
    private final SettingGroup sgSend = settings.createGroup("Send");
    private final SettingGroup sgSend = settings.createGroup("Receive");
    private final SettingGroup sgControl = settings.createGroup("Control");

    private final Setting<Boolean> spoofSend = sgSend.add(new BoolSetting.Builder()
        .name("spoof-send")
        .description("Whether or not to spoof in send packets.")
        .defaultValue(false)
        .build()
    );

    private final Setting<String> findSendString = sgSend.add(new StringSetting.Builder()
        .name("finding-strings")
        .description("If the packet contains bytes, this outgoing channel will be spofed.")
        .build()
    );

    private final Setting<String> replaceSendString = sgSend.add(new StringSetting.Builder()
        .name("replacing-strings")
        .description(" this outgoing channel will be blocked.")
        .build()
    );

    private final Setting<Boolean> spoofReceive = sgReceive.add(new BoolSetting.Builder()
        .name("spoof-receive")
        .description("Whether or not to spoof in send packets.")
        .defaultValue(false)
        .build()
    );

    private final Setting<String> findReceiveString = sgReceive.add(new StringSetting.Builder()
        .name("find-receive-strings")
        .description("If the packet contains bytes, this outgoing channel will be spofed.")
        .build()
    );

    private final Setting<String> replaceReceiveString = sgReceive.add(new StringSetting.Builder()
        .name("replacing-receive-strings")
        .description(" this outgoing channel will be blocked.")
        .build()
    );

  public PacketSpoofer()
  {
    super(Categories.Misc, "packet-spoofer", "Spoof packet bytes.");
  }
  
  public String findSend()
  {
    return findSendString.get();
  }

  public String replaceSend()
  {
    return replaceSendString.get();
  }

  public String findReceive()
  {
    return findReceiveString.get(); 
  }

  public String replaceReceive()
  {
    return repaceReceiveString.get();
  }
}
