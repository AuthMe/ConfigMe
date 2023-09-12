package ch.jalu.configme.properties;

import ch.jalu.configme.properties.types.PropertyType;
import ch.jalu.configme.properties.types.SetPropertyType;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Set property of configurable type. The default value is immutable. The encounter order of the default value and
 * the constructed values is preserved, unless you've provided a custom set property type.
 *
 * @param <E> the set type
 */
public class SetProperty<E> extends TypeBasedProperty<Set<E>> {

    /**
     * Constructor.
     *
     * @param path the path of the property
     * @param entryType the entry type
     * @param defaultValue the values that make up the entries of the default set
     */
    @SafeVarargs
    public SetProperty(@NotNull String path, @NotNull PropertyType<E> entryType, @NotNull E @NotNull ... defaultValue) {
        this(path, entryType, newSet(defaultValue));
    }

    /**
     * Constructor.
     *
     * @param path the path of the property
     * @param entryType the entry type
     * @param defaultValue the default value of the property
     */
    public SetProperty(@NotNull String path, @NotNull PropertyType<E> entryType, @NotNull Set<E> defaultValue) {
        super(path, Collections.unmodifiableSet(defaultValue), new SetPropertyType<>(entryType));
    }

    /**
     * Constructor.
     *
     * @param path the path of the property
     * @param type the type of the set
     * @param defaultValue the default value of the property
     */
    public SetProperty(@NotNull String path, @NotNull SetPropertyType<E> type, @NotNull Set<E> defaultValue) {
        super(path, Collections.unmodifiableSet(defaultValue), type);
    }

    private static <E> @NotNull Set<E> newSet(E @NotNull [] array) {
        return Arrays.stream(array).collect(Collectors.toCollection(LinkedHashSet::new));
    }
}
