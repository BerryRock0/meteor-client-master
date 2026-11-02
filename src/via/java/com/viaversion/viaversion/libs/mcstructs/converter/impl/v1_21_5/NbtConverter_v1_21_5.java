package com.viaversion.viaversion.libs.mcstructs.converter.impl.v1_21_5;

import com.viaversion.viaversion.libs.mcstructs.converter.impl.v1_20_3.NbtConverter_v1_20_3;
import com.viaversion.viaversion.libs.mcstructs.converter.model.Result;
import com.viaversion.nbt.tag.*;
import com.viaversion.nbt.tag.*;
import com.viaversion.nbt.tag.*;
import com.viaversion.viaversion.libs.mcstructs.snbt.SNbt;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

//

public class NbtConverter_v1_21_5 extends NbtConverter_v1_20_3 {

    public static final NbtConverter_v1_21_5 INSTANCE = new NbtConverter_v1_21_5();

    @Override
    public com.viaversion.viaversion.libs.mcstructs.converter.DataConverter<Tag> forkIfDefault() {
        return this == INSTANCE ? new NbtConverter_v1_21_5() : this;
    }

    public NbtConverter_v1_21_5() {
        this(SNbt.V1_21_5);
    }

    protected NbtConverter_v1_21_5(final SNbt<CompoundTag> sNbt) {
        super(sNbt);
    }

    @Override
    public Result<Tag> mergeList(@Nullable Tag list, List<Tag> values) {
        ListType listType;
        if (list == null) {
            listType = ListType.LIST;
        } else if (list instanceof ByteArrayTag) {
            if (((ByteArrayTag) list).length() == 0) listType = ListType.LIST;
            else listType = ListType.BYTE;
        } else if (list instanceof IntArrayTag) {
            if (((IntArrayTag) list).length() == 0) listType = ListType.LIST;
            else listType = ListType.INT;
        } else if (list instanceof LongArrayTag) {
            if (((LongArrayTag) list).length() == 0) listType = ListType.LIST;
            else listType = ListType.LONG;
        } else if (list instanceof ListTag) {
            ListTag<?> listTag = ((ListTag) list);
            if (CompoundTag.class == listTag.getElementType()) listType = ListType.MIXED_LIST;
            else listType = ListType.LIST;
        } else {
            return Result.error("Invalid list tag: " + list);
        }

        Result<List<Tag>> listResult = list == null ? Result.success(new ArrayList<>()) : this.asList(list);
        if (listResult.isError()) return listResult.mapError();
        List<Tag> tags = listResult.get();
        tags.addAll(values);
        if (tags.isEmpty()) return Result.success(new ListTag<>());

        if (listType.equals(ListType.BYTE)) {
            if (tags.stream().allMatch(tag -> tag instanceof ByteTag)) {
                byte[] bytes = new byte[tags.size()];
                for (int i = 0; i < tags.size(); i++) bytes[i] = ((ByteTag) tags.get(i)).getValue();
                return Result.success(new ByteArrayTag(bytes));
            }
        } else if (listType.equals(ListType.INT)) {
            if (tags.stream().allMatch(tag -> tag instanceof IntTag)) {
                int[] ints = new int[tags.size()];
                for (int i = 0; i < tags.size(); i++) ints[i] = ((IntTag) tags.get(i)).getValue();
                return Result.success(new IntArrayTag(ints));
            }
        } else if (listType.equals(ListType.LONG)) {
            if (tags.stream().allMatch(tag -> tag instanceof LongTag)) {
                long[] longs = new long[tags.size()];
                for (int i = 0; i < tags.size(); i++) longs[i] = ((LongTag) tags.get(i)).getValue();
                return Result.success(new LongArrayTag(longs));
            }
        } else if (listType.equals(ListType.LIST)) {
            Tag type = null;
            for (Tag tag : tags) {
                if (type == null) {
                    type = tag;
                } else if (!type.equals(tag)) {
                    type = null;
                    break;
                }
            }
            if (type != null) return Result.success(new ListTag<>(tags));
        }

        ListTag<Tag> out = new ListTag<>();
        for (Tag tag : tags) {
            boolean isMarker = (tag instanceof CompoundTag) && ((CompoundTag) tag).size() == 1 && ((CompoundTag) tag).contains("");
            if ((tag instanceof CompoundTag) && !isMarker) {
                out.add(tag);
            } else {
                CompoundTag compoundTag = new CompoundTag();
                compoundTag.put("", tag);
                out.add(compoundTag);
            }
        }
        return Result.success(out);
    }


    private enum ListType {
        BYTE, INT, LONG, LIST, MIXED_LIST
    }

}
