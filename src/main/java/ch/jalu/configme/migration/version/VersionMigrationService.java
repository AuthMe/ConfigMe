package ch.jalu.configme.migration.version;

import ch.jalu.configme.configurationdata.ConfigurationData;
import ch.jalu.configme.migration.MigrationService;
import ch.jalu.configme.properties.Property;
import ch.jalu.configme.resource.PropertyReader;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Version-based {@link MigrationService} implementation that uses a {@code Property<Integer>} to track configuration
 * versions, based on which it triggers migrations. The application's current configuration value is taken from the
 * property's {@link Property#getDefaultValue() default value}, which should be incremented whenever a new migration is
 * desired.
 * <p>
 * To define a migration, create a new implementation of {@link VersionMigration} and provide it to this service's
 * constructor. Ensure that each migration's starting version is unique and valid, and that the target version is not
 * greater than the default value of the version property.
 * <p>
 * This service triggers migrations and resaves the configuration if the version read from the configuration file is not
 * equal to the version property's default value. Migrations are applied successively from the stored version to the
 * target version of each migration, ensuring proper migration order. For example, if a service has a migration from
 * version 1 to 2, and one from version 2 to 3, then both migrations are run if the version in the config file is 1.
 * On the other hand, if one migration migrates from version 1 to 3 and another one from 2 to 3, then only the former
 * would be run in the same scenario.
 * <p>
 * Regardless of which migrations were run (or if any were run at all), the version in the config file is set to the
 * version property's default value at the end of the execution. This ensures that invalid versions (like a value that
 * was manually changed) are fixed. Since only known properties are saved to the config file, storing the current
 * default value as version most appropriately reflects the structure of the configuration file.
 * <p>
 * It is recommended to create a migration for each incremental version change for simplicity (i.e. 1 to 2,
 * 2 to 3, ...). However, you can also define non-sequential migrations: a migration can migrate from 1 to 4,
 * another from 2 to 3, and one from 3 to 4 to migrate any older version to version 4.
 *
 * @author gamerover98
 */
public class VersionMigrationService implements MigrationService {

    /**
     * The version {@link Property} of the configuration.
     */
    private final Property<Integer> versionProperty;

    /**
     * All known migrations held by start version.
     */
    private final Map<Integer, VersionMigration> migrationsByStartVersion;

    /**
     * Constructor.
     *
     * @param versionProperty the property that contains the configuration version
     * @param migrations all known migrations
     */
    public VersionMigrationService(@NotNull Property<Integer> versionProperty,
                                   @NotNull Iterable<VersionMigration> migrations) {
        this.versionProperty = versionProperty;
        this.migrationsByStartVersion = validateAndGroupMigrationsByFromVersion(migrations);
    }

    /**
     * Constructor.
     *
     * @param versionProperty the property that contains the configuration version
     * @param migrations all known migrations
     */
    public VersionMigrationService(@NotNull Property<Integer> versionProperty,
                                   @NotNull VersionMigration... migrations) {
        this(versionProperty, Arrays.asList(migrations));
    }

    @Override
    public boolean checkAndMigrate(@NotNull PropertyReader reader, @NotNull ConfigurationData configurationData) {
        return performMigrations(reader, configurationData) || !configurationData.areAllValuesValidInResource();
    }

    protected final @NotNull Property<Integer> getVersionProperty() {
        return versionProperty;
    }

    protected final @NotNull Map<Integer, VersionMigration> getMigrationsByStartVersion() {
        return migrationsByStartVersion;
    }

    /**
     * Performs the migration by using the versioning system.
     * <p>
     * Note that the settings manager automatically saves the resource
     * if the migration service returns {@link #MIGRATION_REQUIRED} from {@link #checkAndMigrate}.
     *
     * @param reader the reader with which the configuration file can be read
     * @param configurationData the configuration data
     * @return true if a migration has been performed, false otherwise (see constants on {@link MigrationService})
     */
    protected boolean performMigrations(@NotNull PropertyReader reader, @NotNull ConfigurationData configurationData) {
        int readConfigVersion = versionProperty.determineValue(reader).getValue();
        int configVersion = versionProperty.getDefaultValue();

        // No action needed, versions match.
        if (readConfigVersion == configVersion) {
            return NO_MIGRATION_NEEDED;
        }

        // Migrate the configuration from version 1 to 2 to 3, and so on
        runApplicableMigrations(readConfigVersion, reader, configurationData);
        // We set the current version regardless of what migrations were run: if there was no migration for the version
        // or the migrations didn't end up at the current config version, triggering a resave still means that all
        // stored values correspond to the current structure, so it's safe to assume we're up-to-date.
        configurationData.setValue(versionProperty, configVersion);

        return MIGRATION_REQUIRED;
    }

    /**
     * Runs applicable migrations successively: if a migration is found for the read config version, it is run and its
     * {@link VersionMigration#targetVersion() target version} is noted. If a migration exists for the target version,
     * it is also run, and so forth.
     *
     * @param readConfigVersion the version that was read in the configuration file
     * @param reader the reader with which the configuration file can be read
     * @param configurationData the configuration data
     * @return the target version of the last migration that was run, or the read config version if no migration was run
     */
    protected int runApplicableMigrations(int readConfigVersion,
                                          @NotNull PropertyReader reader,
                                          @NotNull ConfigurationData configurationData) {
        int updatedVersion = readConfigVersion;
        VersionMigration migration = migrationsByStartVersion.get(readConfigVersion);
        while (migration != null) {
            migration.migrate(reader, configurationData);

            updatedVersion = migration.targetVersion();
            migration = migrationsByStartVersion.get(updatedVersion);
        }
        return updatedVersion;
    }

    /**
     * Validates the given migrations and returns them as a map of migration by its start version.
     *
     * @param migrations the migrations to validate and group
     * @return map with all migrations by the migration's start version
     */
    protected @NotNull Map<Integer, VersionMigration> validateAndGroupMigrationsByFromVersion(
                                                                       @NotNull Iterable<VersionMigration> migrations) {
        Map<Integer, VersionMigration> migrationsByStartVersion = new HashMap<>();
        for (VersionMigration migration : migrations) {
            validateVersions(migration);
            int fromVersion = migration.fromVersion();

            if (migrationsByStartVersion.put(fromVersion, migration) != null) {
                throw new IllegalArgumentException(
                    "Multiple migrations were provided for start version " + fromVersion);
            }
        }
        return migrationsByStartVersion;
    }

    /**
     * Validates the from-version and to-version of the migration.
     *
     * @param migration the migration to validate
     */
    protected void validateVersions(@NotNull VersionMigration migration) {
        if (migration.targetVersion() > versionProperty.getDefaultValue()) {
            throw new IllegalArgumentException("The migration from version " + migration.fromVersion() + " to version "
                + migration.targetVersion() + " has an invalid target version. Current configuration version is: "
                + versionProperty.getDefaultValue());
        } else if (migration.fromVersion() >= migration.targetVersion()) {
            throw new IllegalArgumentException(
                "A migration from version " + migration.fromVersion() + " to version " + migration.targetVersion()
                    + " was supplied, but it is expected that the target version be larger than the start version");
        }
    }
}
