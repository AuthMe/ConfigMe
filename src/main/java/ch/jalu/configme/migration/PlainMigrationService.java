package ch.jalu.configme.migration;

import ch.jalu.configme.configurationdata.ConfigurationData;
import ch.jalu.configme.resource.PropertyReader;
import org.jetbrains.annotations.NotNull;

/**
 * Simple migration service that can be extended.
 */
public class PlainMigrationService implements MigrationService {

    @Override
    public boolean checkAndMigrate(@NotNull PropertyReader reader, @NotNull ConfigurationData configurationData) {
        if (performMigrations(reader, configurationData) == MIGRATION_REQUIRED
            || !configurationData.areAllValuesValidInResource()) {
            return MIGRATION_REQUIRED;
        }
        return NO_MIGRATION_NEEDED;
    }

    /**
     * Override this method for custom migrations. This method is executed before checking
     * if all settings are present. For instance, you could implement deleting obsolete properties
     * and rename properties in this method.
     * <p>
     * Note that the settings manager automatically saves the resource
     * if the migration service returns {@link #MIGRATION_REQUIRED} from {@link #checkAndMigrate}.
     *
     * @param reader the reader with which the configuration file can be read
     * @param configurationData the configuration data
     * @return true if a migration has been performed, false otherwise (see constants on {@link MigrationService})
     */
    protected boolean performMigrations(@NotNull PropertyReader reader, @NotNull ConfigurationData configurationData) {
        return NO_MIGRATION_NEEDED;
    }
}
