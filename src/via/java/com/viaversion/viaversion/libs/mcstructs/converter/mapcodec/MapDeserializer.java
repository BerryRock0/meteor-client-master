package com.viaversion.viaversion.libs.mcstructs.converter.mapcodec;

import com.viaversion.viaversion.libs.mcstructs.converter.DataConverter;
import com.viaversion.viaversion.libs.mcstructs.converter.model.Result;

import java.util.Map;

@FunctionalInterface
public interface MapDeserializer<T> {

    <S> Result<T> deserialize(final DataConverter<S> converter, final Map<S, S> map);

}
