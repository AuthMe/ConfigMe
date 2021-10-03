package ch.jalu.configme.properties;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Collections.unmodifiableSet;

/**
 * Property whose value is a String set all in lowercase. The sets are immutable.
 */
public class LowercaseStringSetProperty extends StringSetProperty {

    /**
     * Constructor.
     *
     * @param path property path
     * @param defaultEntries entries in the Set that is the default value
     */
    public LowercaseStringSetProperty(String path, String... defaultEntries) {
        super(path, toLowercaseLinkedHashSet(Arrays.stream(defaultEntries)));
    }

    /**
     * Constructor.
     *
     * @param path property path
     * @param defaultEntries entries in the Set that is the default value
     */
    public LowercaseStringSetProperty(String path, Collection<String> defaultEntries) {
        super(path, toLowercaseLinkedHashSet(defaultEntries.stream()));
    }

    @Override
    protected Collector<String, ?, Set<String>> setCollector() {
        Function<String, String> toLowerCaseFn = value -> String.valueOf(value).toLowerCase();
        return Collectors.mapping(toLowerCaseFn, super.setCollector());
    }

    protected static Set<String> toLowercaseLinkedHashSet(Stream<String> valuesStream) {
        Set<String> valuesLowercase = valuesStream
            .map(String::toLowerCase)
            .collect(Collectors.toCollection(LinkedHashSet::new));
        return unmodifiableSet(valuesLowercase);
    }
}
