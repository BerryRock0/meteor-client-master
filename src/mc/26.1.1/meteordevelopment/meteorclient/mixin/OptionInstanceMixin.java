/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.mixin;

import meteordevelopment.meteorclient.mixininterface.IOptionInstance;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Objects;

@Mixin(OptionInstance.class)
public abstract class OptionInstanceMixin<T> implements IOptionInstance {

    @Shadow
    private T value;

    @Shadow
    @Final
    private java.util.function.Consumer<? super T> onValueUpdate;

    @Override
    public void meteor$set(Object value) {
        @SuppressWarnings("unchecked")
        T cast = (T) value;

        if (!Minecraft.getInstance().isRunning()) {
            this.value = cast;
        } else {
            if (!Objects.equals(this.value, cast)) {
                this.value = cast;
                this.onValueUpdate.accept(cast);
            }
        }
    }
}
