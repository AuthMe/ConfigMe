package com.github.authme.configme.properties;

import com.github.authme.configme.resource.PropertyResource;

import java.util.ArrayList;
import java.util.List;

/**
 * Lowercase String list property.
 */
public class LowercaseStringListProperty extends StringListProperty {

    public LowercaseStringListProperty(String path, String... defaultValues) {
        super(path, defaultValues);
    }

    @Override
    public List<String> getFromResource(PropertyResource resource) {
        List<String> list = super.getFromResource(resource);
        if (list != null) {
            List<String> lowercaseList = new ArrayList<>(list.size());
            for (String element : list) {
                lowercaseList.add(element.toLowerCase());
            }
            return lowercaseList;
        }
        return null;
    }
}
