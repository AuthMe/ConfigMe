package com.github.authme.configme.properties;

import org.bukkit.configuration.file.FileConfiguration;

import java.util.Arrays;
import java.util.List;

/**
 * String list property.
 */
public class StringListProperty extends Property<List<String>> {

    public StringListProperty(String path, String... defaultValues) {
        super(path, Arrays.asList(defaultValues));
    }

    @Override
    public List<String> getFromFile(FileConfiguration configuration) {
        if (!configuration.isList(getPath())) {
            return getDefaultValue();
        }
        return configuration.getStringList(getPath());
    }

    @Override
    public boolean isPresent(FileConfiguration configuration) {
        return configuration.isList(getPath());
    }

    @Override
    public String toYaml(FileConfiguration configuration) {
        List<String> value = getFromFile(configuration);
        String yaml = getSingleQuoteYaml().dump(value);
        // If the property is a non-empty list we need to append a new line because it will be
        // something like the following, which requires a new line:
        // - 'item 1'
        // - 'second item in list'
        return value.isEmpty() ? yaml : "\n" + yaml;
    }
}
