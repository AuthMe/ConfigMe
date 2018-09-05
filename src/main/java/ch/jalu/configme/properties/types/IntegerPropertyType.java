package ch.jalu.configme.properties.types;

public class IntegerPropertyType implements PropertyType<Integer> {

    static final IntegerPropertyType INSTANCE = new IntegerPropertyType();

    private IntegerPropertyType() {
        // Signleton
    }

    @Override
    public Integer convert(Object object) {
        return object instanceof Number ? ((Number) object).intValue() : null;
    }

    @Override
    public Class<Integer> getType() {
        return Integer.class;
    }

    @Override
    public Object toExportValue(Integer value) {
        return value;
    }

}
