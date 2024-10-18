package ch.jalu.configme.beanmapper;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * In classes used for bean properties, annotating a field with this annotation tells ConfigMe to ignore the
 * property, i.e. when a bean is created, the annotated field will not be set, and when a bean is exported, fields
 * with this annotation will also be ignored.
 * <p>
 * Instead of this annotation, you can also declare fields as {@code transient} to have them skipped.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface IgnoreInMapping {
}
