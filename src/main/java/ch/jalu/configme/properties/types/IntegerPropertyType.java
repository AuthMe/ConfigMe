package ch.jalu.configme.properties.types;

import ch.jalu.configme.resource.PropertyReader;

public class IntegerPropertyType implements PropertyType<Integer> {

    static final IntegerPropertyType INSTANCE = new IntegerPropertyType();

    private IntegerPropertyType() {
        // Signleton
    }

    @Override
    public Integer get(PropertyReader reader, String path) {
        return reader.getInt(path);
    }

    @Override
    public Integer convert(Object object) {
        return object instanceof Number ? ((Number) object).intValue() : null;
    }

    @Override
    public Class<Integer> getType() {
        return Integer.class;
    }

}
