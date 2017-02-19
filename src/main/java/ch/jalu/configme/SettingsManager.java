package ch.jalu.configme;

import ch.jalu.configme.configurationdata.ConfigurationData;
import ch.jalu.configme.configurationdata.ConfigurationDataBuilder;
import ch.jalu.configme.migration.MigrationService;
import ch.jalu.configme.migration.PlainMigrationService;
import ch.jalu.configme.properties.OptionalProperty;
import ch.jalu.configme.properties.Property;
import ch.jalu.configme.resource.PropertyResource;
import ch.jalu.configme.resource.YamlFileResource;
import ch.jalu.configme.utils.Utils;

import javax.annotation.Nullable;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

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
     * Convenience method for creating a settings manager for the provided YAML file with defaults.
     * Creates the YAML file if it doesn't exist. Uses the default migration service, checking that all
     * properties are present in the YAML file.
     *
     * @param yamlFile the file to read from and write to
     * @param settingsClasses classes whose Property fields make up all known properties
     * @return the created settings manager
     */
    @SafeVarargs
    public static SettingsManager createWithYamlFile(File yamlFile,
                                                     Class<? extends SettingsHolder>... settingsClasses) {
        Utils.createFileIfNotExists(yamlFile);
        return new SettingsManager(new YamlFileResource(yamlFile), new PlainMigrationService(), settingsClasses);
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
        if (property instanceof OptionalProperty<?>) {
            resource.setValue(property.getPath(), ((Optional<?>) value).orElse(null));
        } else {
            resource.setValue(property.getPath(), value);
        }
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
