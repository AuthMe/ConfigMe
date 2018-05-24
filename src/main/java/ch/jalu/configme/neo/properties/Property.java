package ch.jalu.configme.neo.properties;

import ch.jalu.configme.neo.resource.PropertyReader;

import javax.annotation.Nullable;

public interface Property<T> {

    String getPath();

    // TODO: @Nullable ?
    // TODO: Better name like determineValue? Something to make it clear that we won't be calling it all the time.
    T getValue(PropertyReader reader);

    boolean isPresent(PropertyReader reader);

    @Nullable
    Object toExportRepresentation(T value); // TODO: Provide a way to signal a skip? Or would that be null?

}
