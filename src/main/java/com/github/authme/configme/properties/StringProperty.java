package com.github.authme.configme.properties;

import com.github.authme.configme.resource.PropertyResource;

/**
 * String property.
 */
public class StringProperty extends Property<String> {

    public StringProperty(String path, String defaultValue) {
        super(path, defaultValue);
    }

    @Override
    public String getFromReader(PropertyResource resource) {
        return resource.getString(getPath());
    }
}
