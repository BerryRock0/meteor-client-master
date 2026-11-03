package com.viaversion.viaversion.libs.mcstructs.converter.codec;

import com.viaversion.viaversion.libs.mcstructs.converter.DataConverter;
import com.viaversion.viaversion.libs.mcstructs.converter.model.Result;

@FunctionalInterface
public interface DataDeserializer<T> {

    <S> Result<T> deserialize(final DataConverter<S> converter, final S data);

}
