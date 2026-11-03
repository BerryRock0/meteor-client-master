package com.viaversion.viaversion.libs.mcstructs.text.serializer.v1_21_2;

import com.viaversion.viaversion.libs.mcstructs.core.Identifier;
import com.viaversion.nbt.tag.*;
import com.viaversion.viaversion.libs.mcstructs.text.serializer.verify.TextVerifier;

public interface TextVerifier_v1_21_2 extends TextVerifier {

    default boolean verifyRegistryItem(final Identifier id) {
        return true;
    }

    default boolean verifyRegistryEntity(final Identifier id) {
        return true;
    }

    default boolean verifyDataComponents(final CompoundTag tag) {
        return true;
    }

    default boolean verifySelector(final String selector) {
        return true;
    }

}
