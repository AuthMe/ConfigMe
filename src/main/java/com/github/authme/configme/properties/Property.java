package com.github.authme.configme.properties;

import org.bukkit.configuration.file.FileConfiguration;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.util.Objects;

/**
 * A setting, i.e. a configuration that is read from the config.yml file.
 */
public abstract class Property<T> {

    private static Yaml simpleYaml;
    private static Yaml singleQuoteYaml;

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
     * @return the generated YAML
     */
    public String toYaml(FileConfiguration configuration) {
        return getSimpleYaml().dump(getFromFile(configuration));
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

    /**
     * Returns a YAML instance set to export values with the default style.
     *
     * @return YAML instance
     */
    protected Yaml getSimpleYaml() {
        if (simpleYaml == null) {
            simpleYaml = newYaml(false);
        }
        return simpleYaml;
    }

    /**
     * Returns a YAML instance set to export values with single quotes.
     *
     * @return YAML instance
     */
    protected Yaml getSingleQuoteYaml() {
        if (singleQuoteYaml == null) {
            singleQuoteYaml = newYaml(true);
        }
        return singleQuoteYaml;
    }

    private static Yaml newYaml(boolean useSingleQuotes) {
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setAllowUnicode(true);
        if (useSingleQuotes) {
            options.setDefaultScalarStyle(DumperOptions.ScalarStyle.SINGLE_QUOTED);
        }
        return new Yaml(options);
    }

    @Override
    public String toString() {
        return "Property '" + path + "'";
    }

}
