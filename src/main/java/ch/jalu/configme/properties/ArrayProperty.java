package ch.jalu.configme.properties;

import ch.jalu.configme.properties.types.ArrayPropertyType;
import ch.jalu.configme.properties.types.PropertyType;
import org.jetbrains.annotations.NotNull;

import java.util.function.IntFunction;

/**
 * Property whose value is an array of a given type.
 *
 * @param <T> the type of the elements in the array
 */
public class ArrayProperty<T> extends TypeBasedProperty<T[]> {

    /**
     * Constructor.
     *
     * @param path the path of the property
     * @param defaultValue the default value of the property
     * @param type the property type
     * @param arrayProducer array constructor (desired array size as argument)
     */
    public ArrayProperty(@NotNull String path, T @NotNull [] defaultValue, @NotNull PropertyType<T> type,
                         @NotNull IntFunction<T[]> arrayProducer) {
        this(path, new ArrayPropertyType<>(type, arrayProducer), defaultValue);
    }

    @SafeVarargs
    public ArrayProperty(@NotNull String path, @NotNull PropertyType<T[]> type, T @NotNull ... defaultValue) {
        super(path, defaultValue, type);
    }
}
