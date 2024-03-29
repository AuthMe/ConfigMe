package ch.jalu.configme.properties;

import ch.jalu.configme.properties.types.NumberType;
import org.jetbrains.annotations.NotNull;

/**
 * Long property. This extension exists for convenience.
 */
public class LongProperty extends TypeBasedProperty<Long> {

    public LongProperty(@NotNull String path, long defaultValue) {
        super(path, NumberType.LONG, defaultValue);
    }
}
