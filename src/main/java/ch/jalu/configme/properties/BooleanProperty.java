package ch.jalu.configme.properties;

import ch.jalu.configme.resource.PropertyResource;

/**
 * Boolean property.
 */
public class BooleanProperty extends Property<Boolean> {

    public BooleanProperty(String path, Boolean defaultValue) {
        super(path, defaultValue);
    }

    @Override
    protected Boolean getFromResource(PropertyResource resource) {
        return resource.getBoolean(getPath());
    }
}
