package ch.jalu.configme.properties;

import ch.jalu.configme.resource.PropertyReader;

public class DoubleProperty extends BaseProperty<Double> {

    public DoubleProperty(String path, double defaultValue) {
        super(path, defaultValue);
    }

    @Override
    protected Double getFromResource(PropertyReader reader) {
        return reader.getDouble(getPath());
    }

    @Override
    public Object toExportValue(Double value) {
        return value;
    }

}
