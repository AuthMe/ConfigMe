package ch.jalu.configme.properties.types;

import ch.jalu.configme.internal.ConversionUtils;
import ch.jalu.configme.properties.convertresult.ConvertErrorRecorder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Array property type that stores its values as a string with a separator in the property resource.
 * <p>
 * This type uses another property type to delegate individual element operations to it. The inline array property type
 * never produces arrays with any {@code null} element; if a conversion is not possible by the entry type, it is
 * skipped in the result.
 * <p>
 * There is no way to escape the separator in a value. Values are exported by converting every array element to its
 * export value (with the entry type's {@link PropertyType#toExportValue}) and subsequently calling
 * {@link Object#toString} on the result before the entries are joined with the separator. As such, the conversion
 * <b>from</b> String to the specified type should support the export value's toString representation.
 *
 * @param <T> the array type
 */
public class InlineArrayPropertyType<T> implements PropertyType<T[]> {

    /** Boolean values, comma-separated. */
    public static final InlineArrayPropertyType<Boolean> BOOLEAN =
        new InlineArrayPropertyType<>(BooleanType.BOOLEAN, ",", true, Boolean[]::new);

    /** Byte values, comma-separated. */
    public static final InlineArrayPropertyType<Byte> BYTE =
        new InlineArrayPropertyType<>(NumberType.BYTE, ",", true, Byte[]::new);

    /** Short values, comma-separated. */
    public static final InlineArrayPropertyType<Short> SHORT =
        new InlineArrayPropertyType<>(NumberType.SHORT, ",", true, Short[]::new);

    /** Integer values, comma-separated. */
    public static final InlineArrayPropertyType<Integer> INTEGER =
        new InlineArrayPropertyType<>(NumberType.INTEGER, ",", true, Integer[]::new);

    /** Long values, comma-separated. */
    public static final InlineArrayPropertyType<Long> LONG =
        new InlineArrayPropertyType<>(NumberType.LONG, ",", true, Long[]::new);

    /** Float values, comma-separated. */
    public static final InlineArrayPropertyType<Float> FLOAT =
        new InlineArrayPropertyType<>(NumberType.FLOAT, ",", true, Float[]::new);

    /** Double values, comma-separated. */
    public static final InlineArrayPropertyType<Double> DOUBLE =
        new InlineArrayPropertyType<>(NumberType.DOUBLE, ",", true, Double[]::new);

    /** String values, separated by new lines ({@code \n}). */
    public static final InlineArrayPropertyType<String> STRING =
        new InlineArrayPropertyType<>(StringType.STRING, "\n", false, String[]::new);


    private final PropertyType<T> entryType;
    private final String separator;
    private final boolean useTrimAndSpaces;
    private final IntFunction<T[]> arrayProducer;

    /**
     * Constructor.
     *
     * @param entryType property type determining how the elements in the array behave
     * @param separator string sequence to separate elements
     * @param useTrimAndSpaces whether the read text should be trimmed prior to being converted with the entry type
     * @param arrayProducer function which creates an array of the given capacity
     */
    public InlineArrayPropertyType(@NotNull PropertyType<T> entryType, @NotNull String separator,
                                   boolean useTrimAndSpaces, @NotNull IntFunction<T[]> arrayProducer) {
        this.entryType = entryType;
        this.separator = separator;
        this.useTrimAndSpaces = useTrimAndSpaces;
        this.arrayProducer = arrayProducer;
    }

    @Override
    public T @Nullable [] convert(@Nullable Object object, @NotNull ConvertErrorRecorder errorRecorder) {
        if (object instanceof String) {
            String strValue = (String) object;
            Function<String, T> convertFunction = useTrimAndSpaces
                ? entry -> entryType.convert(entry.trim(), errorRecorder)
                : entry -> entryType.convert(entry, errorRecorder);

            return Arrays.stream(strValue.split(Pattern.quote(separator), -1))
                .map(entry -> ConversionUtils.convertOrLogError(entry, convertFunction, errorRecorder))
                .filter(Objects::nonNull)
                .toArray(arrayProducer);
        }
        return null;
    }

    @Override
    public @NotNull String toExportValue(T @NotNull [] value) {
        String delimiter = useTrimAndSpaces ? (separator + " ") : separator;
        return Arrays.stream(value)
            .map(entryType::toExportValue)
            .filter(Objects::nonNull)
            .map(Object::toString)
            .collect(Collectors.joining(delimiter));
    }

    /**
     * @return function to create an array with the given capacity
     */
    public @NotNull IntFunction<T[]> getArrayProducer() {
        return arrayProducer;
    }
}
