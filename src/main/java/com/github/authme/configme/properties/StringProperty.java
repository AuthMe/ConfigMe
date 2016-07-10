package com.github.authme.configme.properties;

import org.bukkit.configuration.file.FileConfiguration;

/**
 * String property.
 */
public class StringProperty extends Property<String> {

    public StringProperty(String path, String defaultValue) {
        super(path, defaultValue);
    }

    @Override
    public String getFromFile(FileConfiguration configuration) {
        return configuration.getString(getPath(), getDefaultValue());
    }

    @Override
    public String toYaml(FileConfiguration configuration) {
        return getSingleQuoteYaml().dump(getFromFile(configuration));
    }
}
