package ch.jalu.configme.properties;

import ch.jalu.configme.properties.types.PropertyType;
import ch.jalu.configme.resource.PropertyReader;

import javax.annotation.Nullable;
import java.util.Objects;

/**
 * Property implementation which relies on a {@link PropertyType}.
 *
 * @param <T> type of property value
 */
public class TypeBasedProperty<T> extends BaseProperty<T> {

    private final PropertyType<T> type;

    /**
     * Constructor.
     *
     * @param path         the path of the property
     * @param defaultValue the default value of the property
     * @param type         the property type
     */
    public TypeBasedProperty(String path, T defaultValue, PropertyType<T> type) {
        super(path, defaultValue);
        Objects.requireNonNull(type, "type");
        this.type = type;
    }

    @Nullable
    @Override
    protected T getFromReader(PropertyReader reader) {
        return type.convert(reader.getObject(getPath()));
    }

    @Nullable
    @Override
    public Object toExportValue(T value) {
        return type.toExportValue(value);
    }
}
