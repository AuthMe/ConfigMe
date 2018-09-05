package ch.jalu.configme.properties.types;

public class BooleanPropertyType implements PropertyType<Boolean> {

    static final BooleanPropertyType INSTANCE = new BooleanPropertyType();

    private BooleanPropertyType() {
        // Signleton
    }

    @Override
    public Boolean convert(Object object) {
        return object instanceof Boolean ? (Boolean) object : null;
    }

    @Override
    public Class<Boolean> getType() {
        return Boolean.class;
    }

    @Override
    public Object toExportValue(Boolean value) {
        return value;
    }

}
