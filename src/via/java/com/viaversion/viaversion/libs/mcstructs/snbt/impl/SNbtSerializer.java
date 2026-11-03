package com.viaversion.viaversion.libs.mcstructs.snbt.impl;

import com.viaversion.nbt.tag.*;
import com.viaversion.viaversion.libs.mcstructs.snbt.exceptions.SNbtSerializeException;

/**
 * The base SNbt serializer interface.
 */
public interface SNbtSerializer {

    String serialize(final Tag tag) throws SNbtSerializeException;

}
