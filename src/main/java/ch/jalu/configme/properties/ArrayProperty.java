package ch.jalu.configme.properties;

import ch.jalu.configme.properties.convertresult.ConvertErrorRecorder;
import ch.jalu.configme.properties.types.PropertyType;
import ch.jalu.configme.resource.PropertyReader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Objects;
import java.util.function.IntFunction;

/**
 * Property whose value is an array of a given type.
 *
 * @param <T> the type of the elements in the array
 */
public class ArrayProperty<T> extends BaseProperty<T[]> {

    private final PropertyType<T> type;
    private final IntFunction<T[]> arrayProducer;

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
        super(path, defaultValue);
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(arrayProducer, "arrayProducer");
        this.type = type;
        this.arrayProducer = arrayProducer;
    }

    @Override
    protected T @Nullable [] getFromReader(@NotNull PropertyReader reader,
                                           @NotNull ConvertErrorRecorder errorRecorder) {
        Object object = reader.getObject(this.getPath());
        if (object instanceof Collection<?>) {
            Collection<?> collection = (Collection<?>) object;
            return collection.stream()
                .map(elem -> type.convert(elem, errorRecorder))
                .filter(Objects::nonNull)
                .toArray(arrayProducer);
        }
        return null;
    }

    @Override
    public @NotNull Object toExportValue(T @NotNull [] value) {
        Object[] array = new Object[value.length];

        for (int i = 0; i < array.length; i++) {
            array[i] = this.type.toExportValue(value[i]);
        }

        return array;
    }

}
