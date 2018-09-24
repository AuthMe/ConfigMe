package ch.jalu.configme.properties;

import ch.jalu.configme.properties.types.PrimitivePropertyType;

import java.util.List;

/**
 * String list property. The lists are immutable.
 */
public class StringListProperty extends ListProperty<String> {

    public StringListProperty(String path, String... defaultValue) {
        super(path, PrimitivePropertyType.STRING, defaultValue);
    }

    public StringListProperty(String path, List<String> defaultValue) {
        super(path, PrimitivePropertyType.STRING, defaultValue);
    }

    @Override
    public Object toExportValue(List<String> value) {
        return value;
    }
}
