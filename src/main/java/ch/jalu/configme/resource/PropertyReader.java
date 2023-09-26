package ch.jalu.configme.resource;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.List;
import java.util.Set;

/**
 * A property reader provides values from a resource (e.g. a YAML file) based on whose data the values of properties
 * are determined. Property readers typically provide a snapshot of the file's contents, i.e. their values are not
 * updated if the underlying file changes.
 */
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
    boolean contains(@NotNull String path);

    /**
     * Returns the keys available in the file. Depending on the parameter either all keys are returned,
     * or only the keys of the leaf nodes are considered.
     *
     * @param onlyLeafNodes true if only the paths of leaf nodes should be returned (no intermediate paths)
     * @return set of all existing keys (ordered)
     */
    @NotNull Set<String> getKeys(boolean onlyLeafNodes);

    /**
     * Returns the direct children of the given path which are available in the file. Returns an empty set
     * if the path does not exist in the file (never null).
     *
     * @param path the path whose direct child paths should be looked up
     * @return set of all direct children (ordered, never null)
     */
    @NotNull Set<String> getChildKeys(@NotNull String path);

    /**
     * Returns the object at the given path, or null if absent.
     *
     * @param path the path to retrieve the value for
     * @return the value, or null if there is none
     */
    @Nullable Object getObject(@NotNull String path);

    /**
     * Returns the value of the given path as a String if available.
     *
     * @param path the path to retrieve a String for
     * @return the value as a String, or null if not applicable or unavailable
     */
    @Nullable String getString(@NotNull String path);

    /**
     * Returns the value of the given path as an integer if available.
     *
     * @param path the path to retrieve an integer for
     * @return the value as integer, or null if not applicable or unavailable
     */
    @Nullable Integer getInt(@NotNull String path);

    /**
     * Returns the value of the given path as a double if available.
     *
     * @param path the path to retrieve a double for
     * @return the value as a double, or null if not applicable or unavailable
     */
    @Nullable Double getDouble(@NotNull String path);

    /**
     * Returns the value of the given path as a boolean if available.
     *
     * @param path the path to retrieve a boolean for
     * @return the value as a boolean, or null if not applicable or unavailable
     */
    @Nullable Boolean getBoolean(@NotNull String path);

    /**
     * Returns the value of the given path as a list if available.
     *
     * @param path the path to retrieve a list for
     * @return the value as a list, or null if not applicable or unavailable
     */
    @Nullable List<?> getList(@NotNull String path);

}
