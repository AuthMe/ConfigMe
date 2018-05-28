package ch.jalu.configme.neo.propertytype;

import ch.jalu.configme.neo.resource.PropertyReader;

public abstract class NonNullPropertyType<T> implements PropertyType<T> {

    @Override
    public boolean isPresent(PropertyReader reader, String path) {
        return getFromReader(reader, path) != null;
    }

    @Override
    public boolean isValidValue(T value) {
        return value != null;
    }

    @Override
    public Object toExportValue(T value) {
        return value;
    }
}
