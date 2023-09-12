package ch.jalu.configme.properties;

import ch.jalu.configme.properties.types.SetPropertyType;
import ch.jalu.configme.properties.types.StringType;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Collections.unmodifiableSet;

/**
 * Property whose value is a String set all in lowercase. The default value is immutable.
 * The encounter order of the default value and the constructed values is preserved, unless you've provided a custom
 * property type.
 */
public class LowercaseStringSetProperty extends TypeBasedProperty<Set<String>> {

    /**
     * Constructor.
     *
     * @param path property path
     * @param defaultEntries entries in the Set that is the default value
     */
    public LowercaseStringSetProperty(@NotNull String path, @NotNull String @NotNull ... defaultEntries) {
        super(path, toLowercaseLinkedHashSet(Arrays.stream(defaultEntries)),
            new SetPropertyType<>(StringType.STRING_LOWER_CASE));
    }

    /**
     * Constructor.
     *
     * @param path property path
     * @param defaultEntries entries in the Set that is the default value
     */
    public LowercaseStringSetProperty(@NotNull String path, @NotNull Collection<String> defaultEntries) {
        super(path, toLowercaseLinkedHashSet(defaultEntries.stream()),
            new SetPropertyType<>(StringType.STRING_LOWER_CASE));
    }

    protected static @NotNull Set<String> toLowercaseLinkedHashSet(@NotNull Stream<String> valuesStream) {
        Set<String> valuesLowercase = valuesStream
            .map(String::toLowerCase)
            .collect(Collectors.toCollection(LinkedHashSet::new));
        return unmodifiableSet(valuesLowercase);
    }
}
