package com.viaversion.viaversion.libs.mcstructs.converter.impl.v1_20_3;

import com.viaversion.viaversion.libs.mcstructs.converter.DataConverter;
import com.viaversion.viaversion.libs.mcstructs.converter.model.Result;
import com.viaversion.nbt.tag.NumberArrayTag;
import com.viaversion.nbt.tag.NumberTag;
import com.viaversion.nbt.tag.*;
import com.viaversion.nbt.tag.*;
import com.viaversion.nbt.tag.*;
import com.viaversion.viaversion.libs.mcstructs.snbt.SNbt;

import javax.annotation.Nullable;
import java.util.*;

//

public class NbtConverter_v1_20_3 implements DataConverter<Tag> {

    public static final NbtConverter_v1_20_3 INSTANCE = new NbtConverter_v1_20_3();

    private java.util.function.Consumer<String> stringConsumer;
    @Override
    @Nullable
    public java.util.function.Consumer<String> currentConsumer() {
        return stringConsumer;
    }
    @Override
    public void setCurrentConsumer(java.util.function.Consumer<String> consumer) {
        this.stringConsumer = consumer;
    }
    @Override
    public DataConverter<Tag> forkIfDefault() {
        return this == INSTANCE ? new NbtConverter_v1_20_3() : this;
    }

    private final SNbt<CompoundTag> sNbt;

    public NbtConverter_v1_20_3() {
        this(SNbt.V1_14);
    }

    protected NbtConverter_v1_20_3(final SNbt<CompoundTag> sNbt) {
        this.sNbt = sNbt;
    }

    @Override
    public <N> N convertTo(DataConverter<N> to, @Nullable Tag element) {
        if (to == this) return (N) element;
        if (element == null) return to.empty();
        if (element.getTagId() == 0) {
                return to.empty();
        } else if (element instanceof ByteTag) {
                return to.createByte(((ByteTag) element).getValue());
        } else if (element instanceof ShortTag) {
                return to.createShort(((ShortTag) element).getValue());
        } else if (element instanceof IntTag) {
                return to.createInt(((IntTag) element).getValue());
        } else if (element instanceof LongTag) {
                return to.createLong(((LongTag) element).getValue());
        } else if (element instanceof FloatTag) {
                return to.createFloat(((FloatTag) element).getValue());
        } else if (element instanceof DoubleTag) {
                return to.createDouble(((DoubleTag) element).getValue());
        } else if (element instanceof ByteArrayTag) {
                return to.createByteArray(((ByteArrayTag) element).getValue());
        } else if (element instanceof StringTag) {
                return to.createString(((StringTag) element).getValue());
        } else if (element instanceof ListTag<?>) {
                return this.convertList(to, element);
        } else if (element instanceof CompoundTag) {
                return this.convertMap(to, element);
        } else if (element instanceof IntArrayTag) {
                return to.createIntArray(((IntArrayTag) element).getValue());
        } else if (element instanceof LongArrayTag) {
                return to.createLongArray(((LongArrayTag) element).getValue());
        } else {
                throw new IllegalArgumentException("Unknown Nbt type: " + element);
        }
    }

    @Override
    public Tag createBoolean(boolean value) {
        return new ByteTag(value);
    }

    @Override
    public Result<Boolean> asBoolean(Tag element) {
        return this.asNumber(element).map(number -> number.byteValue() != 0);
    }

    @Override
    public Tag createNumber(Number number) {
        return new DoubleTag(number.doubleValue());
    }

    @Override
    public Result<Number> asNumber(Tag element) {
        if (!(element instanceof NumberTag)) return Result.unexpected(element, NumberTag.class);
        return Result.success(((NumberTag) element).getValue());
    }

    @Override
    public Tag createByte(byte value) {
        return new ByteTag(value);
    }

    @Override
    public Tag createShort(short value) {
        return new ShortTag(value);
    }

    @Override
    public Tag createInt(int value) {
        return new IntTag(value);
    }

    @Override
    public Tag createLong(long value) {
        return new LongTag(value);
    }

    @Override
    public Tag createFloat(float value) {
        return new FloatTag(value);
    }

    @Override
    public Tag createDouble(double value) {
        return new DoubleTag(value);
    }

    @Override
    public Tag createString(String value) {
        return new StringTag(value);
    }

    @Override
    public Result<String> asString(Tag element) {
        if (!(element instanceof StringTag)) return Result.unexpected(element, StringTag.class);
        return Result.success(((StringTag) element).getValue());
    }

