package ch.jalu.configme.migration;

import ch.jalu.configme.configurationdata.ConfigurationData;
import ch.jalu.configme.properties.Property;
import ch.jalu.configme.resource.PropertyReader;

import java.util.List;

/**
 * Simple migration service that can be extended.
 */
public class PlainMigrationService implements MigrationService {

    @Override
    public boolean checkAndMigrate(PropertyReader reader, ConfigurationData configurationData) {
        if (performMigrations(reader, configurationData) == MIGRATION_REQUIRED
            || checkAreAllSettingsPresent(reader, configurationData.getProperties()) == MIGRATION_REQUIRED) {
            return MIGRATION_REQUIRED;
        }
        return NO_MIGRATION_NEEDED;
    }

    /**
     * Performs custom migrations. This method exists for extension.
     *
     * @param reader the reader with which the configuration file can be read
     * @param configurationData the configuration data
     * @return true if a migration has been performed, false otherwise (see constants on {@link MigrationService})
     */
    protected boolean performMigrations(PropertyReader reader, ConfigurationData configurationData) {
        return NO_MIGRATION_NEEDED;
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
    protected static <T> boolean moveProperty(Property<T> oldProperty, Property<T> newProperty,
                                              PropertyReader reader, ConfigurationData configurationData) {
        if (reader.contains(oldProperty.getPath())) {
            if (!reader.contains(newProperty.getPath())) {
                T value = oldProperty.determineValue(reader);
                configurationData.setValue(newProperty, value);
            }
            return true;
        }
        return false;
    }

    private static boolean checkAreAllSettingsPresent(PropertyReader reader, List<Property<?>> properties) {
        for (Property<?> property : properties) {
            if (!property.isPresent(reader)) {
                return MIGRATION_REQUIRED;
            }
        }
        return NO_MIGRATION_NEEDED;
    }

}
