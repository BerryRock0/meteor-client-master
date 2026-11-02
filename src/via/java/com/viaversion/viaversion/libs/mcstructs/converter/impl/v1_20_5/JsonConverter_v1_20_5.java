package com.viaversion.viaversion.libs.mcstructs.converter.impl.v1_20_5;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.viaversion.viaversion.libs.mcstructs.converter.impl.v1_20_3.JsonConverter_v1_20_3;
import com.viaversion.viaversion.libs.mcstructs.converter.model.Result;

public class JsonConverter_v1_20_5 extends JsonConverter_v1_20_3 {

    public static final JsonConverter_v1_20_5 INSTANCE = new JsonConverter_v1_20_5();

    @Override
    public com.viaversion.viaversion.libs.mcstructs.converter.DataConverter<JsonElement> forkIfDefault() {
        return this == INSTANCE ? new JsonConverter_v1_20_3() : this;
    }

    @Override
    public Result<Boolean> asBoolean(JsonElement element) {
        if (element.isJsonPrimitive()) {
            JsonPrimitive primitive = element.getAsJsonPrimitive();
            if (primitive.isBoolean()) return Result.success(primitive.getAsBoolean());
        }
        return Result.unexpected(element, "boolean");
    }

    @Override
    public Result<Number> asNumber(JsonElement element) {
        if (element.isJsonPrimitive()) {
            JsonPrimitive primitive = element.getAsJsonPrimitive();
            if (primitive.isNumber()) return Result.success(primitive.getAsNumber());
        }
        return Result.unexpected(element, "number");
    }

}
