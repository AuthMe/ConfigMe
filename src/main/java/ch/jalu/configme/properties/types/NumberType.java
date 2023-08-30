package ch.jalu.configme.properties.types;

import ch.jalu.configme.properties.convertresult.ConvertErrorRecorder;
import ch.jalu.typeresolver.TypeInfo;
import ch.jalu.typeresolver.numbers.StandardNumberType;
import ch.jalu.typeresolver.numbers.ValueRangeComparison;
import ch.jalu.typeresolver.primitives.PrimitiveType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Property type and mapper leaf type for numerical values.
 *
 * @param <N> the number type
 */
public class NumberType<N extends Number> extends PropertyAndLeafType<N> {

    /** Byte number type. */
    public static final NumberType<Byte> BYTE = new NumberType<>(StandardNumberType.TYPE_BYTE);
    /** Short number type. */
    public static final NumberType<Short> SHORT = new NumberType<>(StandardNumberType.TYPE_SHORT);
    /** Integer number type. */
    public static final NumberType<Integer> INTEGER = new NumberType<>(StandardNumberType.TYPE_INTEGER);
    /** Long number type. */
    public static final NumberType<Long> LONG = new NumberType<>(StandardNumberType.TYPE_LONG);
    /** Float number type. */
    public static final NumberType<Float> FLOAT = new NumberType<>(StandardNumberType.TYPE_FLOAT);
    /** Double number type. */
    public static final NumberType<Double> DOUBLE = new NumberType<>(StandardNumberType.TYPE_DOUBLE);
    /** BigInteger number type. */
    public static final NumberType<BigInteger> BIG_INTEGER = new NumberType<>(StandardNumberType.TYPE_BIG_INTEGER);
    /** BigDecimal number type. */
    public static final NumberType<BigDecimal> BIG_DECIMAL = new NumberType<>(StandardNumberType.TYPE_BIG_DECIMAL);

    /** Value after which scientific notation (like "1E+130") might be used when exporting BigDecimal values. */
    private static final BigDecimal BIG_DECIMAL_SCIENTIFIC_THRESHOLD = new BigDecimal("1E100");

    private final ch.jalu.typeresolver.numbers.NumberType<N> numberType;

    /**
     * Constructor.
     *
     * @param type jalu typresolver NumberType implementation to base the object's behavior on
     */
    protected NumberType(@NotNull ch.jalu.typeresolver.numbers.NumberType<N> type) {
        super(type.getType());
        this.numberType = type;
    }

    @Override
    public @Nullable N convert(@Nullable Object object, @NotNull ConvertErrorRecorder errorRecorder) {
        if (object instanceof String) {
            Number value = convertToNumberIfPossible((String) object);
            return value == null ? null : convertToType(value, errorRecorder);
        } else if (object instanceof Number) {
            return convertToType((Number) object, errorRecorder);
        }
        return null;
    }

    @Override
    public @NotNull Object toExportValue(@NotNull N value) {
        if (value instanceof BigDecimal) {
            BigDecimal bigDecimal = (BigDecimal) value;
            return bigDecimal.abs().compareTo(BIG_DECIMAL_SCIENTIFIC_THRESHOLD) >= 0
                ? bigDecimal.toString()
                : bigDecimal.toPlainString();
        } else if (value instanceof BigInteger) {
            return value.toString();
        }
        return value;
    }

    @Override
    public boolean canConvertToType(@NotNull TypeInfo typeInformation) {
        Class<?> requestedClass = PrimitiveType.toReferenceType(typeInformation.toClass());
        return requestedClass != null && requestedClass.isAssignableFrom(numberType.getType());
    }

    /**
     * @return the NumberType instance (from Jalu typeresolver) this object uses to convert to its number class
     */
    protected final @NotNull ch.jalu.typeresolver.numbers.NumberType<N> getTypeResolverNumberType() {
        return numberType;
    }

    /**
     * Converts the number to this instance's type. If the number is out of this type's bounds, an error is added to
     * the error recorder and the value that is the closest to the given number is returned.
     *
     * @param number the number to convert
     * @param errorRecorder error recorder to add errors to
     * @return the converted number (or closest possible value)
     */
    protected @Nullable N convertToType(@NotNull Number number, @NotNull ConvertErrorRecorder errorRecorder) {
        ValueRangeComparison comparison = numberType.compareToValueRange(number);
        if (comparison == ValueRangeComparison.WITHIN_RANGE) {
            return numberType.convertUnsafe(number);
        }

        errorRecorder.setHasError("Value cannot be represented in type (" + comparison + ")");
        return numberType.convertToBounds(number);
    }

    /**
     * Converts the given String to a number of appropriate type, if possible. Otherwise, null is returned.
     *
     * @param value the value to potentially convert
     * @return the string converted as number, null if not possible
     */
    protected @Nullable Number convertToNumberIfPossible(@NotNull String value) {
        try {
            return new BigDecimal(value);
        } catch (NumberFormatException ignore) {
            // nothing to do
        }
        return null;
    }

    @Override
    public @NotNull String toString() {
        return "NumberTypeHandler[" + getType().getSimpleName() + "]";
    }
}
