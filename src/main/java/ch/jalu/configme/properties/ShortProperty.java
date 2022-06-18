package ch.jalu.configme.properties;

import ch.jalu.configme.properties.types.PrimitivePropertyType;

/**
 * Short property. This extension exists for convenience.
 */
public class ShortProperty extends TypeBasedProperty<Short> {

    public ShortProperty(String path, Short defaultValue) {
        super(path, defaultValue, PrimitivePropertyType.SHORT);
    }
}
