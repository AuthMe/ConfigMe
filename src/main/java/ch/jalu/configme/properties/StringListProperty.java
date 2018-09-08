package ch.jalu.configme.properties;

import ch.jalu.configme.resource.PropertyReader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * String list property. The lists are immutable.
 */
public class StringListProperty extends BaseProperty<List<String>> {

    public StringListProperty(String path, String... defaultValue) {
        this(path, Arrays.asList(defaultValue));
    }

    public StringListProperty(String path, List<String> defaultValue) {
        super(path, Collections.unmodifiableList(defaultValue));
    }

    @Override
    protected List<String> getFromReader(PropertyReader reader) {
        List<?> listFromReader = reader.getList(getPath());
        if (listFromReader != null) {
            List<String> result = new ArrayList<>(listFromReader.size());
            for (Object o : listFromReader) {
                result.add(Objects.toString(o));
            }
            return Collections.unmodifiableList(result);
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
