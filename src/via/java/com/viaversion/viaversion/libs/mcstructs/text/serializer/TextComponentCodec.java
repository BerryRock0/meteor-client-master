package com.viaversion.viaversion.libs.mcstructs.text.serializer;

import com.google.gson.*;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;
import com.viaversion.viaversion.libs.mcstructs.converter.DataConverter;
import com.viaversion.viaversion.libs.mcstructs.converter.codec.Codec;
import com.viaversion.viaversion.libs.mcstructs.converter.impl.v1_20_3.JsonConverter_v1_20_3;
import com.viaversion.viaversion.libs.mcstructs.converter.impl.v1_20_3.NbtConverter_v1_20_3;
import com.viaversion.viaversion.libs.mcstructs.converter.impl.v1_20_5.JsonConverter_v1_20_5;
import com.viaversion.viaversion.libs.mcstructs.converter.impl.v1_21_5.NbtConverter_v1_21_5;
import com.viaversion.viaversion.libs.mcstructs.converter.impl.v26_2.NbtConverter_v26_2;
import com.viaversion.nbt.tag.*;
import com.viaversion.nbt.tag.*;
import com.viaversion.viaversion.libs.mcstructs.snbt.SNbt;
import com.viaversion.viaversion.libs.mcstructs.snbt.exceptions.SNbtDeserializeException;
import com.viaversion.viaversion.libs.mcstructs.snbt.exceptions.SNbtSerializeException;
import com.viaversion.viaversion.libs.mcstructs.text.Style;
import com.viaversion.viaversion.libs.mcstructs.text.TextComponent;
import com.viaversion.viaversion.libs.mcstructs.text.serializer.v1_20_3.StyleCodecs_v1_20_3;
import com.viaversion.viaversion.libs.mcstructs.text.serializer.v1_20_3.TextCodecs_v1_20_3;
import com.viaversion.viaversion.libs.mcstructs.text.serializer.v1_20_5.StyleCodecs_v1_20_5;
import com.viaversion.viaversion.libs.mcstructs.text.serializer.v1_20_5.TextCodecs_v1_20_5;
import com.viaversion.viaversion.libs.mcstructs.text.serializer.v1_21_2.StyleCodecs_v1_21_2;
import com.viaversion.viaversion.libs.mcstructs.text.serializer.v1_21_2.TextCodecs_v1_21_2;
import com.viaversion.viaversion.libs.mcstructs.text.serializer.v1_21_4.StyleCodecs_v1_21_4;
import com.viaversion.viaversion.libs.mcstructs.text.serializer.v1_21_4.TextCodecs_v1_21_4;
import com.viaversion.viaversion.libs.mcstructs.text.serializer.v1_21_5.StyleCodecs_v1_21_5;
import com.viaversion.viaversion.libs.mcstructs.text.serializer.v1_21_5.TextCodecs_v1_21_5;
import com.viaversion.viaversion.libs.mcstructs.text.serializer.v1_21_6.StyleCodecs_v1_21_6;
import com.viaversion.viaversion.libs.mcstructs.text.serializer.v1_21_6.TextCodecs_v1_21_6;
import com.viaversion.viaversion.libs.mcstructs.text.serializer.v1_21_9.StyleCodecs_v1_21_9;
import com.viaversion.viaversion.libs.mcstructs.text.serializer.v1_21_9.TextCodecs_v1_21_9;
import com.viaversion.viaversion.libs.mcstructs.text.serializer.v26_1.StyleCodecs_v26_1;
import com.viaversion.viaversion.libs.mcstructs.text.serializer.v26_1.TextCodecs_v26_1;
import com.viaversion.viaversion.libs.mcstructs.text.serializer.v26_2.StyleCodecs_v26_2;
import com.viaversion.viaversion.libs.mcstructs.text.serializer.v26_2.TextCodecs_v26_2;
import com.viaversion.viaversion.libs.mcstructs.text.serializer.verify.TextVerifier;
import com.viaversion.viaversion.libs.mcstructs.text.serializer.verify.VerifyingConverter;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.StringReader;
import java.util.function.Supplier;

