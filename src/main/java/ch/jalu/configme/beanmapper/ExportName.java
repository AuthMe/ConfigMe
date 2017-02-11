package ch.jalu.configme.beanmapper;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation placed on a getter or setter method to indicate that it
 * should be loaded and written to a property resource with a different name.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface ExportName {

    /**
     * @return the name to use when interacting with property resources
     */
    String value();

}
