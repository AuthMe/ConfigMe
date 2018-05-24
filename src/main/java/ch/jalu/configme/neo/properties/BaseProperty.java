package ch.jalu.configme.neo.properties;

import ch.jalu.configme.neo.resource.PropertyReader;

import javax.annotation.Nullable;
import java.util.Objects;

// TODO: Think about renaming this to NonNullProperty and supporting that by default but allowing other
// users to have nullable properties if they so desire.
public abstract class BaseProperty<T> implements Property<T> {

    private final String path;
    private final T defaultValue;

    protected BaseProperty(String path, T defaultValue) {
        Objects.requireNonNull(path, "path");
        Objects.requireNonNull(defaultValue, "defaultValue");
        this.path = path;
        this.defaultValue = defaultValue;
    }

    @Override
    public String getPath() {
        return path;
    }

    protected T getDefaultValue() {
        return defaultValue;
    }

    @Override
    public T getValue(PropertyReader reader) {
        T value = getFromResource(reader);
        return value != null ? value : getDefaultValue();
    }

    @Nullable
    protected abstract T getFromResource(PropertyReader reader);
}
