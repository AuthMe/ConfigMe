package ch.jalu.configme.properties;

import ch.jalu.configme.properties.types.PrimitivePropertyType;

/**
 * Long property. This extension exists for convenience.
 */
public class LongProperty extends TypeBasedProperty<Long> {

    public LongProperty(String path, Long defaultValue) {
        super(path, defaultValue, PrimitivePropertyType.LONG);
    }
}
