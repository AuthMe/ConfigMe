package ch.jalu.configme.neo.properties;

import ch.jalu.configme.neo.resource.PropertyReader;

/**
 * Boolean property.
 */
public class BooleanProperty extends BaseProperty<Boolean> {

    public BooleanProperty(String path, Boolean defaultValue) {
        super(path, defaultValue);
    }

    @Override
    protected Boolean getFromResource(PropertyReader reader) {
        return reader.getBoolean(getPath());
    }

    @Override
    public Object toExportRepresentation(Boolean value) {
        return value;
    }
}