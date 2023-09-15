package ch.jalu.configme.properties;

import ch.jalu.configme.properties.types.NumberType;
import org.jetbrains.annotations.NotNull;

/**
 * Short property. This extension exists for convenience.
 */
public class ShortProperty extends TypeBasedProperty<Short> {

    public ShortProperty(@NotNull String path, @NotNull Short defaultValue) {
        super(path, defaultValue, NumberType.SHORT);
    }
}
