package ch.jalu.configme.utils;

import ch.jalu.configme.configurationdata.ConfigurationData;
import ch.jalu.configme.properties.Property;
import ch.jalu.configme.properties.convertresult.PropertyValue;
import ch.jalu.configme.resource.PropertyReader;
import org.jetbrains.annotations.NotNull;

/**
 * Migration utils.
 */
public final class MigrationUtils {

    private MigrationUtils() {
    }

    /**
     * Utility method: moves the value of an old property to a new property. This is only done if there is no value for
     * the new property in the configuration file and if there is one for the old property. Returns true if a value is
     * present at the old property path.
     *
     * @param oldProperty the old property (create a temporary {@link Property} object with the path)
     * @param newProperty the new property to move the value to
     * @param reader the property reader to read the configuration file from
     * @param configurationData configuration data to update a property's value
     * @param <T> the type of the property
     * @return true if the old path exists in the configuration file, false otherwise
     */
    public static <T> boolean moveProperty(@NotNull Property<T> oldProperty,
                                           @NotNull Property<T> newProperty,
                                           @NotNull PropertyReader reader,
                                           @NotNull ConfigurationData configurationData) {
        if (reader.contains(oldProperty.getPath())) {
            if (!reader.contains(newProperty.getPath())) {
                PropertyValue<T> value = oldProperty.determineValue(reader);
                configurationData.setValue(newProperty, value.getValue());
            }
            return true;
        }
        return false;
    }
}
