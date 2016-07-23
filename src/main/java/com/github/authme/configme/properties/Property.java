package com.github.authme.configme.properties;

import com.github.authme.configme.resource.PropertyResource;

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
     * Gets the property value from the given configuration &ndash; guaranteed to never return null.
     *
     * @param resource the property source
     * @return the value, or default if not present
     */
    public T getValue(PropertyResource resource) {
        T value = getFromReader(resource);
        return value == null ? defaultValue : value;
    }

    protected abstract T getFromReader(PropertyResource resource);

    /**
     * Returns whether or not the given source contains the property.
     *
     * @param resource the property source to verify with
     * @return true if the property is present, false otherwise
     */
    public boolean isPresent(PropertyResource resource) {
        return resource.contains(path);
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

    protected T nullToDefault(T readValue) {
        return readValue == null ? defaultValue : readValue;
    }

    @Override
    public String toString() {
        return "Property '" + path + "'";
    }

}
