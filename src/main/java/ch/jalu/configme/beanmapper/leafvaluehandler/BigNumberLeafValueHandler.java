package ch.jalu.configme.beanmapper.leafvaluehandler;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Number handler for 'Big' types that have arbitrary precision (BigInteger, BigDecimal)
 * and should be represented as strings in the config. See also {@link NumberLeafValueHandler}.
 */
public class BigNumberLeafValueHandler extends AbstractLeafValueHandler {

    /** Value after which scientific notation (like "1E+130") might be used when exporting BigDecimal values. */
    private static final BigDecimal BIG_DECIMAL_SCIENTIFIC_THRESHOLD = new BigDecimal("1E100");

    @Override
    protected Object convert(Class<?> clazz, Object value) {
        if (clazz != BigInteger.class && clazz != BigDecimal.class) {
            return null;
        }
        if (value instanceof String) {
            return fromString(clazz, (String) value);
        } else if (value instanceof Number) {
            return fromNumber(clazz, (Number) value);
        }
        return null;
    }

    @Override
    public Object toExportValue(Object value) {
        final Class<?> valueType = value == null ? null : value.getClass();
        if (valueType == BigInteger.class) {
            return value.toString();
        } else if (valueType == BigDecimal.class) {
            BigDecimal bigDecimal = (BigDecimal) value;
            return bigDecimal.abs().compareTo(BIG_DECIMAL_SCIENTIFIC_THRESHOLD) >= 0
                ? bigDecimal.toString()
                : bigDecimal.toPlainString();
        }
        return null;
    }

    /**
     * Creates a BigInteger or BigDecimal value from the given string value, if possible.
     *
     * @param targetClass the target class to convert to (can only be BigInteger or BigDecimal)
     * @param value the value to convert
     * @return BigInteger or BigDecimal as defined by the target class, or null if no conversion was possible
     */
    @Nullable
    protected Object fromString(Class<?> targetClass, String value) {
        try {
            return targetClass == BigInteger.class
                ? new BigInteger(value)
                : new BigDecimal(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Creates a BigInteger or BigDecimal value from the given number value, if possible.
     *
     * @param targetClass the target class to convert to (can only be BigInteger or BigDecimal)
     * @param value the value to convert
     * @return BigInteger or BigDecimal as defined by the target class
     */
    protected Object fromNumber(Class<?> targetClass, Number value) {
        if (targetClass.isInstance(value)) {
            return value;
        }

        if (targetClass == BigInteger.class) {
            // Don't handle value = BigDecimal separately as property readers should only use basic types anyway
            if (value instanceof Double || value instanceof Float) {
                return BigDecimal.valueOf(value.doubleValue()).toBigInteger();
            }
            return BigInteger.valueOf(value.longValue());
        }

        // targetClass is BigDecimal if we reach this part. Check for Long first as we might lose precision if we
        // use doubleValue (seems like integer would be fine, but let's do it anyway too).
        // Smaller types like short are fine as all values can be precisely represented as a double.
        if (value instanceof Integer || value instanceof Long) {
            return BigDecimal.valueOf(value.longValue());
        }
        return BigDecimal.valueOf(value.doubleValue()).stripTrailingZeros();
    }
}
