package ch.jalu.configme.properties.types;

import ch.jalu.configme.internal.ConversionUtils;
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
 * Property type for arrays: wraps another property type, which handles individual array elements, into an array type.
 * <p>
 * This property type never produces arrays that have any {@code null} elements. If an entry cannot be converted by the
 * entry type (the property type that converts individual array elements), it is skipped in the result.
 *
 * @param <T> the type of the array's elements
 */
public class ArrayPropertyType<T> implements PropertyType<T[]> {

    private final PropertyType<T> entryType;
    private final IntFunction<T[]> arrayProducer;

    /**
     * Constructor.
     * <p>
     * Note that many property types provided by ConfigMe have a method {@code arrayType()},
     * e.g. {@link NumberType#arrayType()}, to create an array equivalent of the base type.
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
                .map(elem -> ConversionUtils.convertOrLogError(elem, entryType, errorRecorder))
                .filter(Objects::nonNull)
                .toArray(arrayProducer);
        }
        return null;
    }

    @Override
    public @NotNull List<?> toExportValue(T @NotNull [] value) {
        return Arrays.stream(value)
            .map(entryType::toExportValue)
            .collect(Collectors.toList());
    }

    public final @NotNull PropertyType<T> getEntryType() {
        return entryType;
    }

    /**
     * @return function to create an array with the given capacity
     */
    public final @NotNull IntFunction<T[]> getArrayProducer() {
        return arrayProducer;
    }
}
