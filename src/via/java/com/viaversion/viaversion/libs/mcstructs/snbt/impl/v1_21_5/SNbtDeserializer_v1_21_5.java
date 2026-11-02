package com.viaversion.viaversion.libs.mcstructs.snbt.impl.v1_21_5;

import com.viaversion.nbt.tag.*;
import com.viaversion.nbt.tag.ListTag;

import com.viaversion.viaversion.libs.mcstructs.snbt.exceptions.SNbtDeserializeException;
import com.viaversion.viaversion.libs.mcstructs.snbt.impl.v1_12.StringReader_v1_12;
import com.viaversion.viaversion.libs.mcstructs.snbt.impl.v1_14.SNbtDeserializer_v1_14;

import java.util.ArrayList;
import java.util.List;

public class SNbtDeserializer_v1_21_5 extends SNbtDeserializer_v1_14 {

    @Override
    protected ListTag<Tag> readList(StringReader_v1_12 reader) throws SNbtDeserializeException {
        reader.jumpTo('[');
        reader.skipWhitespaces();
        if (!reader.canRead()) throw this.makeException(reader, "Expected value");
        List<Tag> list = new ArrayList<>();
        while (reader.peek() != ']') {
            list.add(this.readValue(reader));
            if (!this.hasNextValue(reader)) break;
            if (!reader.canRead()) throw this.makeException(reader, "Expected value");
        }
        reader.jumpTo(']');
        return null /*Not required by ViaVersion at the moment*/;
    }

}
