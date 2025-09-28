package ch.jalu.configme.properties;

import ch.jalu.configme.properties.types.OptionalPropertyType;
import ch.jalu.configme.properties.types.PropertyType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Optional property. Properties of this type may be absent from a property resource and it will still be considered
 * valid.
 *
 * @param <T> the type of value
 */
public class OptionalProperty<T> extends TypeBasedProperty<Optional<T>> {

    /**
     * Constructor. Creates a new property with an empty Optional as default value.
     *
     * @param path the path of the property
     * @param valueType the property type of the value inside the optional
     */
    public OptionalProperty(@NotNull String path, @NotNull PropertyType<T> valueType) {
        this(path, new OptionalPropertyType<>(valueType), Optional.empty());
    }

    /**
     * Constructor.
     *
     * @param path the path of the property
     * @param valueType the property type of the value inside the optional
     * @param defaultValue the default value of the property (will be wrapped in an Optional)
     */
    public OptionalProperty(@NotNull String path, @NotNull PropertyType<T> valueType, @Nullable T defaultValue) {
        this(path, new OptionalPropertyType<>(valueType), Optional.ofNullable(defaultValue));
    }

    /**
     * Constructor.
     *
     * @param path the path of the property
     * @param type the type of this property
     * @param defaultValue the default value of this property
     */
    public OptionalProperty(@NotNull String path, @NotNull PropertyType<Optional<T>> type,
                            @NotNull Optional<T> defaultValue) {
        super(path, type, defaultValue);
    }
}
