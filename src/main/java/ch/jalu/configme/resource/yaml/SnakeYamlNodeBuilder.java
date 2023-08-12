package ch.jalu.configme.resource.yaml;

import ch.jalu.configme.configurationdata.ConfigurationData;
import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.comments.CommentLine;
import org.yaml.snakeyaml.nodes.Node;

import java.util.stream.Stream;

/**
 * Creates SnakeYAML nodes for values and comments.
 */
public interface SnakeYamlNodeBuilder {

    /**
     * Creates a SnakeYAML node representing the given value.
     *
     * @param value the value to create the node for (export value of a property)
     * @param path the path of the property whose value is exported
     * @param configurationData configuration data (for the retrieval of comments)
     * @param numberOfNewLines number of new lines before the property, to add as new lines to the node
     * @return SnakeYAML node of the appropriate type for the value, including comments
     */
    @NotNull Node createYamlNode(@NotNull Object value, @NotNull String path,
                                 @NotNull ConfigurationData configurationData, int numberOfNewLines);

    /**
     * Creates a SnakeYAML string node for a key value (e.g. object property, or map key).
     *
     * @param key the key to wrap into a node
     * @return a node representing the key value
     */
    @NotNull Node createKeyNode(@NotNull String key);

    /**
     * Creates SnakeYAML {@link CommentLine} objects to represent the given comment. If the comment is equal to the
     * new line character {@code \n}, one comment line representing a blank line is returned. Otherwise, a comment line
     * is created for each line (the text is split by {@code \n}).
     *
     * @param comment the comment to represent as CommentLine
     * @return stream with comment line objects representing the given comment
     */
    @NotNull Stream<CommentLine> createCommentLines(@NotNull String comment);

    /**
     * Transfers the comments from the value node to the key node. Logically, comments are associated with values,
     * but we do not want the comments to appear between the key and the value in the YAML output. Therefore, this
     * method is called before producing YAML as to move the comments from the value to the key node.
     *
     * @implNote Only considers {@link Node#getBlockComments() block comments} on the nodes because it's the only type
     *           of comment that this builder sets. Any block comments on the key node are overwritten.
     *
     * @param valueNode the value node to remove the comments from
     * @param keyNode the key node to set the comments to
     */
    void transferComments(@NotNull Node valueNode, @NotNull Node keyNode);

}
