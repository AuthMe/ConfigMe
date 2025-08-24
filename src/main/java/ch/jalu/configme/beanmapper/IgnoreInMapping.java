package ch.jalu.configme.beanmapper;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to tell ConfigMe to ignore the field during bean mapping. Fields with this annotation
 * are not processed by ConfigMe while creating or saving a bean property.
 * <p>
 * Fields declared as {@code transient} are also ignored by ConfigMe.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface IgnoreInMapping {
}
