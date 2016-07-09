package com.github.authme.configme.properties;

import org.bukkit.configuration.file.FileConfiguration;
import org.yaml.snakeyaml.Yaml;

import java.util.Objects;

/**
 * A setting, i.e. a configuration that is read from the config.yml file.
 */
public abstract class Property<T> {

    private final String path;
    private final T defaultValue;

    protected Property(String path, T defaultValue) {
        Objects.requireNonNull(defaultValue);
        this.path = path;
        this.defaultValue = defaultValue;
    }

    /**
     * Gets the property value from the given configuration &ndash; guaranteed to never return null.
     *
     * @param configuration the configuration to read the value from
     * @return the value, or default if not present
     */
    public abstract T getFromFile(FileConfiguration configuration);

    /**
     * Returns whether or not the given configuration file contains the property.
     *
     * @param configuration the configuration file to verify
     * @return true if the property is present, false otherwise
     */
    public boolean isPresent(FileConfiguration configuration) {
        return configuration.contains(path);
    }

    /**
     * Formats the property's value as YAML.
     *
     * @param configuration the file configuration
     * @param simpleYaml YAML object (default)
     * @param singleQuoteYaml YAML object using single quotes
     * @return the generated YAML
     */
    public String toYaml(FileConfiguration configuration, Yaml simpleYaml, Yaml singleQuoteYaml) {
        return simpleYaml.dump(getFromFile(configuration));
    }

    /**
     * Returns the default value of the property.
     *
     * @return the default value
     */
    public T getDefaultValue() {
        return defaultValue;
    }

    /**
     * Returns the property path (i.e. the node at which this property is located in the YAML file).
     *
     * @return the path
     */
    public String getPath() {
        return path;
    }

    @Override
    public String toString() {
        return "Property '" + path + "'";
    }

}
