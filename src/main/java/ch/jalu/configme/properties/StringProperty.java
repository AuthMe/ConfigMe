package ch.jalu.configme.properties;

import ch.jalu.configme.properties.types.StringType;
import org.jetbrains.annotations.NotNull;

/**
 * String property. This extension exists for convenience.
 */
public class StringProperty extends TypeBasedProperty<String> {

    public StringProperty(@NotNull String path, @NotNull String defaultValue) {
        super(path, StringType.STRING, defaultValue);
    }
}
