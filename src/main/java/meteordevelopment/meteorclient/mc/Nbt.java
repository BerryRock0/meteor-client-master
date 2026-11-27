/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */
package meteordevelopment.meteorclient.mc;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;

import java.lang.reflect.Method;
import java.util.Optional;
import java.util.Set;

/** Version-agnostic replacements for CompoundTag helpers that changed around 1.21.5/26.x. */
public final class Nbt {
    private Nbt() {}

    @SuppressWarnings("unchecked")
    private static <T> T readValue(CompoundTag tag, String key, String method, Class<T> type, T fallback) {
        try {
            Object value = CompoundTag.class.getMethod(method, String.class).invoke(tag, key);
            if (value instanceof Optional<?> optional) return optional.map(type::cast).orElse(fallback);
            return value == null ? fallback : type.cast(value);
        } catch (Throwable t) {
            return fallback;
        }
    }

    public static ListTag getListOrEmpty(CompoundTag tag, String key) {
        Tag value = tag.get(key);
        return value instanceof ListTag list ? list : new ListTag();
    }

    public static CompoundTag getCompoundOrEmpty(CompoundTag tag, String key) {
        Tag value = tag.get(key);
        return value instanceof CompoundTag compound ? compound : new CompoundTag();
    }

    public static String getStringOr(CompoundTag tag, String key, String fallback) {
        return readValue(tag, key, "getString", String.class, fallback);
    }

    public static boolean getBooleanOr(CompoundTag tag, String key, boolean fallback) {
        return readValue(tag, key, "getBoolean", Boolean.class, fallback);
    }

    public static int getIntOr(CompoundTag tag, String key, int fallback) {
        return readValue(tag, key, "getInt", Integer.class, fallback);
    }

    public static double getDoubleOr(CompoundTag tag, String key, double fallback) {
        return readValue(tag, key, "getDouble", Double.class, fallback);
    }

    public static Optional<CompoundTag> compound(CompoundTag tag, String key) {
        Tag value = tag.get(key);
        return value instanceof CompoundTag c ? Optional.of(c) : Optional.empty();
    }

    public static Optional<String> string(CompoundTag tag, String key) {
        return Optional.ofNullable(readValue(tag, key, "getString", String.class, null));
    }

    public static Optional<Boolean> bool(CompoundTag tag, String key) {
        return Optional.ofNullable(readValue(tag, key, "getBoolean", Boolean.class, null));
    }

    public static Optional<Integer> integer(CompoundTag tag, String key) {
        return Optional.ofNullable(readValue(tag, key, "getInt", Integer.class, null));
    }

    public static Optional<Double> decimal(CompoundTag tag, String key) {
        return Optional.ofNullable(readValue(tag, key, "getDouble", Double.class, null));
    }

    public static Optional<Byte> byteValue(CompoundTag tag, String key) {
        return Optional.ofNullable(readValue(tag, key, "getByte", Byte.class, null));
    }

    @SuppressWarnings("unchecked")
    public static Set<String> keys(CompoundTag tag) {
        for (String method : new String[] { "keySet", "getAllKeys" }) {
            try {
                Method m = CompoundTag.class.getMethod(method);
                return (Set<String>) m.invoke(tag);
            } catch (Throwable ignored) {
            }
        }
        return Set.of();
    }

    public static Optional<String> asString(Tag tag) {
        if (tag == null) return Optional.empty();
        for (String method : new String[] { "getAsString", "asString" }) {
            try {
                Object value = Tag.class.getMethod(method).invoke(tag);
                if (value instanceof Optional<?> optional) return optional.map(Object::toString);
                return Optional.of(String.valueOf(value));
            } catch (Throwable ignored) {
            }
        }
        return Optional.empty();
    }

    public static <T> Optional<T> read(CompoundTag tag, String key, Codec<T> codec) {
        Tag value = tag.get(key);
        if (value == null) return Optional.empty();
        DataResult<T> result = codec.parse(NbtOps.INSTANCE, value);
        return result.result();
    }

    public static <T> void store(CompoundTag tag, String key, Codec<T> codec, T value) {
        DataResult<Tag> result = codec.encodeStart(NbtOps.INSTANCE, value);
        result.result().ifPresent(encoded -> tag.put(key, encoded));
    }

    public static Optional<CompoundTag> listCompound(ListTag list, int index) {
        try {
            Object value = ListTag.class.getMethod("getCompound", int.class).invoke(list, index);
            if (value instanceof Optional<?> optional) return optional.map(o -> (CompoundTag) o);
            return Optional.ofNullable((CompoundTag) value);
        } catch (Throwable t) {
            return Optional.empty();
        }
    }

    public static String listString(ListTag list, int index) {
        try {
            Object value = ListTag.class.getMethod("getString", int.class).invoke(list, index);
            if (value instanceof Optional<?> optional) return optional.map(Object::toString).orElse("");
            return value == null ? "" : String.valueOf(value);
        } catch (Throwable t) {
            return "";
        }
    }

    public static StringTag string(String value) {
        return StringTag.valueOf(value);
    }
}
