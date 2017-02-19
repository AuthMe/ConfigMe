package ch.jalu.configme.migration;

import ch.jalu.configme.properties.Property;
import ch.jalu.configme.resource.PropertyResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Simple migration service that can be extended.
 */
public class PlainMigrationService implements MigrationService {

    private static final Logger logger = LoggerFactory.getLogger(PlainMigrationService.class);

    @Override
    public boolean checkAndMigrate(PropertyResource resource, List<Property<?>> properties) {
        return performMigrations(resource, properties) || !containsAllSettings(resource, properties);
    }

    /**
     * Override this method for custom migrations. This method is executed before checking
     * if all settings are present. For instance, you could implement deleting obsolete properties
     * and renamed properties in this method.
     * <p>
     * Note that you do <i>not</i> have to save the resource. The settings manager automatically
     * does this if the migration service returns an according result.
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
                logger.debug("Did not find {} in resource '{}'", property, resource);
                return false;
            }
        }
        logger.trace("All properties present in resource '{}'", resource);
        return true;
    }

}
