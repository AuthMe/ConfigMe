package ch.jalu.configme.properties;

import ch.jalu.configme.resource.PropertyReader;

public interface Property<T> {

    String getPath();

    T determineValue(PropertyReader propertyReader);

    T getDefaultValue();

    boolean isPresent(PropertyReader propertyReader);

    boolean isValidValue(Object value);

    Object toExportValue(T value);

}
