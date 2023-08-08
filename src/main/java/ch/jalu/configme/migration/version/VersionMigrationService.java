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

    protected Map<Integer, VersionMigration> validateAndGroupMigrationsByFromVersion(
                                                                                Iterable<VersionMigration> migrations) {
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

    protected void validateVersions(VersionMigration migration) {
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
