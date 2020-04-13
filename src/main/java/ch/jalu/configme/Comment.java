package ch.jalu.configme;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Comment for properties which are also included in the YAML file upon saving.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Comment {

    /**
     * Defines the comment to associate with a property field. You can define an empty line with no comment marker
     * ('#' in YAML) by adding a line that is just "\n", e.g. {@code @Comment("Title", "\n", "Lorem ipsum")}.
     *
     * @return the comment to associate with the property
     */
    String[] value();

}
