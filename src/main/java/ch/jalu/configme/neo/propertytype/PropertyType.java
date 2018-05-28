package ch.jalu.configme.neo.propertytype;

import ch.jalu.configme.neo.resource.PropertyReader;

import javax.annotation.Nullable;

public interface PropertyType<T> {

    @Nullable
    T getFromReader(PropertyReader reader, String path);

    boolean isPresent(PropertyReader reader, String path);

    boolean isValidValue(T value);

    Object toExportValue(T value);

}
