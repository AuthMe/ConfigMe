package com.github.authme.configme.properties;

import com.github.authme.configme.resource.PropertyResource;

import javax.annotation.Nullable;
import java.util.Objects;

/**
 * A setting, i.e. a configuration that is read from the config.yml file.
 */
public abstract class Property<T> {

    private final String path;
    private final T defaultValue;

    protected Property(String path, T defaultValue) {
        Objects.requireNonNull(path);
        Objects.requireNonNull(defaultValue);
        this.path = path;
        this.defaultValue = defaultValue;
    }

    /**
     * Gets the property value from the given resource and falls back to the default value if not present.
     * Guaranteed to never return null.
     *
     * @param resource the property resource
     * @return the value, or default if not present
     */
    public T getValue(PropertyResource resource) {
        T value = getFromResource(resource);
        return value == null ? defaultValue : value;
    }

    @Nullable
    protected abstract T getFromResource(PropertyResource resource);

    /**
     * Returns whether or not the given resource contains the property.
     *
     * @param resource the property resource to check
     * @return true if the property is present, false otherwise
     */
    public boolean isPresent(PropertyResource resource) {
        return getFromResource(resource) != null;
    }

    /**
     * Returns the default value of the property.
     *
     * @return the default value
     */
    public T getDefaultValue() {
        return defaultValue;
    }

    /**
     * Returns the property path.
     *
     * @return the path
     */
    public String getPath() {
        return path;
    }

    @Override
    public String toString() {
        return "Property '" + path + "'";
    }

}
