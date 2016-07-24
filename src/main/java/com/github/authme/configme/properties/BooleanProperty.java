package com.github.authme.configme.properties;

import com.github.authme.configme.resource.PropertyResource;

/**
 * Boolean property.
 */
public class BooleanProperty extends Property<Boolean> {

    public BooleanProperty(String path, Boolean defaultValue) {
        super(path, defaultValue);
    }

    @Override
    public Boolean getFromResource(PropertyResource resource) {
        return resource.getBoolean(getPath());
    }
}
