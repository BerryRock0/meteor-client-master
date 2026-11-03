package com.viaversion.viaversion.libs.mcstructs.converter.mapcodec;

import com.viaversion.viaversion.libs.mcstructs.converter.DataConverter;
import com.viaversion.viaversion.libs.mcstructs.converter.model.Result;

import java.util.Map;

@FunctionalInterface
public interface MapSerializer<T> {

    <S> Result<Map<S, S>> serialize(final DataConverter<S> converter, final Map<S, S> map, final T element);

}
