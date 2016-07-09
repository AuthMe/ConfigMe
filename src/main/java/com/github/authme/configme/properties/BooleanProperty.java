package com.github.authme.configme.properties;

import org.bukkit.configuration.file.FileConfiguration;

/**
 * Boolean property.
 */
public class BooleanProperty extends Property<Boolean> {

    public BooleanProperty(String path, Boolean defaultValue) {
        super(path, defaultValue);
    }

    @Override
    public Boolean getFromFile(FileConfiguration configuration) {
        return configuration.getBoolean(getPath(), getDefaultValue());
    }
}
