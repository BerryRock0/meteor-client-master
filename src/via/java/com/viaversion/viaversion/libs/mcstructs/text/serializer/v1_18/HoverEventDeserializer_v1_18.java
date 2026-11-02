package com.viaversion.viaversion.libs.mcstructs.text.serializer.v1_18;

import com.viaversion.viaversion.libs.mcstructs.core.Identifier;
import com.viaversion.nbt.tag.*;
import com.viaversion.viaversion.libs.mcstructs.snbt.SNbt;
import com.viaversion.viaversion.libs.mcstructs.text.TextComponent;
import com.viaversion.viaversion.libs.mcstructs.text.events.hover.HoverEvent;
import com.viaversion.viaversion.libs.mcstructs.text.events.hover.HoverEventAction;
import com.viaversion.viaversion.libs.mcstructs.text.events.hover.impl.EntityHoverEvent;
import com.viaversion.viaversion.libs.mcstructs.text.serializer.TextComponentSerializer;
import com.viaversion.viaversion.libs.mcstructs.text.serializer.v1_16.HoverEventDeserializer_v1_16;

import java.util.UUID;

public class HoverEventDeserializer_v1_18 extends HoverEventDeserializer_v1_16 {

    public HoverEventDeserializer_v1_18(final TextComponentSerializer textComponentSerializer, final SNbt<?> sNbt) {
        super(textComponentSerializer, sNbt);
    }

    protected HoverEvent deserializeLegacy(final HoverEventAction action, final TextComponent text) {
        if (action == HoverEventAction.SHOW_ENTITY) {
            try {
                this.textComponentSerializer = this.textComponentSerializer.forkIfDefault();
                CompoundTag rawEntity = (CompoundTag) this.sNbt.deserialize(text.asUnformattedString(this.textComponentSerializer));
                TextComponent name = this.textComponentSerializer.deserialize((rawEntity.get("name") instanceof StringTag ? ((StringTag) rawEntity.get("name")).getValue() : ""));
                Identifier entityType = Identifier.of((rawEntity.get("type") instanceof StringTag ? ((StringTag) rawEntity.get("type")).getValue() : ""));
                UUID uuid = UUID.fromString((rawEntity.get("id") instanceof StringTag ? ((StringTag) rawEntity.get("id")).getValue() : ""));
                return new EntityHoverEvent(entityType, uuid, name);
            } catch (Exception ignored) {
                return null;
            }
        }
        return super.deserializeLegacy(action, text);
    }

}
