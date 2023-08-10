package ch.jalu.configme.migration.version;

import ch.jalu.configme.configurationdata.ConfigurationData;
import ch.jalu.configme.resource.PropertyReader;
import org.jetbrains.annotations.NotNull;

/**
 * A migration used by {@link VersionMigrationService} to migrate from one configuration version to a newer one.
 *
 * @see VersionMigrationService
 */
public interface VersionMigration {

    /**
     * @return the configuration version this migration converts from (e.g. 1)
     */
    int fromVersion();

    /**
     * @return the configuration version this migration converts to (e.g. 2)
     */
    int targetVersion();

    /**
     * Migrates the configuration.
     *
     * @param reader the property reader to read the configuration file from
     * @param configurationData configuration data to update a property's value
     */
    void migrate(@NotNull PropertyReader reader, @NotNull ConfigurationData configurationData);

}
