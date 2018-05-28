package ch.jalu.configme.neo.propertytype;

import ch.jalu.configme.neo.resource.PropertyReader;

public class BooleanType extends NonNullPropertyType<Boolean> {

    private static BooleanType instance = new BooleanType();

    protected BooleanType() {
    }

    public static BooleanType instance() {
        return instance;
    }

    @Override
    public Boolean getFromReader(PropertyReader reader, String path) {
        return reader.getBoolean(path);
    }
}
