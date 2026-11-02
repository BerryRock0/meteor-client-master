package com.viaversion.viaversion.libs.mcstructs.text.serializer.v1_16;

import com.google.gson.*;
import com.viaversion.viaversion.libs.mcstructs.core.Identifier;
import com.viaversion.nbt.tag.*;
import com.viaversion.nbt.tag.*;
import com.viaversion.viaversion.libs.mcstructs.snbt.SNbt;
import com.viaversion.viaversion.libs.mcstructs.snbt.exceptions.SNbtDeserializeException;
import com.viaversion.viaversion.libs.mcstructs.text.TextComponent;
import com.viaversion.viaversion.libs.mcstructs.text.events.hover.HoverEvent;
import com.viaversion.viaversion.libs.mcstructs.text.events.hover.HoverEventAction;
import com.viaversion.viaversion.libs.mcstructs.text.events.hover.impl.EntityHoverEvent;
import com.viaversion.viaversion.libs.mcstructs.text.events.hover.impl.ItemHoverEvent;
import com.viaversion.viaversion.libs.mcstructs.text.events.hover.impl.TextHoverEvent;
import com.viaversion.viaversion.libs.mcstructs.text.serializer.TextComponentSerializer;
import com.viaversion.viaversion.libs.mcstructs.text.utils.JsonUtils;

import java.lang.reflect.Type;
import java.util.UUID;

import static com.viaversion.viaversion.libs.mcstructs.text.utils.JsonUtils.getString;

public class HoverEventDeserializer_v1_16 implements JsonDeserializer<HoverEvent> {

    protected TextComponentSerializer textComponentSerializer;
    protected final SNbt<?> sNbt;

    public HoverEventDeserializer_v1_16(final TextComponentSerializer textComponentSerializer, final SNbt<?> sNbt) {
        this.textComponentSerializer = textComponentSerializer;
        this.sNbt = sNbt;
    }

    @Override
    public HoverEvent deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (!json.isJsonObject()) return null;
        JsonObject rawHoverEvent = json.getAsJsonObject();
        if (rawHoverEvent == null) return null;

        String rawAction = getString(rawHoverEvent, "action", null);
        if (rawAction == null) return null;
        HoverEventAction action = HoverEventAction.byName(rawAction);
        if (action == null) return null;
        JsonElement rawContents = rawHoverEvent.get("contents");
        if (rawContents != null) return this.deserialize(action, rawContents);
        TextComponent text = this.textComponentSerializer.deserialize(rawHoverEvent.get("value"));
        if (text == null) return null;
        return this.deserializeLegacy(action, text);
    }

    protected HoverEvent deserialize(final HoverEventAction action, final JsonElement contents) {
        switch (action) {
            case SHOW_TEXT:
                return new TextHoverEvent(this.textComponentSerializer.deserialize(contents));
            case SHOW_ITEM:
                if (contents.isJsonPrimitive()) return new ItemHoverEvent(Identifier.of(contents.getAsString()), 1, null);
                JsonObject rawItem = JsonUtils.getJsonObject(contents, "item");
                Identifier item = Identifier.of(getString(rawItem, "id"));
                int count = JsonUtils.getInt(rawItem, "count", 1);
                if (rawItem.has("tag")) {
                    String rawTag = getString(rawItem, "tag");
                    return new ItemHoverEvent(item, count, (CompoundTag) this.sNbt.tryDeserialize(rawTag));
                }
                return new ItemHoverEvent(item, count, null);
            case SHOW_ENTITY:
                if (!contents.isJsonObject()) return null;
                JsonObject rawEntity = contents.getAsJsonObject();
                Identifier entityType = Identifier.of(getString(rawEntity, "type"));
                UUID uuid = UUID.fromString(getString(rawEntity, "id"));
                TextComponent name = this.textComponentSerializer.deserialize(rawEntity.get("name"));
                return new EntityHoverEvent(entityType, uuid, name);
            default:
                return null;
        }
    }

    protected HoverEvent deserializeLegacy(final HoverEventAction action, final TextComponent text) {
        switch (action) {
            case SHOW_TEXT:
                return new TextHoverEvent(text);
            case SHOW_ITEM:
                CompoundTag rawTag = (CompoundTag) this.sNbt.tryDeserialize(text.asUnformattedString());
                if (rawTag == null) return null;
                Identifier id = Identifier.of((rawTag.get("id") instanceof StringTag ? ((StringTag) rawTag.get("id")).getValue() : ""));
                int count = (rawTag.get("count") instanceof ByteTag ? ((ByteTag) rawTag.get("count")).asByte() : 0);
                CompoundTag tag = null;
                if ((rawTag.get("tag") instanceof CompoundTag)) tag = (rawTag.get("tag") instanceof CompoundTag ? ((CompoundTag) rawTag.get("tag")) : new CompoundTag());
                return new ItemHoverEvent(id, count, tag);
            case SHOW_ENTITY:
                try {
                    this.textComponentSerializer = this.textComponentSerializer.forkIfDefault();
                    CompoundTag rawEntity = (CompoundTag) this.sNbt.deserialize(text.asUnformattedString(this.textComponentSerializer));
                    TextComponent name = this.textComponentSerializer.deserialize((rawEntity.get("name") instanceof StringTag ? ((StringTag) rawEntity.get("name")).getValue() : ""));
                    Identifier entityType = Identifier.of((rawEntity.get("type") instanceof StringTag ? ((StringTag) rawEntity.get("type")).getValue() : ""));
                    UUID uuid = UUID.fromString((rawEntity.get("id") instanceof StringTag ? ((StringTag) rawEntity.get("id")).getValue() : ""));
                    return new EntityHoverEvent(entityType, uuid, name);
                } catch (SNbtDeserializeException | JsonSyntaxException ignored) {
                    return null;
                }
            default:
                return null;
        }
    }

}
