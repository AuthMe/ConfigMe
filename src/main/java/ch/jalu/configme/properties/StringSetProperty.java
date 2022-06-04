package ch.jalu.configme.properties;

import ch.jalu.configme.properties.types.PrimitivePropertyType;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * String set property. The sets are immutable.
 */
public class StringSetProperty extends SetProperty<String> {

    /**
     * Constructor.
     *
     * @param path the path of the property
     * @param defaultValue the values that make up the entries of the default set
     */
    public StringSetProperty(@NotNull String path, @NotNull String @NotNull... defaultValue) {
        super(path, PrimitivePropertyType.STRING, defaultValue);
    }

    /**
     * Constructor.
     *
     * @param path the path of the property
     * @param defaultValue the values that make up the entries of the default set
     */
    public StringSetProperty(@NotNull String path, @NotNull Set<String> defaultValue) {
        super(path, PrimitivePropertyType.STRING, defaultValue);
    }

    @Override
    public @NotNull Object toExportValue(@NotNull Set<String> value) {
        return value;
    }
}
