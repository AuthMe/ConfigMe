package com.github.authme.configme.properties;

import org.bukkit.configuration.file.FileConfiguration;
import org.yaml.snakeyaml.Yaml;

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
    public String toYaml(FileConfiguration configuration, Yaml simpleYaml, Yaml singleQuoteYaml) {
        return singleQuoteYaml.dump(getFromFile(configuration));
    }
}
