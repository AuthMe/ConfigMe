package ch.jalu.configme.neo.migration;

import ch.jalu.configme.neo.configurationdata.ConfigurationData;
import ch.jalu.configme.neo.registry.ValuesRegistry;
import ch.jalu.configme.neo.resource.PropertyReader;

/**
 * The migration service is called when the settings manager is instantiated. It allows to
 * validate the settings and perform migrations (e.g. delete old settings, rename settings).
 * If a migration is performed, the config file will be saved again.
 */
public interface MigrationService {

    // TODO: Explain the different arguments
    boolean checkAndMigrate(PropertyReader reader, ValuesRegistry valuesRegistry, ConfigurationData configurationData);
    // TODO: Replace with enum return value? Makes it annoying to concatenate multiple checks... But is more speaking.

}
