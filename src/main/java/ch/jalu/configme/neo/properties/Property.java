package ch.jalu.configme.neo.properties;

import ch.jalu.configme.neo.propertytype.PropertyType;
import ch.jalu.configme.neo.resource.PropertyReader;

public interface Property<T> {

    String getPath();

    T determineValue(PropertyReader propertyReader);

    T getDefaultValue();

    boolean isPresent(PropertyReader propertyReader);

    PropertyType<T> getPropertyType();

}
