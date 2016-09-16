package com.github.authme.configme.beanmapper;

import java.util.HashMap;
import java.util.Map;

/**
 * Default transformer implementations.
 */
public final class Transformers {

    private static final Transformer[] DEFAULT_TRANSFORMERS = {
        new ReturnerForMatchingType(), new NumberProducer(), new StringProducer(), new EnumProducer()
    };

    private Transformers() {
    }

    public static Transformer[] getDefaultTransformers() {
        return DEFAULT_TRANSFORMERS;
    }

    private static final class ReturnerForMatchingType implements Transformer {
        @Override
        public Object transform(Class<?> type, Object value) {
            if (type.isInstance(value)) {
                return value;
            }
            return null;
        }
    }

    private static final class NumberProducer implements Transformer {

        private static final Map<Class<?>, Class<?>> primitiveTypes = buildPrimitiveNumberMap();

        @Override
        public Number transform(Class<?> type, Object value) {
            value = asReferenceType(value);
            if (!(value instanceof Number)) {
                return null;
            }
            Number number = (Number) value;
            type = asReferenceClass(type);

            if (type.isInstance(value)) {
                return number;
            } else if (Integer.class == type) {
                return number.intValue();
            } else if (Double.class == type) {
                return number.doubleValue();
            } else if (Float.class == type) {
                return number.floatValue();
            } else if (Byte.class == type) {
                return number.byteValue();
            } else if (Long.class == type) {
                return number.longValue();
            } else if (Short.class == type) {
                return number.shortValue();
            }
            return null;
        }

        private Object asReferenceType(Object value) {
            if (value != null) {
                Class<?> referenceType = primitiveTypes.get(value.getClass());
                if (referenceType != null) {
                    return referenceType.cast(value);
                }
            }
            return value;
        }

        private Class<?> asReferenceClass(Class<?> clazz) {
            Class<?> referenceClass = primitiveTypes.get(clazz);
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
            return map;
        }
    }

    private static final class StringProducer extends TypedTransformer<Object, String> {
        StringProducer() {
            super(Object.class, String.class);
        }

        @Override
        protected String safeTransform(Class<? extends String> clazz, Object o) {
             return String.valueOf(o);
        }
    }

    private static final class EnumProducer extends TypedTransformer<String, Enum<?>> {
        EnumProducer() {
            super(String.class, (Class) Enum.class);
        }

        @Override
        protected Enum<?> safeTransform(Class<? extends Enum<?>> type, String value) {
            for (Enum e : type.getEnumConstants()) {
                if (e.name().equalsIgnoreCase(value)) {
                    return e;
                }
            }
            return null;
        }
    }
}
