package ch.jalu.configme.properties;

import ch.jalu.configme.properties.types.BooleanType;
import org.jetbrains.annotations.NotNull;

/**
 * Boolean property. This extension exists for convenience.
 */
public class BooleanProperty extends TypeBasedProperty<Boolean> {

    public BooleanProperty(@NotNull String path, @NotNull Boolean defaultValue) {
        super(path, BooleanType.BOOLEAN, defaultValue);
    }
}
