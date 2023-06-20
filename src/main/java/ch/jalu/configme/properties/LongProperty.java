package ch.jalu.configme.properties;

import ch.jalu.configme.properties.types.PrimitivePropertyType;
import org.jetbrains.annotations.NotNull;

/**
 * Long property. This extension exists for convenience and backwards compatibility.
 */
public class LongProperty extends TypeBasedProperty<Long> {

    public LongProperty(@NotNull String path, @NotNull Long defaultValue) {
        super(path, defaultValue, PrimitivePropertyType.LONG);
    }
}
