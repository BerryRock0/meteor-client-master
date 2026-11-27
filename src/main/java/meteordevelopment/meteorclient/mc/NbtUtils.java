/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */
package meteordevelopment.meteorclient.mc;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.lang.reflect.Method;

/** Saves entities and block entities to NBT across the pre/post 1.21.6 serializer split. */
public final class NbtUtils {
    private NbtUtils() {}

    public static CompoundTag save(Object target, HolderLookup.Provider registries) {
        // 26.x / 1.21.6+: TagValueOutput-based serializers.
        try {
            Class<?> outputType = Class.forName("net.minecraft.world.level.storage.TagValueOutput");
            Object reporter = findField("net.minecraft.util.ProblemReporter", "DISCARDING");
            Method create = outputType.getMethod("createWithContext", Class.forName("net.minecraft.util.ProblemReporter"), HolderLookup.Provider.class);
            Object output = create.invoke(null, reporter, registries);
            try {
                target.getClass().getMethod("saveWithFullMetadata", outputType).invoke(target, output);
            } catch (ReflectiveOperationException ignored) {
                target.getClass().getMethod("saveWithoutId", outputType).invoke(target, output);
            }
            return (CompoundTag) outputType.getMethod("buildResult").invoke(output);
        } catch (Throwable ignored) {
        }

        // 1.21.5 and older: direct CompoundTag serializers.
        try {
            if (target instanceof BlockEntity blockEntity) {
                return blockEntity.saveWithFullMetadata(registries);
            }
            CompoundTag tag = new CompoundTag();
            target.getClass().getMethod("saveWithoutId", CompoundTag.class).invoke(target, tag);
            return tag;
        } catch (Throwable t) {
            return new CompoundTag();
        }
    }

    private static Object findField(String type, String name) throws Exception {
        return Class.forName(type).getField(name).get(null);
    }
}
