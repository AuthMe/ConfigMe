package ch.jalu.configme.resource.yaml;

import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.comments.CommentLine;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.Tag;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Implementation of {@link SnakeYamlNodeContainer}.
 */
public class SnakeYamlNodeContainerImpl implements SnakeYamlNodeContainer {

    private final List<String> comments;
    private final Map<String, Object> values = new LinkedHashMap<>();

    public SnakeYamlNodeContainerImpl(@NotNull List<String> comments) {
        this.comments = comments;
    }

    @Override
    public @NotNull SnakeYamlNodeContainer getOrCreateChildContainer(@NotNull String name,
                                                                     @NotNull Supplier<List<String>> commentsSupplier) {
        Object value = values.computeIfAbsent(name, k -> new SnakeYamlNodeContainerImpl(commentsSupplier.get()));
        if (!(value instanceof SnakeYamlNodeContainer)) {
            throw new IllegalStateException("Unexpectedly found " + value.getClass().getName() + " in '" + name + "'");
        }
        return (SnakeYamlNodeContainer) value;
    }

    @Override
    public @NotNull Node getRootValueNode() {
        Object rootValue = values.get("");
        if (rootValue == null) {
            throw new IllegalStateException("No value was stored for the root path ''");
        }
        return (Node) rootValue;
    }

    @Override
    public void putNode(@NotNull String name, @NotNull Node node) {
        if (values.containsKey(name)) {
            throw new IllegalStateException("Container unexpectedly already contains entry for '" + name + "'");
        }
        values.put(name, node);
    }

    @Override
    public @NotNull Node convertToNode(@NotNull SnakeYamlNodeBuilder nodeBuilder) {
        List<NodeTuple> entryNodes = new ArrayList<>(values.size());
        for (Map.Entry<String, Object> entry : values.entrySet()) {
            Node keyNode = nodeBuilder.createKeyNode(entry.getKey());
            Node valueNode = entry.getValue() instanceof SnakeYamlNodeContainer
                ? ((SnakeYamlNodeContainer) entry.getValue()).convertToNode(nodeBuilder)
                : (Node) entry.getValue();

            nodeBuilder.transferComments(valueNode, keyNode);
            entryNodes.add(new NodeTuple(keyNode, valueNode));
        }

        Node mappingNode = createRootNode(entryNodes);
        List<CommentLine> commentLines = comments.stream()
            .flatMap(nodeBuilder::createCommentLines)
            .collect(Collectors.toList());
        mappingNode.setBlockComments(commentLines);
        return mappingNode;
    }

    protected Node createRootNode(List<NodeTuple> entryNodes) {
        return new MappingNode(Tag.MAP, entryNodes, DumperOptions.FlowStyle.BLOCK);
    }

    protected final List<String> getComments() {
        return comments;
    }

    protected final Map<String, Object> getValues() {
        return values;
    }
}
