package ch.jalu.configme.properties.types;

import ch.jalu.configme.resource.PropertyReader;

public class LowerCaseStringPropertyType extends StringPropertyType {

    static final LowerCaseStringPropertyType INSTANCE = new LowerCaseStringPropertyType();

    LowerCaseStringPropertyType() {}

    @Override
    public String get(PropertyReader reader, String path) {
        String value = reader.getString(path);

        return value == null
            ? null
            : value.toLowerCase();
    }

    @Override
    public String convert(Object object) {
        return object.toString();
    }

}
