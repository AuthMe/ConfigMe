package ch.jalu.configme.properties;

import ch.jalu.configme.resource.PropertyReader;

import javax.annotation.Nullable;
import java.util.Objects;

public abstract class BaseProperty<T> implements Property<T> {

    private final String path;
    private final T defaultValue;

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
    public T determineValue(PropertyReader reader) {
        T value = getFromResource(reader);
        return value != null ? value : getDefaultValue();
    }

    @Override
    public boolean isPresent(PropertyReader reader) {
        return getFromResource(reader) != null;
    }

    @Override
    public boolean isValidValue(Object value) {
        return value != null;
    }

    @Nullable
    protected abstract T getFromResource(PropertyReader reader);

    @Override
    public String toString() {
        return "Property '" + path + "'";
    }
}
