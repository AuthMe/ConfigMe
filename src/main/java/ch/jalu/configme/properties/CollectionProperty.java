package ch.jalu.configme.properties;

import ch.jalu.configme.properties.types.CollectionPropertyType;
import ch.jalu.configme.properties.types.PropertyType;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collector;

/**
 * General {@link Collection} property. Used as common parent type for all collection properties.
 *
 * @param <E> the type of the entries in the collection
 * @param <C> the concrete collection type
 * @see ListProperty
 * @see SetProperty
 */
public class CollectionProperty<E, C extends Collection<E>> extends TypeBasedProperty<C> {

    /**
     * Constructor.
     *
     * @param path the path of the property
     * @param collectionType the property type
     * @param defaultValue the default value of the property
     */
    public CollectionProperty(@NotNull String path, @NotNull PropertyType<C> collectionType, @NotNull C defaultValue) {
        super(path, collectionType, defaultValue);
    }

    /**
     * Constructs a new collection property based on the given entry type and collector.
     *
     * @param path the path of the property
     * @param entryType the collection type
     * @param collector the collection type
     * @param defaultValue the default value
     * @param <E> the type of the entries in the collection
     * @param <C> the concrete collection type
     * @return a new collection property
     */
    public static <E, C extends Collection<E>> @NotNull Property<C> of(@NotNull String path,
                                                                       @NotNull PropertyType<E> entryType,
                                                                       @NotNull Collector<E, ?, C> collector,
                                                                       @NotNull C defaultValue) {
        CollectionPropertyType<E, C> collectionPropertyType = CollectionPropertyType.of(entryType, collector);
        return new CollectionProperty<>(path, collectionPropertyType, defaultValue);
    }

    /**
     * Constructs a new collection property based on the given entry type and collector.
     *
     * @param path the path of the property
     * @param entryType the collection type
     * @param collector the collection type
     * @param defaultValueEntries the entries that make up the default value
     * @param <E> the type of the entries in the collection
     * @param <C> the concrete collection type
     * @return a new collection property
     */
    @SafeVarargs
    public static <E, C extends Collection<E>> @NotNull Property<C> of(@NotNull String path,
                                                                       @NotNull PropertyType<E> entryType,
                                                                       @NotNull Collector<E, ?, C> collector,
                                                                       E @NotNull ... defaultValueEntries) {
        CollectionPropertyType<E, C> collectionPropertyType = CollectionPropertyType.of(entryType, collector);
        C defaultValue = Arrays.stream(defaultValueEntries).collect(collector);
        return new CollectionProperty<>(path, collectionPropertyType, defaultValue);
    }
}
