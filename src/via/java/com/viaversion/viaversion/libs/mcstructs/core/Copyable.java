package com.viaversion.viaversion.libs.mcstructs.core;

public interface Copyable<T> {

    /**
     * Create a copy of this object.<br>
     * All children are copied too.
     *
     * @return The copy
     */
    T copy();

}
