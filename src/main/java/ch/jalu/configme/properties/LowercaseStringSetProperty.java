package ch.jalu.configme.properties;

import ch.jalu.configme.resource.PropertyReader;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LowercaseStringSetProperty extends BaseProperty<Set<String>> {

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
    protected Set<String> getFromResource(PropertyReader reader) {
        List<?> listFromReader = reader.getList(getPath());
        if (listFromReader != null) {
            Set<String> result = new LinkedHashSet<>(listFromReader.size());
            for (Object value : listFromReader) {
                result.add(convertToLowercaseString(value));
            }
            return result;
        }
        return null;
    }

    @Override
    public boolean isPresent(PropertyReader reader) {
        return reader.getList(getPath()) != null;
    }

    @Override
    public Object toExportValue(Set<String> value) {
        return value;
    }

    protected String convertToLowercaseString(@Nullable Object value) {
        return Objects.toString(value).toLowerCase();
    }

    private static Set<String> toLowercaseLinkedHashSet(Stream<String> valuesStream) {
        Set<String> valuesLowercase = valuesStream
            .map(String::toLowerCase)
            .collect(Collectors.toCollection(LinkedHashSet::new));
        return Collections.unmodifiableSet(valuesLowercase);
    }
}
