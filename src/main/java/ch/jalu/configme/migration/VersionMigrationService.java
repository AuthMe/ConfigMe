package ch.jalu.configme.migration;

import ch.jalu.configme.configurationdata.ConfigurationData;
import ch.jalu.configme.properties.Property;
import ch.jalu.configme.resource.PropertyReader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;

import static ch.jalu.configme.properties.PropertyInitializer.*;

/**
 * <b>VMS - Version Migration Service</b>
 * <p>
 *   This {@link MigrationService} can be useful to easily manage the migration from
 *   an old config version to a new one without generating issues.
 * </p>
 *
 * @author gamerover98
 */
public abstract class VersionMigrationService implements MigrationService {

    @Override
    public boolean checkAndMigrate(@NotNull PropertyReader reader, @NotNull ConfigurationData configurationData) {
        if (performMigrations(reader, configurationData) == MIGRATION_REQUIRED
            || !configurationData.areAllValuesValidInResource()) {
            return MIGRATION_REQUIRED;
        }

        return NO_MIGRATION_NEEDED;
    }

    /**
     * @return A not-null set containing all migrations.
     */
    @NotNull
    protected abstract Collection<Migration> migrations();

    /**
     * Perform the migration by using the versioning system.
     * <p>
     * Note that the settings manager automatically saves the resource
     * if the migration service returns {@link #MIGRATION_REQUIRED} from {@link #checkAndMigrate}.
     *
     * @param reader the reader with which the configuration file can be read
     * @param configurationData the configuration data
     * @return true if a migration has been performed, false otherwise (see constants on {@link MigrationService})
     */
    protected boolean performMigrations(@NotNull PropertyReader reader, @NotNull ConfigurationData configurationData) {
        int currentVersion = getCurrentVersion(reader, configurationData);
        AtomicBoolean migrationResult = new AtomicBoolean(NO_MIGRATION_NEEDED);

        // Migrate the configuration from the version 1 to 2 to 3 to ...
        migrations()
            .forEach(migration -> {
                if (migration == null) return; // null-safe check.

                int fromVersion = migration.fromVersion();
                int toVersion = migration.toVersion();

                // start the migration.
                if (currentVersion == fromVersion) {
                    migration.migrate(reader, configurationData);
                    setCurrentVersion(configurationData, toVersion);
                    migrationResult.set(MIGRATION_REQUIRED);
                }
            });

        return migrationResult.get();
    }

    /**
     * @return the nullable config version property comment.
     */
    @Nullable
    protected String getConfigVersionPropertyComment() {
        throw new IllegalStateException("Not implemented yet");
        // return null;
    }

    /**
     * By default, this property is named "version".
     * @return The config version property name.
     */
    protected String getConfigVersionPropertyName() {
        return "version";
    }

    /**
     * By default, the start config version property value is 1.
     * @return the start config version value is 1.
     */
    protected int getStartConfigVersionValue() {
        return 1;
    }

    /**
     * Gets the current configuration version.
     * If the property doesn't exist it will be created with the {@link #getStartConfigVersionValue()}.
     *
     * @param reader the reader with which the configuration file can be read
     * @param configurationData the configuration data
     * @return the current configuration version.
     */
    protected int getCurrentVersion(@NotNull PropertyReader reader, @NotNull ConfigurationData configurationData) {
        Property<Integer> configVersionProperty = newProperty(getConfigVersionPropertyName(), getStartConfigVersionValue());

        // if the version property does not exist just add it.
        if (!reader.contains(getConfigVersionPropertyName())) {
            configurationData.setValue(configVersionProperty, configVersionProperty.getDefaultValue());
            // TODO: add the property comment by using the #getConfigVersionPropertyComment() method.
        }

        return configVersionProperty.determineValue(reader).getValue();
    }

    protected void setCurrentVersion(@NotNull ConfigurationData configurationData, int newVersion) {
        Property<Integer> configVersionProperty = newProperty(getConfigVersionPropertyName(), newVersion);
        configurationData.setValue(configVersionProperty, configVersionProperty.getDefaultValue());
    }

    public interface Migration {

        /**
         * @return The current version value (such as 1).
         */
        int fromVersion();

        /**
         * @return The next version value (such as 2).
         */
        int toVersion();

        /**
         * Migrate the configuration to the next version.
         *
         * @param reader the property reader to read the configuration file from
         * @param configurationData configuration data to update a property's value
         */
        void migrate(@NotNull PropertyReader reader, @NotNull ConfigurationData configurationData);
    }
}
