package ch.jalu.configme.resource.yaml;

import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.nodes.Node;

import java.util.List;
import java.util.function.Supplier;

/**
 * Container that keeps SnakeYAML node objects in hierarchical order. Leaf values are SnakeYAML nodes, while parent
 * nodes are a container that can be converted to a SnakeYAML node.
 */
public interface SnakeYamlNodeContainer {

    /**
     * Returns the existing container for the given name, or creates one and registers the comments as defined by the
     * supplier. An exception is thrown if a node was saved for the given name.
     *
     * @param name the path name to get
     * @param commentsSupplier supplier with comments to set if the container has to be created
     * @return container for the given path name
     */
    @NotNull SnakeYamlNodeContainer getOrCreateChildContainer(String name, Supplier<List<String>> commentsSupplier);

    /**
     * Returns the SnakeYAML node at the root path (empty string). Used as root of the YAML document when the
     * configuration only has a property at root path.
     *
     * @return internal root node
     */
    @NotNull Node getRootValueNode();

    /**
     * Saves the given node at the given path name. Throws an exception if a value is already associated for the given
     * path name.
     *
     * @param name the name to save the value under
     * @param node the node to save
     */
    void putNode(@NotNull String name, @NotNull Node node);

    /**
     * Converts his container and its sub-containers, recursively, to a SnakeYAML node that represents
     * the container's values.
     *
     * @param nodeBuilder node builder to create nodes with
     * @return this container's values as SnakeYAML node
     */
    @NotNull Node convertToNode(@NotNull SnakeYamlNodeBuilder nodeBuilder);

}
