package ch.jalu.configme.properties.types;

import ch.jalu.configme.resource.PropertyReader;

public class FloatPropertyType implements PropertyType<Float> {

    static final FloatPropertyType INSTANCE = new FloatPropertyType();

    private FloatPropertyType() {
        // Signleton
    }

    @Override
    public Float get(PropertyReader reader, String path) {
        return reader.getFloat(path);
    }

    @Override
    public Float convert(Object object) {
        return object instanceof Number ? ((Number) object).floatValue() : null;
    }

    @Override
    public Class<Float> getType() {
        return Float.class;
    }

}
