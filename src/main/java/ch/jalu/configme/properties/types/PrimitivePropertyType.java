package ch.jalu.configme.properties.types;

import java.util.function.Function;

/**
 * Standard implementations of property types for basic types.
 *
 * @param <T> the type the property type produces
 */
public class PrimitivePropertyType<T> implements PropertyType<T> {

    public static final PropertyType<Long> LONG = fromNumber(Long.class, Number::longValue);

    public static final PropertyType<Integer> INTEGER = fromNumber(Integer.class, Number::intValue);

    public static final PropertyType<Double> DOUBLE = fromNumber(Double.class, Number::doubleValue);

    public static final PropertyType<Float> FLOAT = fromNumber(Float.class, Number::floatValue);

    public static final PropertyType<Short> SHORT = fromNumber(Short.class, Number::shortValue);

    public static final PropertyType<Byte> BYTE = fromNumber(Byte.class, Number::byteValue);

    public static final PropertyType<Boolean> BOOLEAN = new PrimitivePropertyType<>(
        Boolean.class,
        object -> object instanceof Boolean ? (Boolean) object : null);

    public static final PropertyType<String> STRING = new PrimitivePropertyType<>(
        String.class,
        object -> object == null ? null : object.toString());

    public static final PropertyType<String> LOWERCASE_STRING = new PrimitivePropertyType<>(
        String.class,
        object -> object == null ? null : object.toString().toLowerCase());

    private final Class<T> type;
    private final Function<Object, T> convertFunction;
    private final Function<T, Object> exportValueFunction;

    /**
     * Constructor.
     *
     * @param type the type of the values handled by this property type
     * @param convertFunction function to convert to the given type
     */
    public PrimitivePropertyType(Class<T> type, Function<Object, T> convertFunction) {
        this(type, convertFunction, t -> t);
    }

    /**
     * Constructor.
     *
     * @param type the type of the values handled by this property type
     * @param convertFunction function to convert to the given type
     * @param exportValueFunction function to convert a value to its export value
     */
    public PrimitivePropertyType(Class<T> type, Function<Object, T> convertFunction,
                                 Function<T, Object> exportValueFunction) {
        this.type = type;
        this.convertFunction = convertFunction;
        this.exportValueFunction = exportValueFunction;
    }

    @Override
    public T convert(Object object) {
        return convertFunction.apply(object);
    }

    @Override
    public Class<T> getType() {
        return type;
    }

    @Override
    public Object toExportValue(T value) {
        return exportValueFunction.apply(value);
    }

    /* Helper to create property types which convert from a Number object. */
    private static <T> PrimitivePropertyType<T> fromNumber(Class<T> type, Function<Number, T> function) {
        return new PrimitivePropertyType<>(type,
            object -> object instanceof Number ? function.apply((Number) object) : null);
    }
}
