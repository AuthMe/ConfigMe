package ch.jalu.configme.neo.properties;

import ch.jalu.configme.neo.resource.PropertyReader;

import javax.annotation.Nullable;

public interface Property<T> {

    String getPath();

    // TODO: Better name like determineValue? Something to make it clear that we won't be calling it all the time.
    T getValue(PropertyReader reader);

    T getDefaultValue();

    /**
     * Returns whether or not the given resource contains the property.
     *
     * @param reader the property reader to check with
     * @return true if the property is present, false otherwise
     */
    boolean isPresent(PropertyReader reader);

    // Null signifies skip property
    @Nullable
    Object toExportRepresentation(T value);

    boolean isValidValueForSetting(T value);

}
