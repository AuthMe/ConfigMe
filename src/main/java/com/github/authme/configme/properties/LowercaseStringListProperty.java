package com.github.authme.configme.properties;

import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

/**
 * Lowercase String list property.
 */
public class LowercaseStringListProperty extends StringListProperty {

    public LowercaseStringListProperty(String path, String... defaultValues) {
        super(path, defaultValues);
    }

    @Override
    public List<String> getFromFile(FileConfiguration configuration) {
        if (!configuration.isList(getPath())) {
            return getDefaultValue();
        }

        // make sure all elements are lowercase
        List<String> lowercaseList = new ArrayList<>();
        for (String element : configuration.getStringList(getPath())) {
            lowercaseList.add(element.toLowerCase());
        }

        return lowercaseList;
    }
}
