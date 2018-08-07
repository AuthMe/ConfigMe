package ch.jalu.configme.properties;

import ch.jalu.configme.resource.PropertyReader;

import java.util.Arrays;
import java.util.List;

public class StringListProperty extends BaseProperty<List<String>> {

    public StringListProperty(String path, String... defaultValue) {
        super(path, Arrays.asList(defaultValue));
    }

    @Override
    protected List<String> getFromResource(PropertyReader reader) {
        List<?> rawList = reader.getList(getPath());
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
    public boolean isPresent(PropertyReader reader) {
        return reader.getList(getPath()) != null;
    }

    @Override
    public Object toExportValue(List<String> value) {
        return value;
    }
}
