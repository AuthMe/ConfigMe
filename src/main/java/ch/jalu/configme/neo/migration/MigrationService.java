package ch.jalu.configme.neo.migration;

import ch.jalu.configme.neo.properties.Property;
import ch.jalu.configme.neo.resource.PropertyResource;

import java.util.List;

/**
 * The migration service is called when the settings manager is instantiated. It allows to
 * validate the settings and perform migrations (e.g. delete old settings, rename settings).
 * If a migration is performed, the config file will be saved again.
 */
public interface MigrationService {

    /**
     * Checks the settings and perform any necessary migrations.
     *
     * @param resource the property resource
     * @param properties all existing properties
     * @return {@code true} if a migration has been performed, {@code false} if the settings are up-to-date
     */
    boolean checkAndMigrate(PropertyResource resource, List<Property<?>> properties);
    // TODO: Replace with enum return value?
    // TODO: Pass in ConfigurationData instead? (to include comments, should we ever want to have them modifiable later)

}
