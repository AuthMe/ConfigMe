package ch.jalu.configme.properties;

import ch.jalu.configme.properties.types.StringType;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Property whose value is a String set all in lowercase. The default value is immutable.
 * The encounter order of the default value and the constructed values is preserved.
 */
public class LowercaseStringSetProperty extends SetProperty<String> {

    /**
     * Constructor.
     *
     * @param path property path
     * @param defaultEntries entries in the Set that is the default value
     */
    public LowercaseStringSetProperty(@NotNull String path, @NotNull String @NotNull ... defaultEntries) {
        super(path, StringType.STRING_LOWER_CASE, toLowercaseLinkedHashSet(Arrays.stream(defaultEntries)));
    }

    /**
     * Constructor.
     *
     * @param path property path
     * @param defaultEntries entries in the Set that is the default value
     */
    public LowercaseStringSetProperty(@NotNull String path, @NotNull Collection<String> defaultEntries) {
        super(path, StringType.STRING_LOWER_CASE, toLowercaseLinkedHashSet(defaultEntries.stream()));
    }

    protected static @NotNull Set<String> toLowercaseLinkedHashSet(@NotNull Stream<String> valuesStream) {
        return valuesStream
            .map(value -> value.toLowerCase(Locale.ROOT))
            .collect(Collectors.toCollection(LinkedHashSet::new));
    }
}
