package com.viaversion.viaversion.libs.mcstructs.converter.mapcodec.impl;

import com.viaversion.viaversion.libs.mcstructs.converter.DataConverter;
import com.viaversion.viaversion.libs.mcstructs.converter.codec.Codec;
import com.viaversion.viaversion.libs.mcstructs.converter.mapcodec.MapCodec;
import com.viaversion.viaversion.libs.mcstructs.converter.model.Result;

import java.util.Map;
import java.util.function.Function;

public class RecursiveMapCodec<T> implements MapCodec<T> {

    private final MapCodec<T> codec;

    public RecursiveMapCodec(final Function<Codec<T>, MapCodec<T>> creator) {
        this.codec = creator.apply(this.asCodec());
    }

    @Override
    public <S> Result<Map<S, S>> serialize(DataConverter<S> converter, Map<S, S> map, T element) {
        return this.codec.serialize(converter, map, element);
    }

    @Override
    public <S> Result<T> deserialize(DataConverter<S> converter, Map<S, S> map) {
        return this.codec.deserialize(converter, map);
    }

}
