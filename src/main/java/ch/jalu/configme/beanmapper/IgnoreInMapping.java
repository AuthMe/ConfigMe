package ch.jalu.configme.beanmapper;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to tell ConfigMe to ignore the field during bean mapping. Fields of bean types with this annotation
 * are not picked up by ConfigMe.
 * <p>
 * Fields can also be ignored by declaring them as {@code transient}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface IgnoreInMapping {
}
