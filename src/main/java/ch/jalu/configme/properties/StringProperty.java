package ch.jalu.configme.properties;

import ch.jalu.configme.properties.types.PrimitivePropertyType;

/**
 * String property. This extension exists for convenience and backwards compatibility.
 */
public class StringProperty extends TypeBasedProperty<String> {

    public StringProperty(String path, String defaultValue) {
        super(path, defaultValue, PrimitivePropertyType.STRING);
    }
}
