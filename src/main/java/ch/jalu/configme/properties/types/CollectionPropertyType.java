package ch.jalu.configme.properties.types;

import ch.jalu.configme.properties.convertresult.ConvertErrorRecorder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * Property type for properties that manage a collection.
 *
 * @param <E> the type of the elements in the collection
 * @param <C> the collection type
 */
public abstract class CollectionPropertyType<E, C extends Collection<E>> implements PropertyType<C> {

    private final PropertyType<E> entryType;

    /**
     * Constructor.
     *
     * @param entryType the type for the entries in the collection
     */
    public CollectionPropertyType(@NotNull PropertyType<E> entryType) {
        this.entryType = entryType;
    }

    /**
     * Creates a new collection type for the given entry type and collector. See also {@link ListPropertyType},
     * {@link SetPropertyType} and {@link EnumSetPropertyType}.
     *
     * @param entryType the entry type
     * @param collector collector to the desired collection type
     * @param <E> the type of the elements in the collection
     * @param <C> the collection type
     * @return collection type for the given entry type and collector
     */
    public static <E, C extends Collection<E>> @NotNull CollectionPropertyType<E, C> of(
                                                                                @NotNull PropertyType<E> entryType,
                                                                                @NotNull Collector<E, ?, C> collector) {
        return new CollectionPropertyType<E, C>(entryType) {
            @Override
            protected @NotNull Collector<E, ?, C> resultCollector() {
                return collector;
            }
        };
    }

    @Override
    public @Nullable C convert(@Nullable Object object, @NotNull ConvertErrorRecorder errorRecorder) {
        if (object instanceof Collection<?>) {
            Collection<?> coll = (Collection<?>) object;
            return coll.stream()
                .map(elem -> convertOrLogError(elem, errorRecorder))
                .filter(Objects::nonNull)
                .collect(resultCollector());
        }
        return null;
    }

    /**
     * Converts the given element with the entry property type, logging an error with the error recorder if the element
     * could not be converted.
     *
     * @param element the element to convert
     * @param errorRecorder the error recorder
     * @return the converted element, or null if not possible
     */
    protected @Nullable E convertOrLogError(@Nullable Object element, @NotNull ConvertErrorRecorder errorRecorder) {
        E result = entryType.convert(element, errorRecorder);
        if (result == null) {
            errorRecorder.setHasError("Could not convert '" + element + "'");
        }
        return result;
    }

    @Override
    public @NotNull List<?> toExportValue(@NotNull C value) {
        return value.stream()
            .map(entryType::toExportValue)
            .collect(Collectors.toList());
    }

    /**
     * @return the property type used for the collection's entries
     */
    public @NotNull PropertyType<E> getEntryType() {
        return entryType;
    }

    protected abstract @NotNull Collector<E, ?, C> resultCollector();

}
