package ch.jalu.configme.properties;

import ch.jalu.configme.properties.convertresult.ConvertErrorRecorder;
import ch.jalu.configme.properties.types.PropertyType;
import ch.jalu.configme.resource.PropertyReader;
import org.jetbrains.annotations.NotNull;

import org.jetbrains.annotations.Nullable;
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
     * @param path the path of the property
     * @param type the property type
     * @param defaultValue the default value of the property
     */
    public TypeBasedProperty(@NotNull String path, @NotNull PropertyType<T> type, @NotNull T defaultValue) {
        super(path, defaultValue);
        Objects.requireNonNull(type, "type");
        this.type = type;
    }

    @Override
    protected @Nullable T getFromReader(@NotNull PropertyReader reader, @NotNull ConvertErrorRecorder errorRecorder) {
        return type.convert(reader.getObject(getPath()), errorRecorder);
    }

    @Override
    public @Nullable Object toExportValue(@NotNull T value) {
        return type.toExportValue(value);
    }

    /**
     * @return the property type this property makes use of
     */
    public @NotNull PropertyType<T> getType() {
        return type;
    }
}
