package ch.jalu.configme.properties;

import ch.jalu.configme.resource.PropertyReader;

/**
 * String property.
 */
public class StringProperty extends BaseProperty<String> {

    public StringProperty(String path, String defaultValue) {
        super(path, defaultValue);
    }

    @Override
    protected String getFromResource(PropertyReader reader) {
        Object value = reader.getObject(getPath());
        return value == null ? null : value.toString();
    }

    @Override
    public Object toExportValue(String value) {
        return value;
    }
}
