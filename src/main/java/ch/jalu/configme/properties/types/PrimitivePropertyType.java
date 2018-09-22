package ch.jalu.configme.properties.types;

import java.util.function.Function;

/**
 * Standard implementations of property types for basic types.
 *
 * @param <T> the type the property type produces
 */
public class PrimitivePropertyType<T> implements PropertyType<T> {

    public static final PropertyType<Long> LONG = fromNumber(Number::longValue);

    public static final PropertyType<Integer> INTEGER = fromNumber(Number::intValue);

    public static final PropertyType<Double> DOUBLE = fromNumber(Number::doubleValue);

    public static final PropertyType<Float> FLOAT = fromNumber(Number::floatValue);

    public static final PropertyType<Short> SHORT = fromNumber(Number::shortValue);

    public static final PropertyType<Byte> BYTE = fromNumber(Number::byteValue);

    public static final PropertyType<Boolean> BOOLEAN = new PrimitivePropertyType<>(
        object -> object instanceof Boolean ? (Boolean) object : null);

    public static final PropertyType<String> STRING = new PrimitivePropertyType<>(
        object -> object == null ? null : object.toString());

    public static final PropertyType<String> LOWERCASE_STRING = new PrimitivePropertyType<>(
        object -> object == null ? null : object.toString().toLowerCase());

    private final Function<Object, T> convertFunction;
    private final Function<T, Object> exportValueFunction;

    /**
     * Constructor.
     *
     * @param convertFunction function to convert to the given type
     */
    public PrimitivePropertyType(Function<Object, T> convertFunction) {
        this(convertFunction, t -> t);
    }

    /**
     * Constructor.
     *
     * @param convertFunction function to convert to the given type
     * @param exportValueFunction function to convert a value to its export value
     */
    public PrimitivePropertyType(Function<Object, T> convertFunction, Function<T, Object> exportValueFunction) {
        this.convertFunction = convertFunction;
        this.exportValueFunction = exportValueFunction;
    }

    @Override
    public T convert(Object object) {
        return convertFunction.apply(object);
    }

    @Override
    public Object toExportValue(T value) {
        return exportValueFunction.apply(value);
    }

    /* Helper to create property types which convert from a Number object. */
    private static <T> PrimitivePropertyType<T> fromNumber(Function<Number, T> function) {
        return new PrimitivePropertyType<>(object -> object instanceof Number ? function.apply((Number) object) : null);
    }
}
