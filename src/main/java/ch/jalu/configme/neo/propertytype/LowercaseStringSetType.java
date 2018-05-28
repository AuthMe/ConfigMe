package ch.jalu.configme.neo.propertytype;

import ch.jalu.configme.neo.resource.PropertyReader;

import javax.annotation.Nullable;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class LowercaseStringSetType extends NonNullPropertyType<Set<String>> {

    private static LowercaseStringSetType instance = new LowercaseStringSetType();

    protected LowercaseStringSetType() {
    }

    public static LowercaseStringSetType instance() {
        return instance;
    }

    @Override
    public Set<String> getFromReader(PropertyReader reader, String path) {
        List<?> listFromReader = reader.getList(path);
        if (listFromReader != null) {
            Set<String> result = new LinkedHashSet<>(listFromReader.size());
            for (Object value : listFromReader) {
                result.add(convertToLowercaseString(value));
            }
            return result;
        }
        return null;
    }

    protected String convertToLowercaseString(@Nullable Object value) {
        return Objects.toString(value).toLowerCase();
    }

    @Override
    public boolean isPresent(PropertyReader reader, String path) {
        return reader.getList(path) != null;
    }
}
