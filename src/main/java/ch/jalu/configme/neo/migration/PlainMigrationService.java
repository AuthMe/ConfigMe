package ch.jalu.configme.neo.migration;

import ch.jalu.configme.neo.properties.Property;
import ch.jalu.configme.neo.resource.PropertyReader;

import java.util.List;

/**
 * Simple migration service that can be extended.
 */
public class PlainMigrationService implements MigrationService {

    @Override
    public boolean checkAndMigrate(PropertyReader reader, List<Property<?>> properties) {
        return performMigrations(reader, properties) || !containsAllSettings(reader, properties);
    }

    // TODO: CRITICAL - not possible to set any values anymore...
    // Changing from PropertyResource to PropertyReader has effectively broken migration possibilities since we can't
    // set any values to the property reader. So probably we need to pass in the "value registry" as well...?
    // We might have something like "checkAndMigrate(ValueRegistry, PropertyReader, ConfigurationData)" in the end.
    // Even if we don't allow to modify comments, ConfigurationData would be clearer as to where the list of properties come from.
    // What's confusing for the user about having the PropertyReader and the ValueRegistry is for him to know which one
    // to use to check some value.

    /**
     * Override this method for custom migrations. This method is executed before checking
     * if all settings are present. For instance, you could implement deleting obsolete properties
     * and rename properties in this method.
     * <p>
     * Note that you do <i>not</i> have to save the resource. The settings manager automatically
     * does this if the migration service returns {@code true} from {@link #checkAndMigrate}.
     *
     * @param reader the property reader to check
     * @param properties list of known properties
     * @return true if a migration was performed and the config should be saved,
     * false if no migration was performed
     */
    protected boolean performMigrations(PropertyReader reader, List<Property<?>> properties) {
        return false;
    }

    private static boolean containsAllSettings(PropertyReader reader, List<Property<?>> properties) {
        for (Property<?> property : properties) {
            if (!property.isPresent(reader)) {
                return false;
            }
        }
        return true;
    }

}
