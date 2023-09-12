package ch.jalu.configme.properties.types;

import ch.jalu.configme.properties.convertresult.ConvertErrorRecorder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.IntFunction;
import java.util.stream.Collectors;

/**
 * Property type for arrays.
 *
 * @param <T> the type of the array's elements
 */
public class ArrayPropertyType<T> implements PropertyType<T[]> {

    private final PropertyType<T> entryType;
    private final IntFunction<T[]> arrayProducer;

    /**
     * Constructor.
     *
     * @param entryType the type of the array's elements
     * @param arrayProducer function to create an array of the right type with the provided size
     */
    public ArrayPropertyType(@NotNull PropertyType<T> entryType, @NotNull IntFunction<T[]> arrayProducer) {
        Objects.requireNonNull(entryType, "entryType");
        Objects.requireNonNull(arrayProducer, "arrayProducer");
        this.entryType = entryType;
        this.arrayProducer = arrayProducer;
    }

    @Override
    public T @Nullable [] convert(@Nullable Object object, @NotNull ConvertErrorRecorder errorRecorder) {
        if (object instanceof Collection<?>) {
            Collection<?> coll = (Collection<?>) object;
            return coll.stream()
                .map(entry -> convertOrLogError(entry, errorRecorder))
                .filter(Objects::nonNull)
                .toArray(arrayProducer);
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
    protected @Nullable T convertOrLogError(@Nullable Object element, @NotNull ConvertErrorRecorder errorRecorder) {
        T result = entryType.convert(element, errorRecorder);
        if (result == null) {
            errorRecorder.setHasError("Could not convert '" + element + "'");
        }
        return result;
    }

    @Override
    public @NotNull List<?> toExportValue(T @NotNull [] value) {
        return Arrays.stream(value)
            .map(entryType::toExportValue)
            .collect(Collectors.toList());
    }

    public @NotNull PropertyType<T> getEntryType() {
        return entryType;
    }

    public @NotNull IntFunction<T[]> getArrayProducer() {
        return arrayProducer;
    }
}
