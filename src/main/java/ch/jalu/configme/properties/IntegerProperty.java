package ch.jalu.configme.properties;

import ch.jalu.configme.properties.types.NumberType;
import org.jetbrains.annotations.NotNull;

/**
 * Integer property. This extension exists for convenience.
 */
public class IntegerProperty extends TypeBasedProperty<Integer> {

    public IntegerProperty(@NotNull String path, @NotNull Integer defaultValue) {
        super(path, defaultValue, NumberType.INTEGER);
    }
}
