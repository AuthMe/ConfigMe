package ch.jalu.configme.resource.yaml;

import ch.jalu.configme.configurationdata.ConfigurationData;
import ch.jalu.configme.properties.convertresult.ValueWithComments;
import ch.jalu.configme.utils.StreamUtils;
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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static ch.jalu.configme.utils.PathUtils.concatSpecifierAware;
import static ch.jalu.configme.utils.PathUtils.pathSpecifierForIndex;
import static ch.jalu.configme.utils.PathUtils.pathSpecifierForMapKey;

/**
 * Default implementation of {@link SnakeYamlNodeBuilder}: creates SnakeYAML nodes for values and comments.
 */
public class SnakeYamlNodeBuilderImpl implements SnakeYamlNodeBuilder {

    private final Set<UUID> usedUniqueCommentIds = new HashSet<>();

    @Override
    public @NotNull Node createYamlNode(@NotNull Object obj, @NotNull String path,
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
                + (value == null ? null : value.getClass().getName()));
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
    public @NotNull Stream<CommentLine> createCommentLines(@NotNull String comment) {
        if ("\n".equals(comment)) {
            return Stream.of(new CommentLine(null, null, "", CommentType.BLANK_LINE));
        }

        return Arrays.stream(comment.split("\\n", -1))
            .map(text -> new CommentLine(null, null, " ".concat(text), CommentType.BLOCK));
    }

    @Override
    public void transferComments(@NotNull Node valueNode, @NotNull Node keyNode) {
        if (valueNode.getBlockComments() != null && !valueNode.getBlockComments().isEmpty()) {
            keyNode.setBlockComments(valueNode.getBlockComments());
            valueNode.setBlockComments(Collections.emptyList());
        }
    }

    protected @NotNull Node createStringNode(@NotNull String value) {
        return new ScalarNode(Tag.STR, value, null, null, DumperOptions.ScalarStyle.PLAIN);
    }

    protected @NotNull Node createNumberNode(@NotNull Number value) {
        Tag tag = (value instanceof Double || value instanceof Float || value instanceof BigDecimal)
            ? Tag.FLOAT
            : Tag.INT;
        return new ScalarNode(tag, value.toString(), null, null, DumperOptions.ScalarStyle.PLAIN);
    }

    protected @NotNull Node createBooleanNode(boolean value) {
        return new ScalarNode(Tag.BOOL, String.valueOf(value), null, null, DumperOptions.ScalarStyle.PLAIN);
    }

    /**
     * Creates a SnakeYAML node representing a sequence of the entries in the stream.
     *
     * @param entries stream of the entries (from a Java collection or array)
     * @param path the property path
     * @param configurationData the configuration data (to retrieve comments)
     * @return SnakeYAML node representing the entries
     */
    protected @NotNull Node createSequenceNode(@NotNull Stream<?> entries, @NotNull String path,
                                               @NotNull ConfigurationData configurationData) {
        AtomicInteger counter = new AtomicInteger();

        List<Node> values = entries
            .map(entry -> {
                String entryPath = concatSpecifierAware(path, pathSpecifierForIndex(counter.getAndIncrement()));
                return createYamlNode(entry, entryPath, configurationData, 0);
            })
            .collect(Collectors.toList());

        return new SequenceNode(Tag.SEQ, values, DumperOptions.FlowStyle.BLOCK);
    }

    /**
     * Creates a SnakeYAML node representing a map (collection of key-value pairs).
     *
     * @param value the map whose values should be in the returned node
     * @param path the property path
     * @param configurationData the configuration data (to retrieve comments)
     * @return SnakeYAML node representing the given map
     */
    protected @NotNull Node createMapNode(@NotNull Map<String, ?> value, String path,
                                          @NotNull ConfigurationData configurationData) {
        List<NodeTuple> nodeEntries = new ArrayList<>(value.size());

        for (Map.Entry<String, ?> entry : value.entrySet()) {
            Node keyNode = createKeyNode(entry.getKey());
            String entryPath = concatSpecifierAware(path, pathSpecifierForMapKey(entry));
            Node valueNode = createYamlNode(entry.getValue(), entryPath, configurationData, 0);
            transferComments(valueNode, keyNode);

            nodeEntries.add(new NodeTuple(keyNode, valueNode));
        }

        return new MappingNode(Tag.MAP, nodeEntries, DumperOptions.FlowStyle.BLOCK);
    }

    /**
     * Creates comments based on all possible sources (number of empty lines, configuration data,
     * {@link ValueWithComments}) and returns them as SnakeYAML comment lines.
     *
     * @param value the export value
     * @param path the path the value is located at
     * @param configurationData the configuration data instance
     * @param numberOfNewLines number of new lines to add to the beginning of the comments
     * @return comment lines representing all defined comments
     */
    protected @NotNull List<CommentLine> collectComments(@NotNull Object value, @NotNull String path,
                                                         @NotNull ConfigurationData configurationData,
                                                         int numberOfNewLines) {
        Stream<String> emptyLineStream = StreamUtils.repeat("\n", numberOfNewLines);
        Stream<String> configDataStream = configurationData.getCommentsForSection(path).stream();
        Stream<String> additionalCommentsStream =
            ValueWithComments.streamThroughCommentsIfApplicable(value, usedUniqueCommentIds);

        return Stream.of(emptyLineStream, configDataStream, additionalCommentsStream)
            .flatMap(Function.identity())
            .flatMap(this::createCommentLines)
            .collect(Collectors.toList());
    }

    /**
     * @return UUIDs of comments which should not be repeated that have already been included
     */
    protected final @NotNull Set<UUID> getUsedUniqueCommentIds() {
        return usedUniqueCommentIds;
    }
}
