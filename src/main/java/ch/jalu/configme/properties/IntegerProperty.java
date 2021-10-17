package ch.jalu.configme.properties;

import ch.jalu.configme.properties.types.PrimitivePropertyType;
import org.jetbrains.annotations.NotNull;

/**
 * Integer property. This extension exists for convenience and backwards compatibility.
 */
public class IntegerProperty extends TypeBasedProperty<Integer> {

    public IntegerProperty(@NotNull String path, @NotNull Integer defaultValue) {
        super(path, defaultValue, PrimitivePropertyType.INTEGER);
    }
}
