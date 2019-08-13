package ch.jalu.configme.resource;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;

public interface PropertyReader {

    /**
     * Returns whether a value is present for the given path. When applicable,
     * {@link ch.jalu.configme.properties.Property#determineValue(PropertyReader)} should be favored over
     * calling this method as it may make more type-aware checks. This method simply returns whether <i>some value</i>
     * exists under the given path.
     *
     * @param path the path to check
     * @return true if there is a value, false otherwise
     */
    boolean contains(String path);

    /**
     * Returns the keys available in the file. Depending on the parameter either all keys are returned,
     * or only the keys of the leaf nodes are considered.
     *
     * @param onlyLeafNodes true if only the paths of leaf nodes should be returned (no intermediate paths)
     * @return set of all existing keys (ordered)
     */
    Set<String> getKeys(boolean onlyLeafNodes);

    /**
     * Returns the direct children of the given path which are available in the file. Returns an empty set
     * if the path does not exist in the file (never null).
     *
     * @param path the path whose direct child paths should be looked up
     * @return set of all direct children (ordered, never null)
     */
    Set<String> getChildKeys(String path);

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

}
