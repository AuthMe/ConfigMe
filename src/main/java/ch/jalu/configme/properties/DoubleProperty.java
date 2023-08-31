package ch.jalu.configme.properties;

import ch.jalu.configme.properties.types.NumberType;
import org.jetbrains.annotations.NotNull;

/**
 * Double property. This extension exists for convenience and backwards compatibility.
 */
public class DoubleProperty extends TypeBasedProperty<Double> {

    public DoubleProperty(@NotNull String path, double defaultValue) {
        super(path, defaultValue, NumberType.DOUBLE);
    }
}
