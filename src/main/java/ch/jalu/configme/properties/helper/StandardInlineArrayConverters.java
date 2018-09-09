package ch.jalu.configme.properties.helper;

import javax.annotation.Nullable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * Standard implementations of {@link InlineArrayConverter}.
 *
 * @param <T> the array type the converter produces
 */
public class StandardInlineArrayConverters<T> implements InlineArrayConverter<T> {

    public static final InlineArrayConverter<Long> LONG =
        new StandardInlineArrayConverters<>(", ", Long.class, Long::parseLong);

    public static final InlineArrayConverter<Integer> INTEGER =
        new StandardInlineArrayConverters<>(", ", Integer.class, Integer::parseInt);

    public static final InlineArrayConverter<Float> FLOAT =
        new StandardInlineArrayConverters<>(", ", Float.class, Float::parseFloat);

    public static final InlineArrayConverter<Double> DOUBLE =
        new StandardInlineArrayConverters<>(", ", Double.class, Double::parseDouble);

    public static final InlineArrayConverter<Short> SHORT =
        new StandardInlineArrayConverters<>(", ", Short.class, Short::parseShort);

    public static final InlineArrayConverter<Byte> BYTE =
        new StandardInlineArrayConverters<>(", ", Byte.class, Byte::parseByte);

    public static final InlineArrayConverter<Boolean> BOOLEAN =
        new StandardInlineArrayConverters<>(", ", Boolean.class, Boolean::parseBoolean);

    public static final InlineArrayConverter<String> STRING =
        new StandardInlineArrayConverters<>("\n", String.class, s -> s);

    private final String separator;
    private final Class<T> type;
    private final Function<String, T> convertFunction;

    public StandardInlineArrayConverters(String separator, Class<T> type, Function<String, T> convertFunction) {
        this.separator = separator;
        this.type = type;
        this.convertFunction = convertFunction;
    }

    @Override
    public T[] fromString(String input) {
        String[] inputArray = input.split(Pattern.quote(this.separator));
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
    public String toExportValue(T[] value) {
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
