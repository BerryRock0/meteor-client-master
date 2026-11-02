package com.viaversion.viaversion.libs.mcstructs.text.serializer.v1_12;

import com.viaversion.viaversion.libs.mcstructs.snbt.SNbt;
import com.viaversion.viaversion.libs.mcstructs.text.TextComponent;
import com.viaversion.viaversion.libs.mcstructs.text.events.hover.HoverEvent;
import com.viaversion.viaversion.libs.mcstructs.text.events.hover.HoverEventAction;
import com.viaversion.viaversion.libs.mcstructs.text.serializer.legacy.HoverEventSerializer;
import com.viaversion.viaversion.libs.mcstructs.text.serializer.legacy.SerializerMap;
import com.viaversion.viaversion.libs.mcstructs.text.serializer.v1_9.StyleSerializer_v1_9;

public class StyleSerializer_v1_12 extends StyleSerializer_v1_9 {

    public StyleSerializer_v1_12(final SNbt<?> sNbt) {
        super(sNbt);
    }

    @Override
    protected SerializerMap<HoverEvent, HoverEventAction, TextComponent> createHoverEventSerializer(SerializerMap.Builder<HoverEvent, HoverEventAction, TextComponent> builder) {
        return builder
                .add(HoverEventSerializer.TEXT)
                .add(HoverEventSerializer.LEGACY_ITEM)
                .add(HoverEventSerializer.LEGACY_ENTITY)
                .finalize(HoverEvent::getAction);
    }

}
