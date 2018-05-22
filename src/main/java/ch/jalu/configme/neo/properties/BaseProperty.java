package ch.jalu.configme.neo.properties;

import ch.jalu.configme.neo.resource.PropertyResource;

import javax.annotation.Nullable;
import java.util.Objects;

// TODO: Should we add a hamcrest-like "do_not_implement_interface()" method on Property?
// And/or think about renaming this to NonNullProperty and supporting that by default but allowing other
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

    @Override
    public T getDefaultValue() {
        return defaultValue;
    }

    @Override
    public T getValue(PropertyResource resource) {
        T value = getFromResource(resource);
        return value != null ? value : getDefaultValue();
    }

    @Nullable
    protected abstract T getFromResource(PropertyResource resource);
}
