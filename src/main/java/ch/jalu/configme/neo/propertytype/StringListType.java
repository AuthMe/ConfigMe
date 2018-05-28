package ch.jalu.configme.neo.propertytype;

import ch.jalu.configme.neo.resource.PropertyReader;

import java.util.List;

public class StringListType extends NonNullPropertyType<List<String>> {

    private static StringListType instance = new StringListType();

    protected StringListType() {
    }

    public static StringListType instance() {
        return instance;
    }

    @Override
    public List<String> getFromReader(PropertyReader reader, String path) {
        List<?> rawList = reader.getList(path);
        if (rawList != null) {
            for (Object o : rawList) {
                if (!(o instanceof String)) {
                    return null;
                }
            }
            // We checked that every entry is a String
            return (List<String>) rawList;
        }
        return null;
    }

    @Override
    public boolean isPresent(PropertyReader reader, String path) {
        return reader.getList(path) != null;
    }
}
