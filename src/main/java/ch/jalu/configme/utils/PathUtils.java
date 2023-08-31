package ch.jalu.configme.utils;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * Util for String paths like property paths or paths formed while traversing a YAML document.
 */
public final class PathUtils {

    /**
     * String appended to the path to specify that the value of an {@link java.util.Optional Optional}
     * is being resolved.
     */
    public static final String OPTIONAL_SPECIFIER = "$opt";

    private PathUtils() {
    }

    /**
     * Concatenates the child path to the parent path.
     *
     * @param parent the parent path (may be empty)
     * @param child the child path
     * @return the concatenated path
     */
    public static @NotNull String concat(@NotNull String parent, @NotNull String child) {
        if (parent.isEmpty()) {
            return child;
        }
        return parent + "." + child;
    }

    /**
     * Concatenates the child path to the parent path. Unlike {@link #concat}, this method does not add a
     * path separator {@code "."} if the child path is a "specifier path" (a suffix to specify that we're
     * iterating through an entry of the parent path, if it is a collection, for example).
     * <p>
     * Examples:<pre>{@code
     *   PathUtils.concatSpecifierAware("db.protocols", "[0]") // "db.protocols[0]"
     *   PathUtils.concatSpecifierAware("db.schema", "name")   // "db.schema.name"
     * }</pre>
     *
     * @param parent the parent path (may be empty)
     * @param child the child path
     * @return the concatenated path
     */
    public static @NotNull String concatSpecifierAware(@NotNull String parent, @NotNull String child) {
        if (isSpecifierSuffix(child)) {
            return parent.concat(child);
        }
        return concat(parent, child);
    }

    /**
     * Creates a path suffix specifying that it is for an index, in the form of "[0]".
     *
     * @param index the index
     * @return string targeting the index of a parent (the parent being a collection or an array)
     */
    public static @NotNull String pathSpecifierForIndex(int index) {
        return "[" + index + "]";
    }

    /**
     * Creates a path suffix specifying that it is for the key of the given entry, e.g. "[k=name]" if "name" is
     * the key in the given entry.
     * <p>
     * ConfigMe expects maps to always have String keys; this method does not require it to avoid unnecessary casts.
     *
     * @param entry the index
     * @return string targeting a specific entry of the parent (the parent being a map)
     */
    public static @NotNull String pathSpecifierForMapKey(@NotNull Map.Entry<?, ?> entry) {
        return "[k=" + entry.getKey() + "]";
    }

    /**
     * Creates a path suffix specifying that it is for the given key, e.g. "[k=name]" if the provided key is "name".
     *
     * @param key the key
     * @return string targeting a specific entry of the parent (the parent being a map)
     */
    public static @NotNull String pathSpecifierForMapKey(@NotNull String key) {
        return "[k=" + key + "]";
    }

    /**
     * Returns whether the child path is a specifying path. Note that the check is done rather crudely as
     * we don't expect regular paths to use {@code [} or {@code $}.
     *
     * @param path the path to inspect
     * @return true if it's a suffix for a parent path, false otherwise
     */
    public static boolean isSpecifierSuffix(@NotNull String path) {
        if (!path.isEmpty()) {
            char firstChar = path.charAt(0);
            return firstChar == '[' || firstChar == '$';
        }
        return false;
    }
}
