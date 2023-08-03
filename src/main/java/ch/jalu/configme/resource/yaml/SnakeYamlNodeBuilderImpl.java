package ch.jalu.configme.resource.yaml;

import ch.jalu.configme.configurationdata.ConfigurationData;
import ch.jalu.configme.properties.convertresult.ValueWithComments;
import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.comments.CommentLine;
import org.yaml.snakeyaml.comments.CommentType;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.SequenceNode;
import org.yaml.snakeyaml.nodes.Tag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Default implementation of {@link SnakeYamlNodeBuilder}: creates SnakeYAML nodes for values and comments.
 */
public class SnakeYamlNodeBuilderImpl implements SnakeYamlNodeBuilder {

    @Override
    public @NotNull Node toYamlNode(@NotNull Object obj, @NotNull String path,
                                    @NotNull ConfigurationData configurationData, int numberOfNewLines) {
        Object value = ValueWithComments.unwrapValue(obj);
        if (value instanceof Enum<?>) {
            value = ((Enum<?>) value).name();
        }

        Node node;
        if (value instanceof String) {
            node = createStringNode((String) value);
        } else if (value instanceof Number) {
            node = createNumberNode((Number) value);
        } else if (value instanceof Boolean) {
            node = createBooleanNode((Boolean) value);
        } else if (value instanceof Iterable<?>) {
            Stream<?> stream = StreamSupport.stream(((Iterable<?>) value).spliterator(), false);
            node = createSequenceNode(stream, path, configurationData);
        } else if (value instanceof Map<?, ?>) {
            node = createMapNode((Map<String, ?>) value, path, configurationData);
        } else if (value instanceof Object[]) {
            Stream<?> stream = Arrays.stream((Object[]) value);
            node = createSequenceNode(stream, path, configurationData);
        } else {
            throw new IllegalArgumentException("Unsupported value of type: "
                + (value == null ? null : value.getClass()));
        }

        List<CommentLine> commentLines = collectComments(obj, path, configurationData, numberOfNewLines);
        node.setBlockComments(commentLines);
        return node;
    }

    @Override
    public @NotNull Node createKeyNode(@NotNull String key) {
        return createStringNode(key);
    }

    @Override
    public @NotNull CommentLine createCommentLine(@NotNull String comment) {
        if ("\n".equals(comment)) {
            return new CommentLine(null, null, "", CommentType.BLANK_LINE);
        }
        return new CommentLine(null, null, " ".concat(comment), CommentType.BLOCK);
    }

    @Override
    public void transferComments(@NotNull Node valueNode, @NotNull Node keyNode) {
        if (!valueNode.getBlockComments().isEmpty()) {
            keyNode.setBlockComments(valueNode.getBlockComments());
            valueNode.setBlockComments(Collections.emptyList());
        }
    }

    protected @NotNull Node createStringNode(@NotNull String value) {
        return new ScalarNode(Tag.STR, value, null, null, DumperOptions.ScalarStyle.PLAIN);
    }

    protected @NotNull Node createNumberNode(@NotNull Number value) {
        Tag tag = (value instanceof Double || value instanceof Float) ? Tag.FLOAT : Tag.INT;
        return new ScalarNode(tag, value.toString(), null, null, DumperOptions.ScalarStyle.PLAIN);
    }

    protected @NotNull Node createBooleanNode(boolean value) {
        return new ScalarNode(Tag.BOOL, String.valueOf(value), null, null, DumperOptions.ScalarStyle.PLAIN);
    }

    protected @NotNull Node createSequenceNode(@NotNull Stream<?> entries, @NotNull String path,
                                               @NotNull ConfigurationData configurationData) {
        AtomicInteger counter = new AtomicInteger();
        String pathPrefix = path.isEmpty() ? "" : path.concat(".");

        List<Node> values = entries
            .map(entry -> {
                String entryPath = pathPrefix.concat(Integer.toString(counter.getAndIncrement()));
                return toYamlNode(entry, entryPath, configurationData, 0);
            })
            .collect(Collectors.toList());

        return new SequenceNode(Tag.SEQ, values, DumperOptions.FlowStyle.BLOCK);
    }

    protected @NotNull Node createMapNode(@NotNull Map<String, ?> value, String path,
                                          @NotNull ConfigurationData configurationData) {
        String pathPrefix = path.isEmpty() ? "" : path.concat(".");
        List<NodeTuple> nodeEntries = new ArrayList<>(value.size());

        for (Map.Entry<String, ?> entry : value.entrySet()) {
            Node keyNode = createKeyNode(entry.getKey());
            Node valueNode = toYamlNode(entry.getValue(), pathPrefix.concat(entry.getKey()), configurationData, 0);
            transferComments(valueNode, keyNode);

            nodeEntries.add(new NodeTuple(keyNode, valueNode));
        }

        return new MappingNode(Tag.MAP, nodeEntries, DumperOptions.FlowStyle.BLOCK);
    }

    protected @NotNull List<CommentLine> collectComments(@NotNull Object value, @NotNull String path,
                                                         @NotNull ConfigurationData configurationData,
                                                         int numberOfNewLines) {
        Stream<String> emptyLineStream = IntStream.range(0, numberOfNewLines)
            .mapToObj(i -> "\n");
        Stream<String> configDataStream = configurationData.getCommentsForSection(path).stream();
        Stream<String> additionalCommentsStream = (value instanceof ValueWithComments)
            ? ((ValueWithComments) value).getComments().stream()
            : Stream.empty();

        return Stream.of(emptyLineStream, configDataStream, additionalCommentsStream)
            .flatMap(Function.identity())
            .map(this::createCommentLine)
            .collect(Collectors.toList());
    }
}
