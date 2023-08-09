package ch.jalu.configme.resource.yaml;

import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.nodes.Node;

import java.util.List;
import java.util.function.Supplier;

/**
 * Container that keeps SnakeYAML node objects in hierarchical order. Leaf values are SnakeYAML nodes, while parent
 * values are containers that can be converted to SnakeYAML nodes representing all enclosed values.
 */
public interface SnakeYamlNodeContainer {

    /**
     * Returns the existing container for the given name, or creates one and registers the comments as returned by the
     * supplier. An exception is thrown if a value (SnakeYAML node) was saved under the given name.
     *
     * @param name the path name to get
     * @param commentsSupplier supplier with comments to set if the container has to be created
     * @return container for the given path name
     */
    @NotNull SnakeYamlNodeContainer getOrCreateChildContainer(String name, Supplier<List<String>> commentsSupplier);

    /**
     * Returns the SnakeYAML node at the root path (empty string). Used as root of the YAML document when the
     * configuration only has one property at root path. Throws an exception if no value was stored for the root path.
     *
     * @return internal root node
     */
    @NotNull Node getRootValueNode();

    /**
     * Saves the given node under the given name (= path element). Throws an exception if a value is already associated
     * with the given name.
     *
     * @param name the name to save the value under
     * @param node the node to save
     */
    void putNode(@NotNull String name, @NotNull Node node);

    /**
     * Converts this container and its sub-containers, recursively, to a SnakeYAML node that represents
     * all SnakeYAML nodes held by the containers.
     *
     * @param nodeBuilder node builder to create nodes with
     * @return this container's values as SnakeYAML node
     */
    @NotNull Node convertToNode(@NotNull SnakeYamlNodeBuilder nodeBuilder);

}
