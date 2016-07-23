package com.github.authme.configme.properties;

import com.github.authme.configme.resource.PropertyResource;

/**
 * Integer property.
 */
public class IntegerProperty extends Property<Integer> {

    public IntegerProperty(String path, Integer defaultValue) {
        super(path, defaultValue);
    }

    @Override
    public Integer getFromReader(PropertyResource resource) {
        return resource.getInt(getPath());
    }
}
