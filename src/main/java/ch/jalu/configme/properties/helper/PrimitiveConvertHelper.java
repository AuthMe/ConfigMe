package ch.jalu.configme.properties.helper;

import javax.annotation.Nullable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class PrimitiveConvertHelper<T> implements InlineConvertHelper<T> {

    private final String separator;

    private final Class<T> type;

    private final Function<String, T> convertFunction;

    public static final InlineConvertHelper<Long> DEFAULT_LONG = new PrimitiveConvertHelper<>(", ", Long.class, Long::parseLong);

    public static final InlineConvertHelper<Integer> DEFAULT_INTEGER = new PrimitiveConvertHelper<>(", ", Integer.class, Integer::parseInt);

    public static final InlineConvertHelper<Float> DEFAULT_FLOAT = new PrimitiveConvertHelper<>(", ", Float.class, Float::parseFloat);

    public static final InlineConvertHelper<Double> DEFAULT_DOUBLE = new PrimitiveConvertHelper<>(", ", Double.class, Double::parseDouble);

    public static final InlineConvertHelper<Short> DEFAULT_SHORT = new PrimitiveConvertHelper<>(", ", Short.class, Short::parseShort);

    public static final InlineConvertHelper<Byte> DEFAULT_BYTE = new PrimitiveConvertHelper<>(", ", Byte.class, Byte::parseByte);

    public static final InlineConvertHelper<Boolean> DEFAULT_BOOLEAN = new PrimitiveConvertHelper<>(", ", Boolean.class, Boolean::parseBoolean);

    public static final InlineConvertHelper<String> DEFAULT_STRING = new PrimitiveConvertHelper<String>("\\\\n", String.class, s -> s) {

        // We override it, because it using different separators
        @Override
        public Object toExportValue(String[] value) {
            StringBuilder sb = new StringBuilder();

            for (int i = 0; i < value.length; i++) {
                if (i != 0) {
                    sb = sb.append("\n");
                }

                sb = sb.append(value[i]);
            }

            return sb.toString();
        }

    };

    public PrimitiveConvertHelper(String separator, Class<T> type, Function<String, T> convertFunction) {
        this.separator = separator;
        this.type = type;
        this.convertFunction = convertFunction;
    }

    @Override
    public T[] fromString(String input) {
        String[] inputArray = input.split(this.separator);
        List<T> list = new ArrayList<>();

        for (String string : inputArray) {
            T value = this.convert(string);

            if (value != null) {
                list.add(value);
            }
        }

        return list.toArray(
            (T[]) Array.newInstance(this.type, 0)
        );
    }

    @Override
    public Object toExportValue(T[] value) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < value.length; i++) {
            if (i != 0) {
                sb = sb.append(this.separator);
            }

            sb = sb.append(value[i]);
        }

        return sb.toString();
    }

    @Nullable
    private T convert(String input) {
        try {
            return this.convertFunction.apply(input);
        } catch (Exception e) {
            return null;
        }
    }

}
