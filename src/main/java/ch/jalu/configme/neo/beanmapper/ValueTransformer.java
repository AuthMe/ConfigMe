package ch.jalu.configme.neo.beanmapper;

import java.util.HashMap;
import java.util.Map;

public class ValueTransformer {

    // TODO: Cleanup needed :( + interface + test

    private final Map<Class<?>, Class<?>> primitiveTypes = buildPrimitiveNumberMap();

    public Object value(Class<?> clazz, Object value) {
        Object result = transform(clazz, value);
        if (result != null) {
            return result;
        }

        if ((clazz == boolean.class || clazz == Boolean.class) && value instanceof Boolean) {
            // Primitive number types are handled in NumberProducer
            return value;
        }

        if (clazz == String.class && value instanceof String) {
            return value;
        } else if (value instanceof String && Enum.class.isAssignableFrom(clazz)) {
            return safeTransform((Class) clazz, (String) value);
        }
        return null;
    }

    protected Enum<?> safeTransform(Class<? extends Enum> type, String value) {
        for (Enum e : type.getEnumConstants()) {
            if (e.name().equalsIgnoreCase(value)) {
                return e;
            }
        }
        return null;
    }

    public Number transform(Class<?> type, Object value) {
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
