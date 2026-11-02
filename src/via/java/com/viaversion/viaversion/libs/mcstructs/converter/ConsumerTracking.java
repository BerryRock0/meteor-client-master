package com.viaversion.viaversion.libs.mcstructs.converter;

import java.util.function.Consumer;
import javax.annotation.Nullable;

public interface ConsumerTracking {

    @Nullable
    default Consumer<String> currentConsumer() {
        return null;
    }

    default void setCurrentConsumer(Consumer<String> consumer) {
    }

    default ConsumerTracking forkIfDefault() {
        return this;
    }
}
