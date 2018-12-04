package ch.jalu.configme;

import ch.jalu.configme.configurationdata.ConfigurationData;
import ch.jalu.configme.migration.MigrationService;
import ch.jalu.configme.properties.Property;
import ch.jalu.configme.resource.PropertyReader;
import ch.jalu.configme.resource.PropertyResource;

import javax.annotation.Nullable;

/**
 * Default implementation of {@link SettingsManager}. Use the {@link SettingsManagerBuilder} to create instances.
 * <p>
 * The settings manager unites a {@link PropertyResource property resource},
 * a {@link MigrationService migration service} and the list of known properties
 * (typically gathered from {@link SettingsHolder} classes).
 * <p>
 * The settings manager allows to look up and modify properties. Thus, the settings
 * manager fulfills the most typical operations on a configuration in an application.
 * After initializing the settings manager, it is usually the only class from ConfigMe
 * you interact with.
 *
 * @see PropertyResource
 * @see ConfigurationData
 * @see MigrationService
 */
public class SettingsManagerImpl implements SettingsManager {

    private final ConfigurationData configurationData;
    private final PropertyResource resource;
    private final MigrationService migrationService;

    /**
     * Constructor. Use {@link SettingsManagerBuilder} to create instances.
     *
     * @param resource the property resource to read from and write to
     * @param configurationData the configuration data
     * @param migrationService migration service to check the property resource with
     */
    protected SettingsManagerImpl(PropertyResource resource, ConfigurationData configurationData,
                                  @Nullable MigrationService migrationService) {
        this.configurationData = configurationData;
        this.resource = resource;
        this.migrationService = migrationService;
        loadFromResourceAndValidate();
    }

    /**
     * Gets the given property from the configuration.
     *
     * @param property The property to retrieve
     * @param <T> The property's type
     * @return The property's value
     */
    @Override
    public <T> T getProperty(Property<T> property) {
        return configurationData.getValue(property);
    }

    /**
     * Sets a new value for the given property.
     *
     * @param property The property to modify
     * @param value The new value to assign to the property
     * @param <T> The property's type
     */
    @Override
    public <T> void setProperty(Property<T> property, T value) {
        configurationData.setValue(property, value);
    }

    @Override
    public void reload() {
        loadFromResourceAndValidate();
    }

    @Override
    public void save() {
        resource.exportProperties(configurationData);
    }

    /**
     * Reads the configuration file and executes the migration service (if present). Saves the file if migrations
     * have been applied.
     */
    protected void loadFromResourceAndValidate() {
        final PropertyReader reader = resource.createReader();
        configurationData.initializeValues(reader);

        if (migrationService != null
            && migrationService.checkAndMigrate(reader, configurationData) == MigrationService.MIGRATION_REQUIRED) {
            save();
        }
    }

    protected final PropertyResource getPropertyResource() {
        return resource;
    }

    protected final ConfigurationData getConfigurationData() {
        return configurationData;
    }

    @Nullable
    protected final MigrationService getMigrationService() {
        return migrationService;
    }
}
