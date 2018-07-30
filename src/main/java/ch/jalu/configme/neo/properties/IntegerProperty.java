package ch.jalu.configme.neo.properties;

import ch.jalu.configme.neo.resource.PropertyReader;

public class IntegerProperty extends BaseProperty<Integer> {

    public IntegerProperty(String path, Integer defaultValue) {
        super(path, defaultValue);
    }

    @Override
    protected Integer getFromResource(PropertyReader reader) {
        return reader.getInt(getPath());
    }

    @Override
    public Object toExportValue(Integer value) {
        return value;
    }
}
