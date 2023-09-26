package ch.jalu.configme;

import ch.jalu.configme.migration.MigrationService;
import ch.jalu.configme.properties.Property;
import org.jetbrains.annotations.NotNull;

/**
 * Settings manager.
 * <p>
 * The settings manager manages a {@link ch.jalu.configme.resource.PropertyResource property resource},
 * {@link ch.jalu.configme.configurationdata.ConfigurationData configuration data}, and an optional
 * {@link MigrationService migration service}.
 * <p>
 * The settings manager allows to look up and modify properties. After it is initialized, the settings manager
 * should be the only class from ConfigMe that developers need to interact with.
 *
 * @see <a href="https://github.com/AuthMe/ConfigMe">ConfigMe on Github</a>
 * @see SettingsManagerBuilder
 */
public interface SettingsManager {

    /**
     * Gets the given property from the configuration.
     *
     * @param property the property to retrieve
     * @param <T> the property's type
     * @return the property's value
     */
    <T> @NotNull T getProperty(@NotNull Property<T> property);

    /**
     * Sets a new value for the given property.
     *
     * @param property the property to modify
     * @param value the new value to assign to the property
     * @param <T> the property's type
     */
    <T> void setProperty(@NotNull Property<T> property, @NotNull T value);

    /**
     * Reloads the configuration from the property resource.
     */
    void reload();

    /**
     * Saves the properties to the configuration file.
     */
    void save();

}
