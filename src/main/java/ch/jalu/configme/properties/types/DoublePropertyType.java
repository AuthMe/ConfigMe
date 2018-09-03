package ch.jalu.configme.properties.types;

import ch.jalu.configme.resource.PropertyReader;

public class DoublePropertyType implements PropertyType<Double> {

    static final DoublePropertyType INSTANCE = new DoublePropertyType();

    private DoublePropertyType() {
        // Signleton
    }

    @Override
    public Double get(PropertyReader reader, String path) {
        return reader.getDouble(path);
    }

    @Override
    public Double convert(Object object) {
        return object instanceof Number ? ((Number) object).doubleValue() : null;
    }

    @Override
    public Class<Double> getType() {
        return Double.class;
    }

}
