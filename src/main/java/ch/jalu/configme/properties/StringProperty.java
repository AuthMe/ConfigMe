package ch.jalu.configme.properties;

import ch.jalu.configme.resource.PropertyReader;

public class StringProperty extends BaseProperty<String> {

    public StringProperty(String path, String defaultValue) {
        super(path, defaultValue);
    }

    @Override
    protected String getFromResource(PropertyReader reader) {
        return reader.getString(getPath());
    }

    @Override
    public Object toExportValue(String value) {
        return value;
    }
}
