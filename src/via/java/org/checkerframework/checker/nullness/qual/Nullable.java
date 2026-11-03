package org.checkerframework.checker.nullness.qual;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Vendored stub of the checker-qual {@code @Nullable} annotation.
 *
 * <p>ViaVersion references checker-qual annotations purely as compile-time
 * nullness documentation. They have no runtime behaviour, so this stub keeps
 * the source compiling without pulling in the checker-qual dependency.</p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE_USE, ElementType.TYPE_PARAMETER, ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER, ElementType.LOCAL_VARIABLE})
public @interface Nullable {
}
