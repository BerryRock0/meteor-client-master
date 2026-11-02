package com.viaversion.viaversion.libs.mcstructs.text.serializer.v1_20_3;

import com.viaversion.viaversion.libs.mcstructs.converter.codec.Codec;
import com.viaversion.nbt.tag.*;
import com.viaversion.viaversion.libs.mcstructs.snbt.SNbt;

import java.util.UUID;

public class ExtraCodecs_v1_20_3 {

    public static final Codec<CompoundTag> STRING_COMPOUND = Codec.STRING.mapThrowing(SNbt.V1_14::serialize, SNbt.V1_14::deserialize);
    public static final Codec<UUID> LENIENT_UUID = Codec.oneOf(Codec.INT_ARRAY_UUID, Codec.STRICT_STRING_UUID);

}
