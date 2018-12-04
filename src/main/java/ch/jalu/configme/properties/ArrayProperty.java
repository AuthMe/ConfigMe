package ch.jalu.configme.properties;

import ch.jalu.configme.properties.types.PropertyType;
import ch.jalu.configme.resource.PropertyReader;

import java.util.Collection;
import java.util.Objects;
import java.util.function.IntFunction;

public class ArrayProperty<T> extends BaseProperty<T[]> {

    private final PropertyType<T> type;
    private final IntFunction<T[]> arrayProducer;

    /**
     * Constructor.
     *
     * @param path         the path of the property
     * @param defaultValue the default value of the property
     * @param type         the property type
     * @param arrayProducer array constructor (desired array size as argument)
     */
    public ArrayProperty(String path, T[] defaultValue, PropertyType<T> type, IntFunction<T[]> arrayProducer) {
        super(path, defaultValue);
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(arrayProducer, "arrayProducer");
        this.type = type;
        this.arrayProducer = arrayProducer;
    }

    @Override
    protected T[] getFromReader(PropertyReader reader) {
        Object object = reader.getObject(this.getPath());
        if (object instanceof Collection<?>) {
            Collection<?> collection = (Collection<?>) object;
            return collection.stream()
                .map(type::convert)
                .filter(Objects::nonNull)
                .toArray(arrayProducer);
        }
        return null;
    }

    @Override
    public Object toExportValue(T[] value) {
        Object[] array = new Object[value.length];

        for (int i = 0; i < array.length; i++) {
            array[i] = this.type.toExportValue(value[i]);
        }

        return array;
    }

}
