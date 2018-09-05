package ch.jalu.configme.properties.types;

public class DoublePropertyType implements PropertyType<Double> {

    static final DoublePropertyType INSTANCE = new DoublePropertyType();

    private DoublePropertyType() {
        // Signleton
    }

    @Override
    public Double convert(Object object) {
        return object instanceof Number ? ((Number) object).doubleValue() : null;
    }

    @Override
    public Class<Double> getType() {
        return Double.class;
    }

    @Override
    public Object toExportValue(Double value) {
        return value;
    }

}
