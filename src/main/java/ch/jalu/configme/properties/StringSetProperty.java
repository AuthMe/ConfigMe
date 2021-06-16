package ch.jalu.configme.properties;

import ch.jalu.configme.properties.types.PrimitivePropertyType;

import java.util.Set;

/**
 * String set property. The sets are immutable.
 */
public class StringSetProperty extends SetProperty<String> {

    public StringSetProperty(String path, String... defaultValue) {
        super(path, PrimitivePropertyType.STRING, defaultValue);
    }

    public StringSetProperty(String path, Set<String> defaultValue) {
        super(path, PrimitivePropertyType.STRING, defaultValue);
    }

    @Override
    public Object toExportValue(Set<String> value) {
        return value;
    }
}