    @Override
    public Result<Tag> mergeList(@Nullable Tag list, List<Tag> values) {
        if (list == null) list = new ListTag<>();
        if ((list instanceof ByteArrayTag) && values.stream().allMatch(tag -> tag instanceof ByteTag)) {
            ByteArrayTag tag = ((ByteArrayTag) list);
            byte[] bytes = Arrays.copyOf(tag.getValue(), tag.length() + values.size());
            for (int i = 0; i < values.size(); i++) bytes[tag.length() + i] = ((NumberTag) values.get(i)).asByte();
            return Result.success(new ByteArrayTag(bytes));
        } else if ((list instanceof IntArrayTag) && values.stream().allMatch(tag -> tag instanceof IntTag)) {
            IntArrayTag tag = ((IntArrayTag) list);
            int[] ints = Arrays.copyOf(tag.getValue(), tag.length() + values.size());
            for (int i = 0; i < values.size(); i++) ints[tag.length() + i] = ((NumberTag) values.get(i)).asInt();
            return Result.success(new IntArrayTag(ints));
        } else if ((list instanceof LongArrayTag) && values.stream().allMatch(tag -> tag instanceof LongTag)) {
            LongArrayTag tag = ((LongArrayTag) list);
            long[] longs = Arrays.copyOf(tag.getValue(), tag.length() + values.size());
            for (int i = 0; i < values.size(); i++) longs[tag.length() + i] = ((NumberTag) values.get(i)).asLong();
            return Result.success(new LongArrayTag(longs));
        }

        Result<List<Tag>> listResult = this.asList(list);
        if (listResult.isError()) return listResult.mapError();
        List<Tag> elements = new ArrayList<>(listResult.get());
        elements.addAll(values);

        Tag listType = null;
        for (Tag value : elements) {
            Tag valueType = value;
            if (listType == null) listType = valueType;
            else if (!valueType.equals(listType)) listType = new MixedListTag(); //Placeholder for mixed lists
        }

        if (listType == null) {
            return Result.success(new ListTag<>());
        } else if ((listType instanceof ByteTag)) {
            byte[] bytes = new byte[elements.size()];
            for (int i = 0; i < elements.size(); i++) bytes[i] = ((ByteTag) elements.get(i)).asByte();
            return Result.success(new ByteArrayTag(bytes));
        } else if ((listType instanceof IntTag)) {
            int[] ints = new int[elements.size()];
            for (int i = 0; i < elements.size(); i++) ints[i] = ((IntTag) elements.get(i)).asInt();
            return Result.success(new IntArrayTag(ints));
        } else if ((listType instanceof LongTag)) {
            long[] longs = new long[elements.size()];
            for (int i = 0; i < elements.size(); i++) longs[i] = ((LongTag) elements.get(i)).asLong();
            return Result.success(new LongArrayTag(longs));
        } else if ((listType instanceof MixedListTag)) {
            ListTag<CompoundTag> listTag = new ListTag<>();
            for (Tag tag : elements) {
                boolean isMarker = (tag instanceof CompoundTag) && ((CompoundTag) tag).size() == 1 && ((CompoundTag) tag).contains("");
                if ((tag instanceof CompoundTag) && !isMarker) listTag.add(((CompoundTag) tag));
                else {
                    CompoundTag wrappingTag = new CompoundTag();
                    wrappingTag.put("", tag);
                    listTag.add(wrappingTag);
                }
            }
            return Result.success(listTag);
        } else {
            return Result.success(new ListTag<>(elements));
        }
    }

    @Override
    public Result<List<Tag>> asList(Tag element) {
        if ((element instanceof ListTag)) {
            ListTag<?> listTag = ((ListTag) element);
            List<Tag> list = new ArrayList<>(listTag.size());
            if (CompoundTag.class == listTag.getElementType()) {
                for (Tag tag : listTag) {
                    CompoundTag compound = ((CompoundTag) tag);
                    Tag wrapped = compound.size() == 1 ? compound.get("") : null;
                    if (wrapped != null) list.add(wrapped);
                    else list.add(tag);
                }
            } else {
                list.addAll(listTag.getValue());
            }
            return Result.success(list);
        } else if ((element instanceof NumberArrayTag)) {
            return Result.success((List) ((NumberArrayTag) element).toListTag().getValue());
        } else {
            return Result.unexpected(element, ListTag.class, NumberArrayTag.class);
        }
    }

    @Override
    public Tag createUnsafeMap(Map<Tag, Tag> values) {
        CompoundTag compound = new CompoundTag();
        for (Map.Entry<Tag, Tag> entry : values.entrySet()) {
            String key;
            if ((entry.getKey() instanceof StringTag)) key = ((StringTag) entry.getKey()).getValue();
            else key = this.sNbt.trySerialize(entry.getKey());
            compound.put(key, entry.getValue());
        }
        return compound;
    }

    @Override
    public Result<Tag> mergeMap(@Nullable Tag map, Map<Tag, Tag> values) {
        if (map == null) map = new CompoundTag();
        if (!(map instanceof CompoundTag)) return Result.unexpected(map, CompoundTag.class);
        CompoundTag compound = ((CompoundTag) map);
        for (Map.Entry<Tag, Tag> entry : values.entrySet()) {
            if ((entry.getKey() instanceof StringTag)) {
                compound.put(((StringTag) entry.getKey()).getValue(), entry.getValue());
            } else {
                return Result.error("Map key is not a string tag");
            }
        }
        return Result.success(compound);
    }

    @Override
    public Result<Map<Tag, Tag>> asMap(Tag element) {
        if (!(element instanceof CompoundTag)) return Result.unexpected(element, CompoundTag.class);
        CompoundTag compound = ((CompoundTag) element);
        Map<Tag, Tag> map = new HashMap<>();
        for (Map.Entry<String, Tag> entry : compound) map.put(this.createString(entry.getKey()), entry.getValue());
        return Result.success(map);
    }

