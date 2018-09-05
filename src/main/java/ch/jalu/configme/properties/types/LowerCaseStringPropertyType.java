package ch.jalu.configme.properties.types;

import javax.annotation.Nullable;

public class LowerCaseStringPropertyType extends StringPropertyType {

    static final LowerCaseStringPropertyType INSTANCE = new LowerCaseStringPropertyType();

    LowerCaseStringPropertyType() {}

    @Override
    @Nullable
    public String convert(Object object) {
        String string = super.convert(object);

        return string == null
            ? null
            : string.toLowerCase();
    }

    @Override
    public Object toExportValue(String value) {
        return value == null
            ? null
            : value.toLowerCase();
    }

}
