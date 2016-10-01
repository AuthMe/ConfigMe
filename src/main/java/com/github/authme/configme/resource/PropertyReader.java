package com.github.authme.configme.resource;

/**
 * Property reader.
 */
public interface PropertyReader {

    /**
     * Returns the value for the given path, or null if not present.
     *
     * @param path the path to retrieve the value for
     * @return the value, or null if not available
     */
    Object getObject(String path);

    /**
     * Returns the value for the given path in a typed manner. Returns null if no value is
     * present or if the value does not match the requested type.
     *
     * @param path the path to retrieve the value for
     * @param clazz the class to cast the value to if possible
     * @param <T> the class' type
     * @return the typed value, or null if unavailable or not applicable
     */
    <T> T getTypedObject(String path, Class<T> clazz);

    /**
     * Sets the value at the given path in memory. This method does not persist any values
     * to an external resource; this should only be done by {@link PropertyResource#exportProperties}.
     *
     * @param path the path to set a value for
     * @param value the value to set
     */
    void set(String path, Object value);

    /**
     * Reloads the properties from the external source.
     */
    void reload();

}
