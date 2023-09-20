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
 * the constructed values is preserved, unless the property was created with a custom set property type that does not
 * make these guarantees.
 *
 * @param <E> the type of the elements in the set
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
        super(path, new SetPropertyType<>(entryType), Collections.unmodifiableSet(defaultValue));
    }

    /**
     * Constructor. Use {@link #withSetType}.
     *
     * @param path the path of the property
     * @param type the type of the set
     * @param defaultValue the default value of the property
     */
    // Constructor arguments are usually (path, type, defaultValue), but this is not possible here because there
    // are other constructors with the same argument order.
    protected SetProperty(@NotNull PropertyType<Set<E>> type, @NotNull String path, @NotNull Set<E> defaultValue) {
        super(path, type, Collections.unmodifiableSet(defaultValue));
    }

    /**
     * Creates a new set property with the given path, type and default value.
     *
     * @param path the path of the property
     * @param setType the set type
     * @param defaultValue the values that make up the entries of the default set
     * @param <E> the type of the elements in the set
     * @return a new set property
     */
    @SafeVarargs
    public static <E> SetProperty<E> withSetType(@NotNull String path, @NotNull PropertyType<Set<E>> setType,
                                                 @NotNull E @NotNull ... defaultValue) {
        return new SetProperty<>(setType, path, newSet(defaultValue));
    }

    /**
     * Creates a new set property with the given path, type and default value.
     *
     * @param path the path of the property
     * @param setType the set type
     * @param defaultValue the default value of the property
     * @param <E> the type of the elements in the set
     * @return a new set property
     */
    public static <E> SetProperty<E> withSetType(@NotNull String path, @NotNull PropertyType<Set<E>> setType,
                                                 @NotNull Set<E> defaultValue) {
        return new SetProperty<>(setType, path, defaultValue);
    }

    private static <E> @NotNull Set<E> newSet(E @NotNull [] array) {
        return Arrays.stream(array).collect(Collectors.toCollection(LinkedHashSet::new));
    }
}
