package com.github.authme.configme.resource;

import com.github.authme.configme.propertymap.PropertyEntry;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Property resource; provides and exports properties.
 */
public interface PropertyResource {

    // -------
    // Retrieval of values
    // -------

    /**
     * Returns the object at the given path, or null if absent.
     *
     * @param path the path to retrieve the value for
     * @return the value, or null if there is none
     */
    @Nullable
    Object getObject(String path);

    /**
     * Returns the value of the given path as a String if available.
     *
     * @param path the path to retrieve a String for
     * @return the value as a String, or null if not applicable or unavailable
     */
    @Nullable
    String getString(String path);

    /**
     * Returns the value of the given path as an integer if available.
     *
     * @param path the path to retrieve an integer for
     * @return the value as integer, or null if not applicable or unavailable
     */
    @Nullable
    Integer getInt(String path);

    /**
     * Returns the value of the given path as a double if available.
     *
     * @param path the path to retrieve a double for
     * @return the value as a double, or null if not applicable or unavailable
     */
    @Nullable
    Double getDouble(String path);

    /**
     * Returns the value of the given path as a boolean if available.
     *
     * @param path the path to retrieve a boolean for
     * @return the value as a boolean, or null if not applicable or unavailable
     */
    @Nullable
    Boolean getBoolean(String path);

    /**
     * Returns the value of the given path as a list if available.
     *
     * @param path the path to retrieve a list for
     * @return the value as a list, or null if not applicable or unavailable
     */
    @Nullable
    List<?> getList(String path);


    // -------
    // Reload / Modification
    // -------

    /**
     * Sets the value for the given path. Only modifies the in-memory collection of loaded values.
     * New values are only persisted after {@link #exportProperties(List)} has been called.
     *
     * @param path the path to set a new value for
     * @param value the value to set
     */
    void setValue(String path, Object value);

    /**
     * Reloads the configuration, e.g. from a file.
     */
    void reload();


    // -------
    // Export
    // -------

    /**
     * Exports the properties (e.g. writes to a file).
     *
     * @param knownProperties the property map for all properties to consider
     */
    void exportProperties(List<PropertyEntry> knownProperties);

}
