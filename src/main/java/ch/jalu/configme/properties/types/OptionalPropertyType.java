package ch.jalu.configme.properties.types;

import ch.jalu.configme.properties.convertresult.ConvertErrorRecorder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Property type for optionals. Wraps another property type.
 *
 * @param <T> the value type of the optional
 */
public class OptionalPropertyType<T> implements PropertyType<Optional<T>> {

    private final PropertyType<T> valueType;

    /**
     * Constructor.
     *
     * @param valueType the property type to handle the value inside the optional
     */
    public OptionalPropertyType(PropertyType<T> valueType) {
        this.valueType = valueType;
    }

    @Override
    public @Nullable Optional<T> convert(@Nullable Object object, @NotNull ConvertErrorRecorder errorRecorder) {
        if (object != null) {
            return Optional.ofNullable(valueType.convert(object, errorRecorder));
        }
        return Optional.empty();
    }

    @Override
    public @Nullable Object toExportValue(@NotNull Optional<T> value) {
        return value.map(valueType::toExportValue).orElse(null);
    }
}
