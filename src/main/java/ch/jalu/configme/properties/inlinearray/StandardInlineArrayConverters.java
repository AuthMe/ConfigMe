package ch.jalu.configme.properties.inlinearray;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.regex.Pattern;

/**
 * Standard implementations of {@link InlineArrayConverter}.
 *
 * @param <T> the array type the converter produces
 */
public class StandardInlineArrayConverters<T> implements InlineArrayConverter<T> {

    public static final InlineArrayConverter<Long> LONG =
        new StandardInlineArrayConverters<>(",", Long[]::new, Long::parseLong);

    public static final InlineArrayConverter<Integer> INTEGER =
        new StandardInlineArrayConverters<>(",", Integer[]::new, Integer::parseInt);

    public static final InlineArrayConverter<Float> FLOAT =
        new StandardInlineArrayConverters<>(",", Float[]::new, Float::parseFloat);

    public static final InlineArrayConverter<Double> DOUBLE =
        new StandardInlineArrayConverters<>(",", Double[]::new, Double::parseDouble);

    public static final InlineArrayConverter<Short> SHORT =
        new StandardInlineArrayConverters<>(",", Short[]::new, Short::parseShort);

    public static final InlineArrayConverter<Byte> BYTE =
        new StandardInlineArrayConverters<>(",", Byte[]::new, Byte::parseByte);

    public static final InlineArrayConverter<Boolean> BOOLEAN =
        new StandardInlineArrayConverters<>(",", Boolean[]::new,
            s -> s.isEmpty() ? null : Boolean.parseBoolean(s));

    public static final InlineArrayConverter<String> STRING =
        new StandardInlineArrayConverters<>("\n", String[]::new, s -> s, false);


    private final String separator;
    private final IntFunction<T[]> arrayProducer;
    private final Function<String, T> convertFunction;
    private final boolean useTrimAndSpaces;

    public StandardInlineArrayConverters(String separator, IntFunction<T[]> arrayProducer,
                                         Function<String, T> convertFunction) {
        this(separator, arrayProducer, convertFunction, true);
    }

    /**
     * Constructor.
     *
     * @param separator sequence by which the elements of the array are separated in the String representation
     * @param arrayProducer array constructor (desired array size as argument)
     * @param convertFunction convert function from String to type for one element
     * @param useTrimAndSpaces true if a space should be put after the separator in the export and if the split elements
     *                         from the input String should be trimmed before being passed to the convert function
     */
    public StandardInlineArrayConverters(String separator, IntFunction<T[]> arrayProducer,
                                         Function<String, T> convertFunction, boolean useTrimAndSpaces) {
        this.separator = separator;
        this.arrayProducer = arrayProducer;
        this.convertFunction = convertFunction;
        this.useTrimAndSpaces = useTrimAndSpaces;
    }

    @Override
    public T[] fromString(String input) {
        String[] inputArray = input.split(Pattern.quote(separator));

        return Arrays.stream(inputArray)
            .map(this::convert)
            .filter(Objects::nonNull)
            .toArray(arrayProducer);
    }

    @Override
    public String toExportValue(T[] value) {
        String delimiter = useTrimAndSpaces ? separator + " " : separator;
        StringJoiner joiner = new StringJoiner(delimiter);
        for (T entry : value) {
            joiner.add(String.valueOf(entry));
        }
        return joiner.toString();
    }

    /**
     * Converts from the given String to the converter's type, if possible.
     *
     * @param input String to convert from
     * @return converted value, or null if not possible
     */
    @Nullable
    protected T convert(String input) {
        try {
            String argument = useTrimAndSpaces ? input.trim() : input;
            return convertFunction.apply(argument);
        } catch (Exception e) {
            return null;
        }
    }
}
