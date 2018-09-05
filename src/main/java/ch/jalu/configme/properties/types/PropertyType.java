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

    static BooleanPropertyType booleanType() {
        return BooleanPropertyType.INSTANCE;
    }

    static DoublePropertyType doubleType() {
        return DoublePropertyType.INSTANCE;
    }

    static <E extends Enum<E>> EnumPropertyType<E> enumType(Class<E> type) {
        return EnumPropertyType.of(type);
    }

    static FloatPropertyType floatType() {
        return FloatPropertyType.INSTANCE;
    }

    static IntegerPropertyType integerType() {
        return IntegerPropertyType.INSTANCE;
    }

    static LowerCaseStringPropertyType lowerCaseStringType() {
        return LowerCaseStringPropertyType.INSTANCE;
    }

    static StringPropertyType stringType() {
        return StringPropertyType.INSTANCE;
    }

}
