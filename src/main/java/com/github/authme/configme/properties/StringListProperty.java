package com.github.authme.configme.properties;

import com.github.authme.configme.resource.PropertyResource;

import java.util.Arrays;
import java.util.List;

/**
 * String list property.
 */
public class StringListProperty extends Property<List<String>> {

    public StringListProperty(String path, String... defaultValues) {
        super(path, Arrays.asList(defaultValues));
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<String> getFromReader(PropertyResource resource) {
        List<?> rawList = resource.getList(getPath());
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
}
