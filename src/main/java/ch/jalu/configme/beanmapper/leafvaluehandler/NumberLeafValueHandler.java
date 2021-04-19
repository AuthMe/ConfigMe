package ch.jalu.configme.beanmapper.leafvaluehandler;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Number handler for types without arbitrary precision. See also {@link BigNumberLeafValueHandler}.
 */
public class NumberLeafValueHandler extends AbstractLeafValueHandler {

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
