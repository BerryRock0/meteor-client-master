package com.viaversion.viaversion.libs.mcstructs.snbt.impl;

import com.viaversion.nbt.tag.*;
import com.viaversion.viaversion.libs.mcstructs.snbt.exceptions.SNbtDeserializeException;

/**
 * The base SNbt deserializer interface.
 *
 * @param <T> The output type of the deserializer
 */
public interface SNbtDeserializer<T extends Tag> {

    T deserialize(final String s) throws SNbtDeserializeException;

    Tag deserializeValue(final String s) throws SNbtDeserializeException;

}
