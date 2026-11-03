package javax.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Vendored stub of the JSR-305 {@code @Nullable} annotation.
 *
 * <p>ViaNBT and the vendored MCStructs sources reference JSR-305 annotations
 * only as compile-time nullness documentation. They carry no runtime behaviour,
 * so this stub keeps the sources compiling without the JSR-305 dependency.</p>
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER, ElementType.LOCAL_VARIABLE, ElementType.TYPE_USE})
public @interface Nullable {
}
