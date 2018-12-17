package ch.jalu.configme.properties;

import ch.jalu.configme.properties.types.PrimitivePropertyType;

/**
 * Integer property. This extension exists for convenience and backwards compatibility.
 */
public class IntegerProperty extends TypeBasedProperty<Integer> {

    public IntegerProperty(String path, Integer defaultValue) {
        super(path, defaultValue, PrimitivePropertyType.INTEGER);
    }
}
