package ch.jalu.configme.neo.properties;

import ch.jalu.configme.neo.resource.PropertyResource;

public class StringProperty extends BaseProperty<String> {

    public StringProperty(String path, String defaultValue) {
        super(path, defaultValue);
    }

    @Override
    public boolean isPresent(PropertyResource resource) {
        return resource.getString(getPath()) != null;
    }

    @Override
    public Object toExportRepresentation(String value) {
        return value;
    }

    @Override
    protected String getFromResource(PropertyResource resource) {
        return resource.getString(getPath());
    }
}
