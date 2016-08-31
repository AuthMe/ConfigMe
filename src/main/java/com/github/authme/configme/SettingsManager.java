package com.github.authme.configme;

import com.github.authme.configme.migration.MigrationService;
import com.github.authme.configme.properties.Property;
import com.github.authme.configme.propertymap.PropertyEntry;
import com.github.authme.configme.propertymap.SettingsFieldRetriever;
import com.github.authme.configme.resource.PropertyResource;

import java.util.Collections;
import java.util.List;

/**
 * The new settings manager.
 */
public class SettingsManager {

    protected final List<PropertyEntry> knownProperties;
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
    public SettingsManager(PropertyResource resource, MigrationService migrationService,
                           Class<? extends SettingsHolder>... settingsClasses) {
        this(resource, migrationService, SettingsFieldRetriever.getAllProperties(settingsClasses));
    }

    /**
     * Constructor.
     *
     * @param resource the property resource to read and write properties to
     * @param migrationService migration service to check the property resource with
     * @param knownProperties collection of all available settings
     */
    public SettingsManager(PropertyResource resource, MigrationService migrationService,
                           List<? extends PropertyEntry> knownProperties) {
        this.knownProperties = Collections.unmodifiableList(knownProperties);
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
        resource.exportProperties(knownProperties);
    }

    protected void validateAndLoadOptions() {
        if (migrationService.checkAndMigrate(resource, knownProperties)) {
            save();
        }
    }

}
