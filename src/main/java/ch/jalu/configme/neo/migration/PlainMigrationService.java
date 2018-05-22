package ch.jalu.configme.neo.migration;

import ch.jalu.configme.neo.properties.Property;
import ch.jalu.configme.neo.resource.PropertyResource;

import java.util.List;

/**
 * Simple migration service that can be extended.
 */
public class PlainMigrationService implements MigrationService {

    @Override
    public boolean checkAndMigrate(PropertyResource resource, List<Property<?>> properties) {
        return performMigrations(resource, properties) || !containsAllSettings(resource, properties);
    }

    /**
     * Override this method for custom migrations. This method is executed before checking
     * if all settings are present. For instance, you could implement deleting obsolete properties
     * and rename properties in this method.
     * <p>
     * Note that you do <i>not</i> have to save the resource. The settings manager automatically
     * does this if the migration service returns {@code true} from {@link #checkAndMigrate}.
     *
     * @param resource the property resource to check
     * @param properties list of known properties
     * @return true if a migration was performed and the config should be saved,
     * false if no migration was performed
     */
    protected boolean performMigrations(PropertyResource resource, List<Property<?>> properties) {
        return false;
    }

    private static boolean containsAllSettings(PropertyResource resource, List<Property<?>> properties) {
        for (Property<?> property : properties) {
            if (!property.isPresent(resource)) {
                return false;
            }
        }
        return true;
    }

}
