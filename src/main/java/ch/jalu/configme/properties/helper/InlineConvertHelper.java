package ch.jalu.configme.properties.helper;

public interface InlineConvertHelper<T> {

    T[] fromString(String in);

    Object toExportValue(T[] value);

    static InlineConvertHelper<String> stringHelper() {
        return PrimitiveConvertHelper.DEFAULT_STRING;
    }

    static InlineConvertHelper<Long> longHelper() {
        return PrimitiveConvertHelper.DEFAULT_LONG;
    }

    static InlineConvertHelper<Integer> integerHelper() {
        return PrimitiveConvertHelper.DEFAULT_INTEGER;
    }

    static InlineConvertHelper<Double> doubleHelper() {
        return PrimitiveConvertHelper.DEFAULT_DOUBLE;
    }

    static InlineConvertHelper<Float> floatHelper() {
        return PrimitiveConvertHelper.DEFAULT_FLOAT;
    }

    static InlineConvertHelper<Short> shortHelper() {
        return PrimitiveConvertHelper.DEFAULT_SHORT;
    }

    static InlineConvertHelper<Byte> byteHelper() {
        return PrimitiveConvertHelper.DEFAULT_BYTE;
    }

    static InlineConvertHelper<Boolean> booleanHelper() {
        return PrimitiveConvertHelper.DEFAULT_BOOLEAN;
    }

}
