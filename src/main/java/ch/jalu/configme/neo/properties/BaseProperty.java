package ch.jalu.configme.neo.properties;

import ch.jalu.configme.neo.propertytype.PropertyType;
import ch.jalu.configme.neo.resource.PropertyReader;

import java.util.Objects;

public class BaseProperty<T> implements Property<T> {

    private final String path;
    private final T defaultValue;
    private final PropertyType<T> propertyType;

    public BaseProperty(String path, T defaultValue, PropertyType<T> propertyType) {
        Objects.requireNonNull(path, "path");
        Objects.requireNonNull(defaultValue, "defaultValue");
        Objects.requireNonNull(propertyType, "propertyType");
        this.path = path;
        this.defaultValue = defaultValue;
        this.propertyType = propertyType;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public T determineValue(PropertyReader reader) {
        T value = propertyType.getFromReader(reader, getPath());
        return value != null ? value : getDefaultValue();
    }

    @Override
    public T getDefaultValue() {
        return defaultValue;
    }

    @Override
    public boolean isPresent(PropertyReader propertyReader) {
        return propertyType.isPresent(propertyReader, getPath());
    }

    @Override
    public PropertyType<T> getPropertyType() {
        return propertyType;
    }
}
