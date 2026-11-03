package com.viaversion.viaversion.libs.mcstructs.text.serializer.v1_21_2;

import com.viaversion.viaversion.libs.mcstructs.converter.codec.Codec;
import com.viaversion.viaversion.libs.mcstructs.converter.impl.v1_20_3.NbtConverter_v1_20_3;
import com.viaversion.viaversion.libs.mcstructs.converter.model.Result;
import com.viaversion.nbt.tag.*;
import com.viaversion.nbt.tag.*;

import java.util.UUID;

public class ExtraCodecs_v1_21_2 {

    public static final Codec<UUID> LENIENT_UUID = Codec.oneOf(Codec.INT_ARRAY_UUID, Codec.STRICT_STRING_UUID);
    public static final Codec<CompoundTag> INLINED_COMPOUND_TAG = NbtConverter_v1_20_3.INSTANCE.toCodec().verified(tag -> {
        if (!(tag instanceof CompoundTag)) return Result.error("Expected a compound tag");
        return null;
    }).map(tag -> ((CompoundTag) tag), tag -> ((CompoundTag) tag));

}
