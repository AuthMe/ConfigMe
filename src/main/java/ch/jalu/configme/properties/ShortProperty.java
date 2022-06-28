package ch.jalu.configme.properties;

import ch.jalu.configme.properties.types.PrimitivePropertyType;
import org.jetbrains.annotations.NotNull;

/**
 * Short property. This extension exists for convenience and backwards compatibility.
 */
public class ShortProperty extends TypeBasedProperty<Short> {

    public ShortProperty(@NotNull String path, @NotNull Short defaultValue) {
        super(path, defaultValue, PrimitivePropertyType.SHORT);
    }
}
