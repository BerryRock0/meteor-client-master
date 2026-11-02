package org.jetbrains.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Vendored stub of the JetBrains {@code @Nullable} annotation.
 *
 * <p>ViaNBT uses this annotation only as compile-time nullness documentation.
 * It has no runtime behaviour, so this stub avoids depending on
 * {@code org.jetbrains:annotations}.</p>
 */
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.TYPE_USE, ElementType.TYPE_PARAMETER, ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER, ElementType.LOCAL_VARIABLE})
public @interface Nullable {
}
