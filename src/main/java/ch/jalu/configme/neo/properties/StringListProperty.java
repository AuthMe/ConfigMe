package ch.jalu.configme.neo.properties;

import ch.jalu.configme.neo.propertytype.StringListType;

import java.util.Arrays;
import java.util.List;

/**
 * String list property.
 */
public class StringListProperty extends BaseProperty<List<String>> {

    public StringListProperty(String path, String... defaultValues) {
        super(path, Arrays.asList(defaultValues), StringListType.instance());
    }
}
