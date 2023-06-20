package ch.jalu.configme.properties;

import ch.jalu.configme.properties.types.PrimitivePropertyType;
import org.jetbrains.annotations.NotNull;

/**
 * String property. This extension exists for convenience and backwards compatibility.
 */
public class StringProperty extends TypeBasedProperty<String> {

    public StringProperty(@NotNull String path, @NotNull String defaultValue) {
        super(path, defaultValue, PrimitivePropertyType.STRING);
    }
}
