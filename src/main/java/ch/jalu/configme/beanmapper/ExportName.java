package ch.jalu.configme.beanmapper;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation placed on a field, getter or setter method to indicate that it
 * should be loaded and written to a property resource with a different name.
 * <p>
 * In ConfigMe 2.0, the annotation will only be supported on fields.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD})
@Documented
public @interface ExportName {

    /**
     * @return the name to use when interacting with property resources
     */
    @NotNull String value();

}
