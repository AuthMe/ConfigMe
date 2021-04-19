package ch.jalu.configme.beanmapper.leafvaluehandler;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Contains default leaf value handlers implementations and provides the leaf value handler which is used by default.
 * The handler implementations contained in this class only handle one type each. This allows to easily
 * override a specific behavior (by extending that small class only), allows for reuse (if you only want to use
 * specific elements) and can be combined using {@link CombiningLeafValueHandler}.
 *
 * @see #getDefaultLeafValueHandler()
 */
public final class StandardLeafValueHandlers {

    private static LeafValueHandler defaultHandler;

    private StandardLeafValueHandlers() {
    }

    /**
     * Returns the default leaf value handler used in ConfigMe.
     *
     * @return default leaf value handler
     */
    public static LeafValueHandler getDefaultLeafValueHandler() {
        if (defaultHandler == null) {
            defaultHandler = new CombiningLeafValueHandler(new StringHandler(), new EnumHandler(),
                new BooleanHandler(), new NumberHandler(), new BigNumberHandler(), new ObjectHandler());
        }
        return defaultHandler;
    }

    /** String handler. */
    public static class StringHandler extends AbstractLeafValueHandler {

        @Override
        public Object convert(Class<?> clazz, Object value) {
            if (clazz == String.class
                && (value instanceof String || value instanceof Number || value instanceof Boolean)) {
                return value.toString();
            }
            return null;
        }

        @Override
        public Object toExportValue(Object value) {
            return (value instanceof String) ? value : null;
        }
    }

    /** Enum handler. */
    public static class EnumHandler extends AbstractLeafValueHandler {

        @Override
        public Object convert(Class<?> clazz, Object value) {
            if (value instanceof String && Enum.class.isAssignableFrom(clazz)) {
                String givenText = (String) value;
                for (Enum e : (Enum[]) clazz.getEnumConstants()) {
                    if (e.name().equalsIgnoreCase(givenText)) {
                        return e;
                    }
                }
            }
            return null;
        }

        @Override
        public Object toExportValue(Object value) {
            if (value instanceof Enum<?>) {
                return ((Enum<?>) value).name();
            }
            return null;
        }
    }

    /** Boolean handler. */
    public static class BooleanHandler extends AbstractLeafValueHandler {

        @Override
        public Object convert(Class<?> clazz, Object value) {
            if ((clazz == boolean.class || clazz == Boolean.class) && value instanceof Boolean) {
                return value;
            }
            return null;
        }

        @Override
        public Object toExportValue(Object value) {
            return (value instanceof Boolean) ? value : null;
        }
    }

    /** Object handler. */
    public static class ObjectHandler extends AbstractLeafValueHandler {

        @Override
        public Object convert(Class<?> clazz, Object value) {
            if (clazz == Object.class) {
                return value;
            }
            return null;
        }

        @Override
        public Object toExportValue(Object value) {
            return null;
        }
    }

    /** Number handler for types without arbitrary precision. */
    public static class NumberHandler extends AbstractLeafValueHandler {

        private static final Map<Class<?>, Class<?>> PRIMITIVE_NUMBERS_MAP = buildPrimitiveNumberMap();

        @Override
        public Object convert(Class<?> clazz, Object value) {
            if (!(value instanceof Number)) {
                return null;
            }
            Number number = (Number) value;
            clazz = asReferenceClass(clazz);

            if (clazz.isInstance(value)) {
                return number;
            } else if (Integer.class == clazz) {
                return number.intValue();
            } else if (Double.class == clazz) {
                return number.doubleValue();
            } else if (Float.class == clazz) {
                return number.floatValue();
            } else if (Byte.class == clazz) {
                return number.byteValue();
            } else if (Long.class == clazz) {
                return number.longValue();
            } else if (Short.class == clazz) {
                return number.shortValue();
            }
            return null;
        }

        @Override
        public Object toExportValue(Object value) {
            if (value instanceof Number) {
                // TODO #182: Turn check around so no values are ever exported that are not supported by the handler.
                return (value instanceof BigInteger || value instanceof BigDecimal) ? null : value;
            }
            return null;
        }

        protected Class<?> asReferenceClass(Class<?> clazz) {
            Class<?> referenceClass = PRIMITIVE_NUMBERS_MAP.get(clazz);
            return referenceClass == null ? clazz : referenceClass;
        }

        private static Map<Class<?>, Class<?>> buildPrimitiveNumberMap() {
            Map<Class<?>, Class<?>> map = new HashMap<>();
            map.put(byte.class, Byte.class);
            map.put(short.class, Short.class);
            map.put(int.class, Integer.class);
            map.put(long.class, Long.class);
            map.put(float.class, Float.class);
            map.put(double.class, Double.class);
            return Collections.unmodifiableMap(map);
        }
    }

    /**
     * Number handler for 'Big' types that have arbitrary precision (BigInteger, BigDecimal)
     * and should be represented as strings in the config.
     */
    public static class BigNumberHandler extends AbstractLeafValueHandler {

        /** Value after which scientific notation (like "1E+30") might be used when exporting BigDecimal values. */
        private static final BigDecimal BIG_DECIMAL_SCIENTIFIC_THRESHOLD = new BigDecimal("1E10");

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
            if (value instanceof BigInteger) {
                return value.toString();
            } else if (value instanceof BigDecimal) {
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
}
