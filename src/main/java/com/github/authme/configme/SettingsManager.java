package com.github.authme.configme;

import com.github.authme.configme.migration.MigrationService;
import com.github.authme.configme.properties.Property;
import com.github.authme.configme.propertymap.PropertyMap;
import com.github.authme.configme.resource.PropertyResource;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * The new settings manager.
 */
public class SettingsManager {

    private final PropertyMap propertyMap;
    private final PropertyResource resource;
    private final MigrationService migrationService;

    /**
     * Constructor. Checks the given {@link FileConfiguration} object for completeness.
     *
     * @param propertyMap collection of all available settings
     * @param resource the property resource to read and write properties to
     * @param migrationService migration service to check the settings file with
     */
    public SettingsManager(PropertyMap propertyMap, PropertyResource resource, MigrationService migrationService) {
        this.propertyMap = propertyMap;
        this.resource = resource;
        this.migrationService = migrationService;
        validateAndLoadOptions();
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
        resource.exportProperties(propertyMap);
    }

    private void validateAndLoadOptions() {
        if (migrationService.checkAndMigrate(resource, propertyMap)) {
            save();
        }
    }

}
