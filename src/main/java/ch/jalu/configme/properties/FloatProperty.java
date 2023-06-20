package ch.jalu.configme.properties;

import ch.jalu.configme.properties.types.PrimitivePropertyType;
import org.jetbrains.annotations.NotNull;

/**
 * Float property. This extension exists for convenience.
 */
public class FloatProperty extends TypeBasedProperty<Float> {

    public FloatProperty(@NotNull String path, float defaultValue) {
        super(path, defaultValue, PrimitivePropertyType.FLOAT);
    }
}
