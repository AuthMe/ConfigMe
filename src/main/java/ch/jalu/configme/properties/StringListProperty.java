package ch.jalu.configme.properties;

import ch.jalu.configme.properties.types.StringType;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * String list property. The lists are immutable.
 */
public class StringListProperty extends ListProperty<String> {

    public StringListProperty(@NotNull String path, String @NotNull ... defaultValue) {
        super(path, StringType.STRING, defaultValue);
    }

    public StringListProperty(@NotNull String path, @NotNull List<String> defaultValue) {
        super(path, StringType.STRING, defaultValue);
    }

    @Override
    public @NotNull Object toExportValue(@NotNull List<String> value) {
        return value;
    }
}
