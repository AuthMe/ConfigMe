package ch.jalu.configme.migration.version;

import ch.jalu.configme.configurationdata.ConfigurationData;
import ch.jalu.configme.migration.MigrationService;
import ch.jalu.configme.properties.Property;
import ch.jalu.configme.resource.PropertyReader;
import ch.jalu.configme.SettingsHolder;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * <b>VMS - Version Migration Service</b>
 * <p>
 *   This {@link MigrationService} can be useful to easily manage the migration from
 *   an old config version to a new one without generating issues.
 * </p>
 *
 * @author gamerover98
 */
public class VersionMigrationService implements MigrationService {

    /**
     * The version {@link Property} of the configuration.
     */
    private final Property<Integer> versionProperty;

    /**
     * A map where the key is the start version and the
     * value is an implementation of the {@link VersionMigration}.
     */
    private final Map<Integer, VersionMigration> migrationMap;

    /**
     * @param versionProperty The not-null version {@link Property} from the {@link SettingsHolder}.
     * @param migrationMap A not-null collection of migrations.
     * @throws IllegalArgumentException if the versionPropertyType is empty.
     */
    public VersionMigrationService(@NotNull Property<Integer> versionProperty,
                                   @NotNull Map<Integer, VersionMigration> migrationMap) {
        this.versionProperty = versionProperty;
        // The migrationMap field will be an unmodifiable LinkedHashMap ordered by the key.
        this.migrationMap = Collections.unmodifiableMap(
            migrationMap.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .collect(
                    LinkedHashMap::new,
                    (map, entry) -> map.put(entry.getKey(), entry.getValue()),
                    LinkedHashMap::putAll));
    }

    @Override
    public boolean checkAndMigrate(@NotNull PropertyReader reader, @NotNull ConfigurationData configurationData) {
        return performMigrations(reader, configurationData) || !configurationData.areAllValuesValidInResource();
    }

    /**
     * @return The not-null version property from the {@link SettingsHolder}.
     */
    @NotNull
    public Property<Integer> getVersionProperty() {
        return this.versionProperty;
    }

    /**
     * @return The unmodifiable {@link Map} of migrations where the key
     *         is the start version and the value is an implementation
     *         of the {@link VersionMigration}.
     */
    @NotNull
    public Map<Integer, VersionMigration> getMigrationMap() {
        return this.migrationMap;
    }

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
        boolean migrationResult = NO_MIGRATION_NEEDED;

        int readConfigVersion = versionProperty.determineValue(reader).getValue();
        int configVersion = versionProperty.getDefaultValue();

        // No action needed, versions match.
        if (readConfigVersion == configVersion) {
            return migrationResult;
        }

        /*
         * If the version read from the current config file is higher than
         * the current default value of the SettingsHolder version property,
         * we need to reset the configuration to the current SettingsHolder version.
         */
        if (readConfigVersion > configVersion) {
            // Reset the config version and delegate the rest to ConfigMe.
            configurationData.setValue(versionProperty, configVersion);
            migrationResult = MIGRATION_REQUIRED;
        } else {

            // Migrate the configuration from version 1 to 2 to 3, and so on
            for (Map.Entry<Integer, VersionMigration> migrationEntry : migrationMap.entrySet()) {
                VersionMigration versionMigration = migrationEntry.getValue();

                int fromVersion = migrationEntry.getKey();
                int toVersion = versionMigration.targetVersion();

                // Start the migration.
                if (readConfigVersion == fromVersion) {
                    versionMigration.migrate(reader, configurationData);
                    configurationData.setValue(versionProperty, toVersion);
                    migrationResult = MIGRATION_REQUIRED;
                }
            }

            /*
             * If there are no migrations, reset the file similarly to when
             * the read config version is higher than the current default value (as mentioned above).
             */
            if (migrationResult == NO_MIGRATION_NEEDED) {
                // Reset the config version and delegate the rest to ConfigMe.
                configurationData.setValue(versionProperty, configVersion);
                migrationResult = MIGRATION_REQUIRED;
            }
        }

        return migrationResult;
    }
}
