package ch.jalu.configme.properties.types;

import ch.jalu.configme.resource.PropertyReader;

public class StringPropertyType implements PropertyType<String> {

    static final StringPropertyType INSTANCE = new StringPropertyType();

    StringPropertyType() {}

    @Override
    public String get(PropertyReader reader, String path) {
        return reader.getString(path);
    }

    @Override
    public String convert(Object object) {
        return object.toString();
    }

    @Override
    public Class<String> getType() {
        return String.class;
    }

}
