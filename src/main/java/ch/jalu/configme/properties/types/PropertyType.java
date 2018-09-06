package ch.jalu.configme.properties.types;

import ch.jalu.configme.beanmapper.Mapper;

import javax.annotation.Nullable;

public interface PropertyType<T> {

    @Nullable
    T convert(Object object);

    Class<T> getType();

    Object toExportValue(T value);

    static <B> BeanPropertyType<B> beanType(Class<B> type) {
        return BeanPropertyType.of(type);
    }

    static <B> BeanPropertyType<B> beanType(Class<B> type, Mapper mapper) {
        return BeanPropertyType.of(type, mapper);
    }

    static <E extends Enum<E>> EnumPropertyType<E> enumType(Class<E> type) {
        return EnumPropertyType.of(type);
    }

    static PropertyType<Boolean> booleanType() {
        return PrimitivePropertyType.BOOLEAN;
    }

    static PropertyType<Double> doubleType() {
        return PrimitivePropertyType.DOUBLE;
    }

    static PropertyType<Float> floatType() {
        return PrimitivePropertyType.FLOAT;
    }

    static PropertyType<Long> longType() {
        return PrimitivePropertyType.LONG;
    }

    static PropertyType<Integer> integerType() {
        return PrimitivePropertyType.INTEGER;
    }

    static PropertyType<Short> shortType() {
        return PrimitivePropertyType.SHORT;
    }

    static PropertyType<Byte> byteType() {
        return PrimitivePropertyType.BYTE;
    }

    static PropertyType<String> lowerCaseStringType() {
        return PrimitivePropertyType.LOWERCASE_STRING;
    }

    static PropertyType<String> stringType() {
        return PrimitivePropertyType.STRING;
    }

}
