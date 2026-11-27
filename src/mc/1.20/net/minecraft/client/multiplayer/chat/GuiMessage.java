package net.minecraft.client.multiplayer.chat;

import net.minecraft.network.chat.Component;

/** Compatibility placeholder for the chat message type introduced in 26.x. */
public class GuiMessage {
    public static class Line {
        public GuiMessageTag tag() { return null; }
    }

    public Component content() { return null; }
}
