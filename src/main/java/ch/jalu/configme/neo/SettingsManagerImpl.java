package ch.jalu.configme.neo;

import ch.jalu.configme.neo.configurationdata.ConfigurationData;
import ch.jalu.configme.neo.migration.MigrationService;
import ch.jalu.configme.neo.properties.Property;
import ch.jalu.configme.neo.registry.ValuesRegistry;
import ch.jalu.configme.neo.resource.PropertyReader;
import ch.jalu.configme.neo.resource.PropertyResource;

import javax.annotation.Nullable;

/**
 * Settings manager.
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
 * @see <a href="https://github.com/AuthMe/ConfigMe">ConfigMe on Github</a>
 * @see PropertyResource
 * @see MigrationService
 * @see SettingsHolder
 */
public class SettingsManagerImpl implements SettingsManager {

    protected final ConfigurationData configurationData;
    protected final PropertyResource resource;
    protected final MigrationService migrationService;
    protected final ValuesRegistry valuesRegistry;

    /**
     * Constructor.
     *
     * @param resource the property resource to read and write properties to
     * @param configurationData the configuration data
     * @param migrationService migration service to check the property resource with
     * @param registry values registry
     */
    protected SettingsManagerImpl(PropertyResource resource, ConfigurationData configurationData,
                                  @Nullable MigrationService migrationService, ValuesRegistry registry) {
        this.configurationData = configurationData;
        this.resource = resource;
        this.migrationService = migrationService;
        this.valuesRegistry = registry;
        loadFromResourceAndValidate();
        // TODO: Configuration validation on "startup"
    }

    /**
     * Gets the given property from the configuration.
     *
     * @param property The property to retrieve
     * @param <T> The property's type
     * @return The property's value
     */
    public <T> T getProperty(Property<T> property) {
        return valuesRegistry.get(property);
    }

    /**
     * Sets a new value for the given property.
     *
     * @param property The property to modify
     * @param value The new value to assign to the property
     * @param <T> The property's type
     */
    public <T> void setProperty(Property<T> property, T value) {
        valuesRegistry.set(property, value);
    }

    /**
     * Reloads the configuration.
     */
    public void reload() {
        loadFromResourceAndValidate();
    }

    public void save() {
        // TODO: resource.exportProperties(configurationData, valueRegistry) ?
        throw new UnsupportedOperationException("Not yet implemented");
    }

    protected void loadFromResourceAndValidate() {
        final PropertyReader reader = resource.createReader();
        valuesRegistry.initializeValues(reader, configurationData);

        if (migrationService != null && migrationService.checkAndMigrate(reader, valuesRegistry, configurationData)) {
            save();
        }
    }

}
