package ch.jalu.configme.properties;

import ch.jalu.configme.properties.types.InlineArrayPropertyType;
import org.jetbrains.annotations.NotNull;

/**
 * Array property which reads and stores its value as one String in which the elements
 * are separated by a delimiter.
 *
 * @param <T> the array element type
 */
public class InlineArrayProperty<T> extends TypeBasedProperty<T[]> {

    /**
     * Constructor.
     *
     * @param path the path of the property
     * @param inlineArrayType the inline array property type
     * @param defaultValue the default value of the property
     */
    public InlineArrayProperty(@NotNull String path,
                               @NotNull InlineArrayPropertyType<T> inlineArrayType,
                               T @NotNull [] defaultValue) {
        super(path, inlineArrayType, defaultValue);
    }
}
