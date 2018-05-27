package ch.jalu.configme.neo.properties;

import ch.jalu.configme.neo.resource.PropertyReader;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class LowercaseStringSetProperty extends BaseProperty<Set<String>> {

    public LowercaseStringSetProperty(String path, String... defaultEntries) {
        super(path, toLinkedHashSet(defaultEntries));
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
    public Object toExportRepresentation(Set<String> value) {
        return value;
    }

    protected String convertToLowercaseString(@Nullable Object value) {
        // TODO: BEtter thing for arrays?
        return Objects.toString(value).toLowerCase();
    }

    private static Set<String> toLinkedHashSet(String... values) {
        return Collections.unmodifiableSet(new LinkedHashSet<>(Arrays.asList(values)));
    }
}
