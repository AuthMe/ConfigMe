package ch.jalu.configme.properties;

import ch.jalu.configme.properties.types.PrimitivePropertyType;

/**
 * Float property. This extension exists for convenience.
 */
public class FloatProperty extends TypeBasedProperty<Float> {

    public FloatProperty(String path, float defaultValue) {
        super(path, defaultValue, PrimitivePropertyType.FLOAT);
    }
}
