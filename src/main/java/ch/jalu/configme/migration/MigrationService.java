package ch.jalu.configme.migration;

import ch.jalu.configme.configurationdata.ConfigurationData;
import ch.jalu.configme.resource.PropertyReader;

/**
 * The migration service is called when the settings manager is instantiated. It allows to
 * validate the settings and perform migrations (e.g. delete old settings, rename settings).
 * If a migration is performed, the config file will be saved again.
 */
public interface MigrationService {

    /** Constant for the return value of {@link #checkAndMigrate}, indicating that a migration has been performed. */
    boolean MIGRATION_REQUIRED = true;

    /** Constant for the return value of {@link #checkAndMigrate}, indicating that no migration was needed. */
    boolean NO_MIGRATION_NEEDED = false;

    /**
     * Performs the migration, returning whether a migration has been performed or not.
     *
     * @param reader reader to access the values in the configuration file
     * @param configurationData configuration data, which knows all properties and manages their associated values
     * @return true if a migration has been performed, false otherwise. Indicates whether the configuration data should
     *         be saved to the configuration file or not
     */
    boolean checkAndMigrate(PropertyReader reader, ConfigurationData configurationData);

}
