package ch.jalu.configme.neo.propertytype;

import ch.jalu.configme.neo.resource.PropertyReader;

public class IntegerType extends NonNullPropertyType<Integer> {

    private static IntegerType instance = new IntegerType();

    protected IntegerType() {
    }

    public static IntegerType instance() {
        return instance;
    }

    @Override
    public Integer getFromReader(PropertyReader reader, String path) {
        return reader.getInt(path);
    }
}
