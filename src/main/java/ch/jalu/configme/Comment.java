package ch.jalu.configme;

import org.jetbrains.annotations.NotNull;

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
    String @NotNull [] value();

    /**
     * Defines if the comment should be repeated if it would be included multiple times. Relevant only for bean
     * properties: if a class used in a collection has comments, the comments will either be included on the first
     * entry if {@code false}, or on each entry if {@code true}.
     *
     * @return whether to repeat the comment if the same field is exported multiple times
     */
    boolean repeat() default false;
}