/**
 * The text component serializer and deserializer wrapper class for multiple types of input and output.<br>
 * Use the static default fields for a specific minecraft version or create your own serializer/deserializer.<br>
 * <br>
 * This class will now be used for implementations of new minecraft versions (1.20.3+) instead of the {@link TextComponentSerializer} class because components can now be serialized to nbt.<br>
 * Backwards compatibility is supported through the {@link #asSerializer()} method. The fields in {@link TextComponentSerializer} will still be updated using this wrapper method.
 */
@ParametersAreNonnullByDefault
public class TextComponentCodec {

    private static final Gson GSON = new GsonBuilder().disableHtmlEscaping().create();
    /**
     * The text codec for 1.20.3.
     */
    public static final TextComponentCodec V1_20_3 = new TextComponentCodec(() -> SNbt.V1_14, () -> TextCodecs_v1_20_3.TEXT, () -> StyleCodecs_v1_20_3.CODEC, JsonConverter_v1_20_3.INSTANCE, NbtConverter_v1_20_3.INSTANCE);
    /**
     * The text codec for 1.20.5.
     */
    public static final TextComponentCodec V1_20_5 = new TextComponentCodec(() -> SNbt.V1_14, () -> TextCodecs_v1_20_5.TEXT, () -> StyleCodecs_v1_20_5.CODEC, JsonConverter_v1_20_5.INSTANCE, NbtConverter_v1_20_3.INSTANCE);
    /**
     * The text codec for 1.21.2.
     */
    public static final TextComponentCodec V1_21_2 = new TextComponentCodec(() -> SNbt.V1_14, () -> TextCodecs_v1_21_2.TEXT, () -> StyleCodecs_v1_21_2.CODEC, JsonConverter_v1_20_5.INSTANCE, NbtConverter_v1_20_3.INSTANCE);
    /**
     * The text codec for 1.21.4.
     */
    public static final TextComponentCodec V1_21_4 = new TextComponentCodec(() -> SNbt.V1_14, () -> TextCodecs_v1_21_4.TEXT, () -> StyleCodecs_v1_21_4.CODEC, JsonConverter_v1_20_5.INSTANCE, NbtConverter_v1_20_3.INSTANCE);
    /**
     * The text codec for 1.21.5.
     */
    public static final TextComponentCodec V1_21_5 = new TextComponentCodec(() -> SNbt.V1_21_5, () -> TextCodecs_v1_21_5.TEXT, () -> StyleCodecs_v1_21_5.CODEC, JsonConverter_v1_20_5.INSTANCE, NbtConverter_v1_21_5.INSTANCE);
    /**
     * The text codec for 1.21.6.
     */
    public static final TextComponentCodec V1_21_6 = new TextComponentCodec(() -> SNbt.V1_21_5, () -> TextCodecs_v1_21_6.TEXT, () -> StyleCodecs_v1_21_6.CODEC, JsonConverter_v1_20_5.INSTANCE, NbtConverter_v1_21_5.INSTANCE);
    /**
     * The text codec for 1.21.9.
     */
    public static final TextComponentCodec V1_21_9 = new TextComponentCodec(() -> SNbt.V1_21_5, () -> TextCodecs_v1_21_9.TEXT, () -> StyleCodecs_v1_21_9.CODEC, JsonConverter_v1_20_5.INSTANCE, NbtConverter_v1_21_5.INSTANCE);
    /**
     * The text codec for 26.1.
     */
    public static final TextComponentCodec V26_1 = new TextComponentCodec(() -> SNbt.V1_21_5, () -> TextCodecs_v26_1.TEXT, () -> StyleCodecs_v26_1.CODEC, JsonConverter_v1_20_5.INSTANCE, NbtConverter_v1_21_5.INSTANCE);
    /**
     * The text codec for 26.2.
     */
    public static final TextComponentCodec V26_2 = new TextComponentCodec(() -> SNbt.V1_21_5, () -> TextCodecs_v26_2.TEXT, () -> StyleCodecs_v26_2.CODEC, JsonConverter_v1_20_5.INSTANCE, NbtConverter_v26_2.INSTANCE);
    /**
     * The latest text codec.
     */
    public static final TextComponentCodec LATEST = V26_2;


