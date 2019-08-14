package ch.jalu.configme.properties;

import ch.jalu.configme.properties.types.PropertyType;
import ch.jalu.configme.resource.PropertyReader;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * List property of a configurable type. The lists are immutable.
 *
 * @param <T> the property type
 */
public class ListProperty<T> extends BaseProperty<List<T>> {

    private final PropertyType<T> type;

    /**
     * Constructor.
     *
     * @param path         the path of the property
     * @param type         the property type
     * @param defaultValue the entries in the list of the default value
     */
    @SafeVarargs
    public ListProperty(String path, PropertyType<T> type, T... defaultValue) {
        this(path, type, Arrays.asList(defaultValue));
    }

    /**
     * Constructor.
     *
     * @param path         the path of the property
     * @param type         the property type
     * @param defaultValue the default value of the property
     */
    public ListProperty(String path, PropertyType<T> type, List<T> defaultValue) {
        super(path, Collections.unmodifiableList(defaultValue));
        Objects.requireNonNull(type, "type");
        this.type = type;
    }

    @Nullable
    @Override
    protected List<T> getFromReader(PropertyReader reader) {
        List<?> list = reader.getList(getPath());

        if (list != null) {
            return Collections.unmodifiableList(list.stream()
                .map(type::convert)
                .filter(Objects::nonNull)
                .collect(Collectors.toList()));
        }
        return null;
    }

    @Override
    public Object toExportValue(List<T> value) {
        return value.stream()
            .map(type::toExportValue)
            .collect(Collectors.toList());
    }
}
