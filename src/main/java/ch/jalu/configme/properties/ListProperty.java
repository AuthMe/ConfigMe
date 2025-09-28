package ch.jalu.configme.properties;

import ch.jalu.configme.properties.types.ListPropertyType;
import ch.jalu.configme.properties.types.PropertyType;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * List property of a configurable type. The default value is immutable.
 *
 * @param <E> the type of the elements in the list
 */
public class ListProperty<E> extends CollectionProperty<E, List<E>> {

    /**
     * Constructor.
     *
     * @param path the path of the property
     * @param entryType the entry type
     * @param defaultValue the entries in the list of the default value
     */
    @SafeVarargs
    public ListProperty(@NotNull String path, @NotNull PropertyType<E> entryType,
                        @NotNull E @NotNull ... defaultValue) {
        this(path, entryType, Arrays.asList(defaultValue));
    }

    /**
     * Constructor.
     *
     * @param path the path of the property
     * @param entryType the entry type
     * @param defaultValue the default value of the property
     */
    public ListProperty(@NotNull String path, @NotNull PropertyType<E> entryType, @NotNull List<E> defaultValue) {
        super(path, new ListPropertyType<>(entryType), Collections.unmodifiableList(defaultValue));
    }

    /**
     * Constructor. Use {@link #withListType}.
     *
     * @param listType the list type
     * @param path the path of the property
     * @param defaultValue the default value of the property
     */
    // Constructor arguments are usually (path, type, defaultValue), but this is not possible here because there
    // are other constructors with the same argument order.
    protected ListProperty(@NotNull PropertyType<List<E>> listType, @NotNull String path,
                           @NotNull List<E> defaultValue) {
        super(path, listType, Collections.unmodifiableList(defaultValue));
    }

    /**
     * Creates a new list property with the given path, type and default value.
     *
     * @param path the path of the property
     * @param listType the list type
     * @param defaultValue the entries in the list of the default value
     * @param <E> the type of the elements in the list
     * @return a new list property
     */
    @SafeVarargs
    public static <E> @NotNull ListProperty<E> withListType(@NotNull String path,
                                                            @NotNull PropertyType<List<E>> listType,
                                                            @NotNull E @NotNull ... defaultValue) {
        return new ListProperty<>(listType, path, Arrays.asList(defaultValue));
    }

    /**
     * Creates a new list property with the given path, type and default value.
     *
     * @param path the path of the property
     * @param listType the list type
     * @param defaultValue the default value of the property
     * @param <E> the type of the elements in the list
     * @return a new list property
     */
    public static <E> @NotNull ListProperty<E> withListType(@NotNull String path,
                                                            @NotNull PropertyType<List<E>> listType,
                                                            @NotNull List<E> defaultValue) {
        return new ListProperty<>(listType, path, defaultValue);
    }
}
