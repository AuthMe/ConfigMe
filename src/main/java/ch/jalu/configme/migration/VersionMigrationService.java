package ch.jalu.configme.migration;

import ch.jalu.configme.configurationdata.ConfigurationData;
import ch.jalu.configme.properties.Property;
import ch.jalu.configme.properties.PropertyInitializer;
import ch.jalu.configme.resource.PropertyReader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicBoolean;

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
     * By default, the start config version property value is 1.
     */
    private final int startVersion;

    /**
     * The current config version of the configuration file.
     */
    private final int currentVersion;

    /**
     * By default, this property is named "version".
     */
    private final String versionPropertyName;

    /**
     * A collection of {@link Migration}.
     */
    private final Collection<Migration> migrations;

    /**
     * By default, the starting version is 1 and the version property name is "version".
     *
     * @param currentVersion The current config version.
     * @param migrations A not-null collection of migrations.
     * @throws IllegalArgumentException if the versionPropertyType is empty.
     */
    public VersionMigrationService(int currentVersion, @NotNull Collection<Migration> migrations) {
        this(1, currentVersion, "version", migrations);
    }

    /**
     * By default, the version property name is "version".
     *
     * @param startVersion the starting version value
     * @param currentVersion The current config version.
     * @param migrations A not-null collection of migrations.
     * @throws IllegalArgumentException if the versionPropertyType is empty.
     */
    public VersionMigrationService(int startVersion,
                                   int currentVersion,
                                   @NotNull Collection<Migration> migrations) {
        this(startVersion, currentVersion, "version", migrations);
    }

    /**
     * @param startVersion the starting version value
     * @param currentVersion The current config version.
     * @param versionPropertyName The not-null & not-empty version property name.
     * @param migrations A not-null collection of migrations.
     * @throws IllegalArgumentException if the versionPropertyType is empty.
     */
    public VersionMigrationService(int startVersion,
                                   int currentVersion,
                                   @NotNull String versionPropertyName,
                                   @NotNull Collection<Migration> migrations) {

        versionPropertyName = versionPropertyName.trim();
        versionPropertyName = versionPropertyName.replace(" ", "-"); // prevent spaces

        if (versionPropertyName.isEmpty())
            throw new IllegalArgumentException("The versionPropertyName cannot be an empty string");

        this.startVersion = startVersion;
        this.currentVersion = currentVersion;
        this.versionPropertyName = versionPropertyName;
        this.migrations = Collections.unmodifiableCollection(migrations);
    }

    @Override
    public boolean checkAndMigrate(@NotNull PropertyReader reader, @NotNull ConfigurationData configurationData) {
        int currentConfigVersion = readConfigurationVersion(reader, configurationData);

        if (currentConfigVersion < getStartVersion()) handleTooLowerVersion(currentConfigVersion);
        if (currentConfigVersion > getCurrentVersion()) handleTooHigherVersion(currentConfigVersion);

        return performMigrations(reader, configurationData) || !configurationData.areAllValuesValidInResource();
    }

    /**
     * @return the start config version. By default, it is 1.
     */
    public int getStartVersion() {
        return this.startVersion;
    }

    /**
     * @return the current config version.
     */
    public int getCurrentVersion() {
        return this.currentVersion;
    }

    /**
     * @return The config property name. By default, it is "version".
     */
    @NotNull
    public String getVersionPropertyName() {
        return this.versionPropertyName;
    }

    /**
     * @return The unmodifiable {@link Collection<Migration>} of migrations.
     */
    @NotNull
    public Collection<Migration> getMigrations() {
        return this.migrations;
    }

    /**
     * Invoked if the version is lower than {@link #getStartVersion()}.
     */
    protected void handleTooLowerVersion(int version) {
        throw new IllegalStateException("The current version (" + version + ") is lower than " + getStartVersion());
    }

    /**
     * Invoked if the version is greater than {@link #getCurrentVersion()}.
     */
    protected void handleTooHigherVersion(int version) {
        throw new IllegalStateException("The current version (" + version + ") is greater than " + getCurrentVersion());
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
        int currentConfigVersion = readConfigurationVersion(reader, configurationData);
        AtomicBoolean migrationResult = new AtomicBoolean(NO_MIGRATION_NEEDED);

        // Migrate the configuration from the version 1 to 2 to 3 to ...
        migrations
            .forEach(migration -> {
                if (migration == null) return; // null-safe check.

                int fromVersion = migration.fromVersion();
                int toVersion = migration.toVersion();

                // start the migration.
                if (currentConfigVersion == fromVersion) {
                    migration.migrate(reader, configurationData);
                    setConfigurationVersion(configurationData, toVersion);
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
     * Gets the current configuration version.
     * If the property doesn't exist it will be created with the {@link #getStartVersion()}.
     *
     * @param reader the reader with which the configuration file can be read
     * @param configurationData the configuration data
     * @return the current configuration version.
     */
    protected int readConfigurationVersion(@NotNull PropertyReader reader, @NotNull ConfigurationData configurationData) {
        Property<Integer> configVersionProperty = PropertyInitializer.newProperty(getVersionPropertyName(), getStartVersion());

        // if the version property does not exist just add it.
        if (!reader.contains(getVersionPropertyName())) {
            configurationData.setValue(configVersionProperty, configVersionProperty.getDefaultValue());
            // TODO: add the property comment by using the #getConfigVersionPropertyComment() method.
        }

        return configVersionProperty.determineValue(reader).getValue();
    }

    protected void setConfigurationVersion(@NotNull ConfigurationData configurationData, int newVersion) {
        Property<Integer> configVersionProperty = PropertyInitializer.newProperty(getVersionPropertyName(), newVersion);
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
