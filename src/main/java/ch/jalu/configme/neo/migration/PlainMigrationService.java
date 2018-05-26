package ch.jalu.configme.neo.migration;

import ch.jalu.configme.neo.configurationdata.ConfigurationData;
import ch.jalu.configme.neo.properties.Property;
import ch.jalu.configme.neo.registry.ValuesRegistry;
import ch.jalu.configme.neo.resource.PropertyReader;

import java.util.List;

/**
 * Simple migration service that can be extended.
 */
public class PlainMigrationService implements MigrationService {

    @Override
    public boolean checkAndMigrate(PropertyReader reader,
                                   ValuesRegistry registry,
                                   ConfigurationData configurationData) {
        return performMigrations(reader, registry, configurationData)
            || !containsAllSettings(reader, configurationData.getAllProperties());
    }

    /**
     * Override this method for custom migrations. This method is executed before checking
     * if all settings are present. For instance, you could implement deleting obsolete properties
     * and rename properties in this method.
     * <p>
     * Note that you do <i>not</i> have to save the resource. The settings manager automatically
     * does this if the migration service returns {@code true} from {@link #checkAndMigrate}.
     *
     * @param reader the property reader to check
     * @param registry the values registry
     * @param configurationData configuration data
     * @return true if a migration was performed and the config should be saved,
     *         false if no migration was performed
     */
    protected boolean performMigrations(PropertyReader reader, ValuesRegistry registry,
                                        ConfigurationData configurationData) {
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
