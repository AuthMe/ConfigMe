package ch.jalu.configme;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Comment for properties which are also included in the configuration file upon saving. This annotation can be used on
 * {@link ch.jalu.configme.properties.Property Property} fields, as well as on the fields of
 * bean classes that are used for {@link ch.jalu.configme.properties.BeanProperty bean properties}.
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
     * Defines if the comment should be repeated, or if it should only be included in the output the first time.
     * This method is relevant only for bean properties: if a bean class used in a collection has comments,
     * the comments will either be included on the first entry if {@code false}, or on each entry if {@code true}.
     *
     * @return whether the comment should be repeated for each occurrence of the same field
     */
    boolean repeat() default false;

}
