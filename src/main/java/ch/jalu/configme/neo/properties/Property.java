package ch.jalu.configme.neo.properties;

import ch.jalu.configme.neo.resource.PropertyResource;

import javax.annotation.Nullable;

// TODO: Interface or abstract class?
public interface Property<T> {

    String getPath();

    T getDefaultValue();

    T getValue(PropertyResource resource);

    boolean isPresent(PropertyResource resource);

    @Nullable
    Object toExportRepresentation(T value); // TODO: Provide a way to signal a skip? Or would that be null?

}
