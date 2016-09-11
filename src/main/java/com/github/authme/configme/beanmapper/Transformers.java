package com.github.authme.configme.beanmapper;

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

    private static final class NumberProducer extends TypedTransformer<Number, Number> {
        NumberProducer() {
            super(Number.class, Number.class);
        }

        @Override
        protected Number safeTransform(Class<? extends Number> type, Number value) {
            if (type.isInstance(value)) {
                return value;
            } else if (Integer.class == type) {
                return value.intValue();
            } else if (Double.class == type) {
                return value.doubleValue();
            } else if (Float.class == type) {
                return value.floatValue();
            } else if (Byte.class == type) {
                return value.byteValue();
            } else if (Long.class == type) {
                return value.longValue();
            }
            return null;
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
            try {
                return Enum.valueOf((Class) type, value);
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
    }
}
