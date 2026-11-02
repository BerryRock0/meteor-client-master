package com.viaversion.viaversion.libs.mcstructs.converter.codec;

import com.viaversion.viaversion.libs.mcstructs.converter.DataConverter;
import com.viaversion.viaversion.libs.mcstructs.converter.model.Result;

@FunctionalInterface
public interface DataSerializer<T> {

    <S> Result<S> serialize(final DataConverter<S> converter, final T element);

}