    private final Supplier<SNbt<CompoundTag>> sNbtSupplier;
    private final Supplier<Codec<TextComponent>> textCodecSupplier;
    private final Supplier<Codec<Style>> styleCodecSupplier;
    private final DataConverter<JsonElement> jsonConverter;
    private final DataConverter<Tag> nbtConverter;
    private SNbt<CompoundTag> sNbt;
    private Codec<TextComponent> textCodec;
    private Codec<Style> styleCodec;

    public TextComponentCodec(final Supplier<SNbt<CompoundTag>> sNbtSupplier, final Supplier<Codec<TextComponent>> textCodec, final Supplier<Codec<Style>> styleCodec, final DataConverter<JsonElement> jsonConverter, final DataConverter<Tag> nbtConverter) {
        this.sNbtSupplier = sNbtSupplier;
        this.textCodecSupplier = textCodec;
        this.styleCodecSupplier = styleCodec;
        this.jsonConverter = jsonConverter;
        this.nbtConverter = nbtConverter;
    }

    /**
     * @return The used snbt serializer
     */
    private SNbt<CompoundTag> getSNbtSerializer() {
        if (this.sNbt == null) this.sNbt = this.sNbtSupplier.get();
        return this.sNbtSupplier.get();
    }

    /**
     * @return The used text codec
     */
    public Codec<TextComponent> getTextCodec() {
        if (this.textCodec == null) this.textCodec = this.textCodecSupplier.get();
        return this.textCodec;
    }

    /**
     * @return The used style codec
     */
    public Codec<Style> getStyleCodec() {
        if (this.styleCodec == null) this.styleCodec = this.styleCodecSupplier.get();
        return this.styleCodec;
    }

    /**
     * @return The used json converter
     */
    public DataConverter<JsonElement> getJsonConverter() {
        return this.jsonConverter;
    }

    /**
     * @return The used nbt converter
     */
    public DataConverter<Tag> getNbtConverter() {
        return this.nbtConverter;
    }

    /**
     * Create a new text component codec with the given text verifier.<br>
     * The verifier is used to check if the text component is valid during serialization/deserialization.<br>
     * By default, no verifier is used and all text components are valid.
     *
     * @param verifier The text verifier
     * @return The new text component codec
     */
    public TextComponentCodec withVerifier(final TextVerifier verifier) {
        return new TextComponentCodec(this.sNbtSupplier, this.textCodecSupplier, this.styleCodecSupplier, new VerifyingConverter<>(this.jsonConverter, verifier), new VerifyingConverter<>(this.nbtConverter, verifier));
    }

    /**
     * Deserialize a text component from a json string.
     *
     * @param json The json string
     * @return The deserialized text component
     */
    public TextComponent deserializeJson(final String json) {
        return this.deserializeJsonTree(JsonParser.parseString(json));
    }

    /**
     * Deserialize a text component from a json string.<br>
     * A {@link JsonReader} will be used to parse the json string.
     *
     * @param json The json string
     * @return The deserialized text component
     */
    public TextComponent deserializeJsonReader(final String json) {
        JsonReader reader = new JsonReader(new StringReader(json));
        reader.setLenient(false);
        try {
            return this.deserialize(Streams.parse(reader));
        } catch (StackOverflowError e) {
            throw new JsonParseException("Failed parsing JSON source: " + reader + " to Json", e);
        }
    }

