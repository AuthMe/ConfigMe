package ch.jalu.configme.properties;

import ch.jalu.configme.configurationdata.PropertyValue;
import ch.jalu.configme.resource.PropertyReader;

import javax.annotation.Nullable;
import java.util.Objects;

/**
 * Base implementation of {@link Property}. All properties should extend from this class.
 * <p>
 * This base implementation makes interacting with properties null safe by guaranteeing that the default value
 * and its {@link #determineValue determined value} can never be null.
 *
 * @param <T> the property type
 */
public abstract class BaseProperty<T> implements Property<T> {

    private final String path;
    private final T defaultValue;

    /**
     * Constructor.
     *
     * @param path the path of the property
     * @param defaultValue the default value of the property
     */
    public BaseProperty(String path, T defaultValue) {
        Objects.requireNonNull(path, "path");
        Objects.requireNonNull(defaultValue, "defaultValue");
        this.path = path;
        this.defaultValue = defaultValue;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public T getDefaultValue() {
        return defaultValue;
    }

    @Override
    public PropertyValue<T> determineValue(PropertyReader reader) {
        T value = getFromReader(reader);
        return PropertyValue.defaultIfInvalid(value, this);
    }

    @Override
    public boolean isValidValue(T value) {
        return value != null;
    }

    /**
     * Constructs the value of the property from the property reader. Returns null if no value is
     * available in the reader or if it cannot be used to construct a value for this property.
     *
     * @param reader the reader to read from
     * @return value based on the reader, or null if not applicable
     */
    @Nullable
    protected abstract T getFromReader(PropertyReader reader);

    @Override
    public String toString() {
        return "Property '" + path + "'";
    }
}
