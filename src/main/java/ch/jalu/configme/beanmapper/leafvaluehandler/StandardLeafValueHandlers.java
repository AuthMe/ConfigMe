package ch.jalu.configme.beanmapper.leafvaluehandler;

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
                new BooleanHandler(), new ObjectHandler(), new NumberHandler());
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

    /** Number handler. */
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
            return (value instanceof Number) ? value : null;
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
}
