package ch.jalu.configme.neo.migration;

import ch.jalu.configme.neo.properties.Property;
import ch.jalu.configme.neo.resource.PropertyReader;

import java.util.List;

/**
 * The migration service is called when the settings manager is instantiated. It allows to
 * validate the settings and perform migrations (e.g. delete old settings, rename settings).
 * If a migration is performed, the config file will be saved again.
 */
public interface MigrationService {

    boolean checkAndMigrate(PropertyReader reader, List<Property<?>> properties);
    // TODO: Replace with enum return value? Makes it annoying to concatenate multiple checks... But is more speaking.
    // TODO: Pass in ConfigurationData instead? (to include comments, should we ever want to have them modifiable later)

}
