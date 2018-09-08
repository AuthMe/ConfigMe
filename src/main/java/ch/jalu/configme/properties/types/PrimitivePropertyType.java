package ch.jalu.configme.properties.types;

import javax.annotation.Nullable;
import java.util.function.Function;

public class PrimitivePropertyType<T> implements PropertyType<T> {

    private final Class<T> type;

    private final Function<Object, T> convertFunction;

    public static final PropertyType<Long> LONG = number(Long.class, Number::longValue);

    public static final PropertyType<Integer> INTEGER = number(Integer.class, Number::intValue);

    public static final PropertyType<Double> DOUBLE = number(Double.class, Number::doubleValue);

    public static final PropertyType<Float> FLOAT = number(Float.class, Number::floatValue);

    public static final PropertyType<Short> SHORT = number(Short.class, Number::shortValue);

    public static final PropertyType<Byte> BYTE = number(Byte.class, Number::byteValue);

    public static final PropertyType<Boolean> BOOLEAN = new PrimitivePropertyType<>(
        Boolean.class,
        object -> object instanceof Boolean ? (Boolean) object : null
    );

    public static final PropertyType<String> STRING = new PrimitivePropertyType<>(
        String.class,
        object -> object == null ? null : object.toString()
    );

    public static final PropertyType<String> LOWERCASE_STRING = new PrimitivePropertyType<>(
        String.class,
        object -> object == null ? null : object.toString().toLowerCase()
    );

    public PrimitivePropertyType(Class<T> type, Function<Object, T> convertFunction) {
        this.type = type;
        this.convertFunction = convertFunction;
    }

    @Nullable
    @Override
    public T convert(Object object) {
        return this.convertFunction.apply(object);
    }

    @Override
    public Class<T> getType() {
        return this.type;
    }

    @Override
    public Object toExportValue(T value) {
        return value;
    }

    private static <T extends Number> PrimitivePropertyType<T> number(Class<T> type, Function<Number, T> function) {
        return new PrimitivePropertyType<>(type, (object) -> object instanceof Number ? function.apply((Number) object) : null);
    }

}
