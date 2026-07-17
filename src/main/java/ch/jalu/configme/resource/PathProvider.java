package ch.jalu.configme.resource;

import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * Exposes the paths available in a configuration resource.
 * <p>
 * This interface is an optional extension for {@link PropertyReader} implementations that support path enumeration.
 */
public interface PathProvider {

    /**
     * Returns all paths available in the resource, including intermediate paths.
     * <p>
     * For a configuration like:
     * <pre>
     * header:
     *   title: Hello
     *   font:
     *     color: red
     *     size: 12
     * </pre>
     * this method returns {@code ["header", "header.title", "header.font", "header.font.color", "header.font.size"]}.
     *
     * @return all paths (in encounter order)
     */
    @NotNull Set<String> getPaths();

    /**
     * Returns all leaf paths in the resource.
     * <p>
     * For the configuration example in {@link #getPaths()}, this method returns
     * {@code ["header.title", "header.font.color", "header.font.size"]}.
     *
     * @return all leaf paths (in encounter order)
     */
    @NotNull Set<String> getLeafPaths();

    /**
     * Returns the direct child paths of the given path. Returns an empty set
     * if the path does not exist or has no children.
     * <p>
     * For the configuration example in {@link #getPaths()},
     * {@code getChildPaths("header")} returns {@code ["header.title", "header.font"]}.
     * <p>
     * If {@code path} is empty, the top-level paths are returned.
     *
     * @param path the path whose direct child paths should be looked up
     * @return all direct child paths (in encounter order, never null)
     */
    @NotNull Set<String> getChildPaths(@NotNull String path);

}
