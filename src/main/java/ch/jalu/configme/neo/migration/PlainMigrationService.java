package ch.jalu.configme.neo.migration;

import ch.jalu.configme.neo.configurationdata.ConfigurationData;
import ch.jalu.configme.neo.properties.Property;
import ch.jalu.configme.neo.resource.PropertyReader;

import java.util.List;

/**
 * Simple migration service that can be extended.
 */
public class PlainMigrationService implements MigrationService {

    @Override
    public boolean checkAndMigrate(PropertyReader reader, ConfigurationData configurationData) {
        return performMigrations(reader, configurationData)
            || !containsAllSettings(reader, configurationData.getProperties());
    }

    protected boolean performMigrations(PropertyReader reader, ConfigurationData configurationData) {
        return false;
    }

    // TODO: Offer protected static method to move old property to new property

    private static boolean containsAllSettings(PropertyReader reader, List<Property<?>> properties) {
        for (Property<?> property : properties) {
            if (!property.isPresent(reader)) {
                return false;
            }
        }
        return true;
    }

}
