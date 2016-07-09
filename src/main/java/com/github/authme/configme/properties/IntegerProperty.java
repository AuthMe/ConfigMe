package com.github.authme.configme.properties;

import org.bukkit.configuration.file.FileConfiguration;

/**
 * Integer property.
 */
public class IntegerProperty extends Property<Integer> {

    public IntegerProperty(String path, Integer defaultValue) {
        super(path, defaultValue);
    }

    @Override
    public Integer getFromFile(FileConfiguration configuration) {
        return configuration.getInt(getPath(), getDefaultValue());
    }
}
