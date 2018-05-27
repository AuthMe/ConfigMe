package ch.jalu.configme.neo.properties;

import ch.jalu.configme.neo.resource.PropertyReader;

public class StringProperty extends BaseProperty<String> {

    public StringProperty(String path, String defaultValue) {
        super(path, defaultValue);
    }

    @Override
    protected String getFromResource(PropertyReader reader) {
        return reader.getString(getPath());
    }

    @Override
    public Object toExportRepresentation(String value) {
        return value;
    }
}
