package ch.jalu.configme.neo.propertytype;

import ch.jalu.configme.neo.resource.PropertyReader;

public class StringType extends NonNullPropertyType<String> {

    private static StringType instance = new StringType();

    protected StringType() {
    }

    public static StringType instance() {
        return instance;
    }

    @Override
    public String getFromReader(PropertyReader reader, String path) {
        return reader.getString(path);
    }
}
