package com.github.authme.configme;

import com.github.authme.configme.configurationdata.ConfigurationData;
import com.github.authme.configme.configurationdata.ConfigurationDataBuilder;
import com.github.authme.configme.migration.MigrationService;
import com.github.authme.configme.properties.Property;
import com.github.authme.configme.resource.PropertyResource;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
public class SettingsManager {

    protected final ConfigurationData configurationData;
    protected final PropertyResource resource;
    protected final MigrationService migrationService;

    /**
     * Constructor.
     *
     * @param resource the property resource to read and write properties to
     * @param migrationService migration service to check the property resource with
     * @param settingsClasses classes whose Property fields make up all known properties
     */
    @SafeVarargs
    public SettingsManager(PropertyResource resource, @Nullable MigrationService migrationService,
                           Class<? extends SettingsHolder>... settingsClasses) {
        this(resource, migrationService, ConfigurationDataBuilder.collectData(settingsClasses));
    }

    /**
     * Constructor.
     *
     * @param resource the property resource to read and write properties to
     * @param migrationService migration service to check the property resource with
     * @param configurationData the configuration data
     */
    public SettingsManager(PropertyResource resource, @Nullable MigrationService migrationService,
                           ConfigurationData configurationData) {
        this.configurationData = configurationData;
        this.resource = resource;
        this.migrationService = migrationService;
        validateAndLoadOptions();
    }

    /**
     * Convenience method for creating a settings manager for the given collection of properties.
     *
     * @param resource the property resource to read and write from
     * @param migrationService migration service or null to skip migration check
     * @param properties the properties
     * @return the created settings manager
     */
    @SuppressWarnings("unchecked")
    public static SettingsManager createWithProperties(PropertyResource resource,
                                                       @Nullable MigrationService migrationService,
                                                       Collection<? extends Property<?>> properties) {
        List<Property<?>> propertyList = (properties instanceof List<?>)
            ? (List<Property<?>>) properties
            : new ArrayList<>(properties);
        return new SettingsManager(resource, migrationService, new ConfigurationData(propertyList));
    }

    /**
     * Gets the given property from the configuration.
     *
     * @param property The property to retrieve
     * @param <T> The property's type
     * @return The property's value
     */
    public <T> T getProperty(Property<T> property) {
        return property.getValue(resource);
    }

    /**
     * Sets a new value for the given property.
     *
     * @param property The property to modify
     * @param value The new value to assign to the property
     * @param <T> The property's type
     */
    public <T> void setProperty(Property<T> property, T value) {
        resource.setValue(property.getPath(), value);
    }

    /**
     * Reloads the configuration.
     */
    public void reload() {
        resource.reload();
        validateAndLoadOptions();
    }

    /**
     * Saves the config file. Use after migrating one or more settings.
     */
    public void save() {
        resource.exportProperties(configurationData);
    }

    /**
     * Checks with the migration service if the configuration is up to date.
     * If not, saves the config.
     */
    protected void validateAndLoadOptions() {
        if (migrationService != null
                && migrationService.checkAndMigrate(resource, configurationData.getProperties())) {
            save();
        }
    }

}