    @Override
    public Result<Map<String, Tag>> asStringTypeMap(Tag element) {
        if (!(element instanceof CompoundTag)) return Result.unexpected(element, CompoundTag.class);
        CompoundTag compound = ((CompoundTag) element);
        Map<String, Tag> map = new HashMap<>();
        for (Map.Entry<String, Tag> entry : compound) map.put(entry.getKey(), entry.getValue());
        return Result.success(map);
    }

    @Override
    public Tag createByteArray(byte[] value) {
        return new ByteArrayTag(value);
    }

    @Override
    public Result<byte[]> asByteArray(Tag element) {
        if ((element instanceof ByteArrayTag)) {
            return Result.success(((ByteArrayTag) element).getValue());
        } else if ((element instanceof IntArrayTag)) {
            IntArrayTag intArrayTag = ((IntArrayTag) element);
            byte[] bytes = new byte[intArrayTag.length()];
            for (int i = 0; i < intArrayTag.length(); i++) bytes[i] = (byte) intArrayTag.get(i);
            return Result.success(bytes);
        } else if ((element instanceof LongArrayTag)) {
            LongArrayTag longArrayTag = ((LongArrayTag) element);
            byte[] bytes = new byte[longArrayTag.length()];
            for (int i = 0; i < longArrayTag.length(); i++) bytes[i] = (byte) longArrayTag.get(i);
            return Result.success(bytes);
        } else if ((element instanceof ListTag)) {
            List<Tag> list = this.asList(element).get();
            if (list.stream().allMatch(tag -> tag instanceof NumberTag)) {
                byte[] bytes = new byte[list.size()];
                for (int i = 0; i < list.size(); i++) bytes[i] = ((NumberTag) list.get(i)).asByte();
                return Result.success(bytes);
            } else {
                return Result.error("List tag does not contain only number tags");
            }
        } else {
            return Result.unexpected(element, ByteArrayTag.class, IntArrayTag.class, LongArrayTag.class, ListTag.class);
        }
    }

    @Override
    public Tag createIntArray(int[] value) {
        return new IntArrayTag(value);
    }

    @Override
    public Result<int[]> asIntArray(Tag element) {
        if ((element instanceof ByteArrayTag)) {
            ByteArrayTag byteArrayTag = ((ByteArrayTag) element);
            int[] ints = new int[byteArrayTag.length()];
            for (int i = 0; i < byteArrayTag.length(); i++) ints[i] = byteArrayTag.get(i);
            return Result.success(ints);
        } else if ((element instanceof IntArrayTag)) {
            return Result.success(((IntArrayTag) element).getValue());
        } else if ((element instanceof LongArrayTag)) {
            LongArrayTag longArrayTag = ((LongArrayTag) element);
            int[] ints = new int[longArrayTag.length()];
            for (int i = 0; i < longArrayTag.length(); i++) ints[i] = (int) longArrayTag.get(i);
            return Result.success(ints);
        } else if ((element instanceof ListTag)) {
            List<Tag> list = this.asList(element).get();
            if (list.stream().allMatch(tag -> tag instanceof NumberTag)) {
                int[] ints = new int[list.size()];
                for (int i = 0; i < list.size(); i++) ints[i] = ((NumberTag) list.get(i)).asInt();
                return Result.success(ints);
            } else {
                return Result.error("List tag does not contain only number tags");
            }
        } else {
            return Result.unexpected(element, ByteArrayTag.class, IntArrayTag.class, LongArrayTag.class, ListTag.class);
        }
    }

    @Override
    public Tag createLongArray(long[] value) {
        return new LongArrayTag(value);
    }

    @Override
    public Result<long[]> asLongArray(Tag element) {
        if ((element instanceof ByteArrayTag)) {
            ByteArrayTag byteArrayTag = ((ByteArrayTag) element);
            long[] longs = new long[byteArrayTag.length()];
            for (int i = 0; i < byteArrayTag.length(); i++) longs[i] = byteArrayTag.get(i);
            return Result.success(longs);
        } else if ((element instanceof IntArrayTag)) {
            IntArrayTag intArrayTag = ((IntArrayTag) element);
            long[] longs = new long[intArrayTag.length()];
            for (int i = 0; i < intArrayTag.length(); i++) longs[i] = intArrayTag.get(i);
            return Result.success(longs);
        } else if ((element instanceof LongArrayTag)) {
            return Result.success(((LongArrayTag) element).getValue());
        } else if ((element instanceof ListTag)) {
            List<Tag> list = this.asList(element).get();
            if (list.stream().allMatch(tag -> tag instanceof NumberTag)) {
                long[] longs = new long[list.size()];
                for (int i = 0; i < list.size(); i++) longs[i] = ((NumberTag) list.get(i)).asLong();
                return Result.success(longs);
            } else {
                return Result.error("List tag does not contain only number tags");
            }
        } else {
            return Result.unexpected(element, ByteArrayTag.class, IntArrayTag.class, LongArrayTag.class, ListTag.class);
        }
    }

}
