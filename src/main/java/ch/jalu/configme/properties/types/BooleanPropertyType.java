package ch.jalu.configme.properties.types;

import ch.jalu.configme.resource.PropertyReader;

public class BooleanPropertyType implements PropertyType<Boolean> {

    static final BooleanPropertyType INSTANCE = new BooleanPropertyType();

    private BooleanPropertyType() {
        // Signleton
    }

    @Override
    public Boolean get(PropertyReader reader, String path) {
        return reader.getBoolean(path);
    }

    @Override
    public Boolean convert(Object object) {
        return object instanceof Boolean ? (Boolean) object : null;
    }

    @Override
    public Class<Boolean> getType() {
        return Boolean.class;
    }

}
