package ch.jalu.configme.properties.types;

import javax.annotation.Nullable;

public interface PropertyType<T> {

    @Nullable
    T convert(Object object);

    Class<T> getType();

    Object toExportValue(T value);


    static PropertyType<String> stringType() {
        return PrimitivePropertyType.STRING;
    }

}
