package ch.jalu.configme.properties.types;

public class StringPropertyType implements PropertyType<String> {

    static final StringPropertyType INSTANCE = new StringPropertyType();

    StringPropertyType() {}

    @Override
    public String convert(Object object) {
        return object == null
            ? null
            : object.toString();
    }

    @Override
    public Class<String> getType() {
        return String.class;
    }

    @Override
    public Object toExportValue(String value) {
        return value;
    }

}
