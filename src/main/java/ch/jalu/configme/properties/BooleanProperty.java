package ch.jalu.configme.properties;

import ch.jalu.configme.resource.PropertyReader;

public class BooleanProperty extends BaseProperty<Boolean> {

    public BooleanProperty(String path, Boolean defaultValue) {
        super(path, defaultValue);
    }

    @Override
    protected Boolean getFromResource(PropertyReader reader) {
        return reader.getBoolean(getPath());
    }

    @Override
    public Object toExportValue(Boolean value) {
        return value;
    }
}
