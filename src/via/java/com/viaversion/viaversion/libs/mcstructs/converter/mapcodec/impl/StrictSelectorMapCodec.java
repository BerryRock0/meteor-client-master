package com.viaversion.viaversion.libs.mcstructs.converter.mapcodec.impl;

import com.viaversion.viaversion.libs.mcstructs.converter.DataConverter;
import com.viaversion.viaversion.libs.mcstructs.converter.mapcodec.MapCodec;
import com.viaversion.viaversion.libs.mcstructs.converter.model.Result;

import java.util.Map;

public class StrictSelectorMapCodec<T> implements MapCodec<T> {

    private final String fieldName;
    private final MapCodec<T> primary;
    private final MapCodec<T> secondary;

    public StrictSelectorMapCodec(final String fieldName, final MapCodec<T> primary, final MapCodec<T> secondary) {
        this.fieldName = fieldName;
        this.primary = primary;
        this.secondary = secondary;
    }

    @Override
    public <S> Result<Map<S, S>> serialize(DataConverter<S> converter, Map<S, S> map, T element) {
        return this.secondary.serialize(converter, map, element);
    }

    @Override
    public <S> Result<T> deserialize(DataConverter<S> converter, Map<S, S> map) {
        if (map.containsKey(converter.createString(this.fieldName))) {
            return this.primary.deserialize(converter, map);
        } else {
            return this.secondary.deserialize(converter, map);
        }
    }

}
