package ch.jalu.configme.neo;

//import ch.jalu.configme.neo.SettingsHolder;
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
     * @param migrationService migration service to check the property resource with
     * @param configurationData the configuration data
     */
    public SettingsManagerImpl(PropertyResource resource, @Nullable MigrationService migrationService,
                               ConfigurationData configurationData, ValuesRegistry registry) {
        this.configurationData = configurationData;
        this.resource = resource;
        this.migrationService = migrationService;
        this.valuesRegistry = registry;
        loadFromResourceAndValidate();
    }

    // TODO: missing many convenience instantiation methods. Have them here or export them to some builder?
    // Having a separate config kind of thing would make it more flexible...

    /**
     * Gets the given property from the configuration.
     *
     * @param property The property to retrieve
     * @param <T> The property's type
     * @return The property's value
     */
    public <T> T getProperty(Property<T> property) {
        // return property.getValue(resource); // TODO: valueRegistry.get(property) ?
        // Would users understand that or is that way too confusing, given the methods on Property?
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Sets a new value for the given property.
     *
     * @param property The property to modify
     * @param value The new value to assign to the property
     * @param <T> The property's type
     */
    public <T> void setProperty(Property<T> property, T value) {
        throw new UnsupportedOperationException("Not yet implemented");  // TODO fix me
        // validate that value may be set by a method on Property? Would allow us to have BaseProperty for sure not
        // nullable while not overly restricting it for others...
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
        valuesRegistry.setAllProperties(reader, configurationData);

        if (migrationService != null && migrationService.checkAndMigrate(reader, configurationData.getAllProperties())) {
            // TODO: Update registry
            save();
        }
    }

}
