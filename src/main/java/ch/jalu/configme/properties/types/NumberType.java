package ch.jalu.configme.properties.types;

import ch.jalu.configme.properties.convertresult.ConvertErrorRecorder;
import ch.jalu.typeresolver.TypeInfo;
import ch.jalu.typeresolver.numbers.StandardNumberType;
import ch.jalu.typeresolver.primitives.PrimitiveType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.math.BigInteger;

public class NumberType<N extends Number> extends PropertyAndLeafType<N> {

    public static final NumberType<Byte> BYTE = new NumberType<>(StandardNumberType.TYPE_BYTE);
    public static final NumberType<Short> SHORT = new NumberType<>(StandardNumberType.TYPE_SHORT);
    public static final NumberType<Integer> INTEGER = new NumberType<>(StandardNumberType.TYPE_INTEGER);
    public static final NumberType<Long> LONG = new NumberType<>(StandardNumberType.TYPE_LONG);
    public static final NumberType<Float> FLOAT = new NumberType<>(StandardNumberType.TYPE_FLOAT);
    public static final NumberType<Double> DOUBLE = new NumberType<>(StandardNumberType.TYPE_DOUBLE);
    public static final NumberType<BigInteger> BIG_INTEGER = new NumberType<>(StandardNumberType.TYPE_BIG_INTEGER);
    public static final NumberType<BigDecimal> BIG_DECIMAL = new NumberType<>(StandardNumberType.TYPE_BIG_DECIMAL);

    /** Value after which scientific notation (like "1E+130") might be used when exporting BigDecimal values. */
    private static final BigDecimal BIG_DECIMAL_SCIENTIFIC_THRESHOLD = new BigDecimal("1E100");

    private final ch.jalu.typeresolver.numbers.NumberType<N> numberType;

    NumberType(@NotNull ch.jalu.typeresolver.numbers.NumberType<N> type) {
        super(type.getType());
        this.numberType = type;
    }

    @Override
    public @Nullable N convert(@Nullable Object object, @NotNull ConvertErrorRecorder errorRecorder) {
        if (object instanceof String) {
            // TODO: error recorder if number is not within bounds?
            Number value = convertToNumberIfPossible((String) object);
            return value == null ? null : numberType.convertToBounds(value);
        } else if (object instanceof Number) {
            return numberType.convertToBounds((Number) object);
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

    protected final @NotNull ch.jalu.typeresolver.numbers.NumberType<N> getTypeResolverNumberType() {
        return numberType;
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