    /**
     * Deserialize a text component from a json string.<br>
     * A {@link JsonReader} will be used to parse the json string.
     *
     * @param json The json string
     * @return The deserialized text component
     */
    public TextComponent deserializeLenientJson(final String json) {
        JsonReader reader = new JsonReader(new StringReader(json));
        reader.setLenient(true);
        return this.deserializeJsonTree(JsonParser.parseReader(reader));
    }

    /**
     * Deserialize a text component from a nbt string.
     *
     * @param nbt The nbt string
     * @return The deserialized text component
     */
    public TextComponent deserializeNbt(final String nbt) {
        try {
            return this.deserialize(this.getSNbtSerializer().getDeserializer().deserializeValue(nbt));
        } catch (SNbtDeserializeException e) {
            throw new RuntimeException("Failed to deserialize SNbt", e);
        }
    }

    /**
     * Deserialize a text component from a {@link JsonElement}.
     *
     * @param element The json element
     * @return The deserialized text component
     */
    public TextComponent deserializeJsonTree(@Nullable final JsonElement element) {
        if (element == null) return null;
        return this.deserialize(element);
    }

    /**
     * Deserialize a text component from a {@link Tag}.
     *
     * @param nbt The nbt tag
     * @return The deserialized text component
     */
    public TextComponent deserializeNbtTree(@Nullable final Tag nbt) {
        if (nbt == null) return null;
        return this.deserialize(nbt);
    }

    /**
     * Deserialize a text component from a {@link JsonElement}.<br>
     * This method does not check for null values.
     *
     * @param json The json element
     * @return The deserialized text component
     */
    public TextComponent deserialize(final JsonElement json) {
        return this.getTextCodec().deserialize(this.jsonConverter, json).getOrThrow(JsonParseException::new);
    }

    /**
     * Deserialize a text component from a {@link Tag}.<br>
     * This method does not check for null values.
     *
     * @param nbt The nbt tag
     * @return The deserialized text component
     */
    public TextComponent deserialize(final Tag nbt) {
        return this.getTextCodec().deserialize(this.nbtConverter, nbt).get();
    }

    /**
     * Serialize a text component to a {@link JsonElement}.
     *
     * @param component The text component
     * @return The serialized json element
     */
    public JsonElement serializeJsonTree(final TextComponent component) {
        return this.getTextCodec().serialize(this.jsonConverter, component).getOrThrow(JsonParseException::new);
    }

    /**
     * Serialize a text component to a {@link Tag}.
     *
     * @param component The text component
     * @return The serialized nbt tag
     */
    public Tag serializeNbtTree(final TextComponent component) {
        return this.getTextCodec().serialize(this.nbtConverter, component).get();
    }

    /**
     * Serialize a text component to a json string.
     *
     * @param component The text component
     * @return The serialized json string
     */
    public String serializeJsonString(final TextComponent component) {
        return GSON.toJson(this.serializeJsonTree(component));
    }

    /**
     * Serialize a text component to a nbt string.
     *
     * @param component The text component
     * @return The serialized nbt string
     */
    public String serializeNbtString(final TextComponent component) {
        try {
            return this.getSNbtSerializer().serialize(this.serializeNbtTree(component));
        } catch (SNbtSerializeException e) {
            throw new RuntimeException("Failed to serialize SNbt", e);
        }
    }

    /**
     * @return A wrapper for this codec to use it as a {@link TextComponentSerializer}.
     */
    public TextComponentSerializer asSerializer() {
        return new TextComponentSerializer(this, () -> new GsonBuilder()
                .registerTypeHierarchyAdapter(TextComponent.class, (JsonSerializer<TextComponent>) (src, typeOfSrc, context) -> this.serializeJsonTree(src))
                .registerTypeHierarchyAdapter(TextComponent.class, (JsonDeserializer<TextComponent>) (src, typeOfSrc, context) -> this.deserializeJsonTree(src))
                .disableHtmlEscaping()
                .create());
    }

}
