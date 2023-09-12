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
     * @param defaultValue the default value of the property
     * @param inlineConverter the inline array property type
     */
    public InlineArrayProperty(@NotNull String path, T @NotNull [] defaultValue,
                               @NotNull InlineArrayPropertyType<T> inlineConverter) {
        super(path, defaultValue, inlineConverter);
    }
}
