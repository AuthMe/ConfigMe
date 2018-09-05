package ch.jalu.configme.properties.types;

public class FloatPropertyType implements PropertyType<Float> {

    static final FloatPropertyType INSTANCE = new FloatPropertyType();

    private FloatPropertyType() {
        // Signleton
    }

    @Override
    public Float convert(Object object) {
        return object instanceof Number ? ((Number) object).floatValue() : null;
    }

    @Override
    public Class<Float> getType() {
        return Float.class;
    }

    @Override
    public Object toExportValue(Float value) {
        return value;
    }

}
