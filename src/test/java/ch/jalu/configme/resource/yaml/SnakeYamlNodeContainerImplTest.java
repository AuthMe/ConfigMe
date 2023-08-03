package ch.jalu.configme.resource.yaml;

import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.comments.CommentLine;
import org.yaml.snakeyaml.comments.CommentType;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.Tag;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static ch.jalu.configme.resource.yaml.SnakeYamlNodeBuilderImplTest.isBlankComment;
import static ch.jalu.configme.resource.yaml.SnakeYamlNodeBuilderImplTest.isBlockComment;
import static ch.jalu.configme.resource.yaml.SnakeYamlNodeBuilderImplTest.isScalarNode;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Test for {@link SnakeYamlNodeContainerImpl}.
 */
class SnakeYamlNodeContainerImplTest {

    @Test
    void shouldStoreNodes() {
        // given
        SnakeYamlNodeContainerImpl rootContainer = new SnakeYamlNodeContainerImpl(Collections.emptyList());
        ScalarNode stringNode = new ScalarNode(Tag.STR, "test", null, null, DumperOptions.ScalarStyle.PLAIN);
        ScalarNode boolNode = new ScalarNode(Tag.BOOL, "true", null, null, DumperOptions.ScalarStyle.PLAIN);

        // when
        rootContainer.putNode("name", stringNode);
        SnakeYamlNodeContainerImpl debugContainer =
            (SnakeYamlNodeContainerImpl) rootContainer.getOrCreateChildContainer("debug", () -> Arrays.asList("dc1", "dc2"));
        debugContainer.putNode("log", boolNode);

        // then
        assertThat(rootContainer.getComments(), empty());
        assertThat(rootContainer.getValues().keySet(), contains("name", "debug"));
        assertThat(rootContainer.getValues().get("name"), sameInstance(stringNode));
        assertThat(rootContainer.getValues().get("debug"), sameInstance(debugContainer));

        assertThat(rootContainer.getOrCreateChildContainer("debug", () -> null), sameInstance(debugContainer));
        assertThat(debugContainer.getComments(), contains("dc1", "dc2"));
        assertThat(debugContainer.getValues().keySet(), contains("log"));
        assertThat(debugContainer.getValues().values(), contains(boolNode));
    }

    @Test
    void shouldCreateMapNodeFromAllValuesAndMoveCommentsToKeyNodes() {
        // given
        SnakeYamlNodeContainerImpl rootContainer = new SnakeYamlNodeContainerImpl(Arrays.asList("root1", "\n", "root2"));
        ScalarNode stringNode = new ScalarNode(Tag.STR, "test", null, null, DumperOptions.ScalarStyle.PLAIN);
        stringNode.setBlockComments(Arrays.asList(
            new CommentLine(null, null, "sc1", CommentType.BLOCK),
            new CommentLine(null, null, "sc2", CommentType.BLOCK)));

        ScalarNode boolNode = new ScalarNode(Tag.BOOL, "true", null, null, DumperOptions.ScalarStyle.PLAIN);
        boolNode.setBlockComments(Collections.singletonList(
            new CommentLine(null, null, "bc1", CommentType.BLOCK)));

        rootContainer.putNode("name", stringNode);
        SnakeYamlNodeContainerImpl debugContainer =
            (SnakeYamlNodeContainerImpl) rootContainer.getOrCreateChildContainer("debug", () -> Arrays.asList("dc1", "dc2"));
        debugContainer.putNode("log", boolNode);

        SnakeYamlNodeBuilderImpl nodeBuilder = new SnakeYamlNodeBuilderImpl();

        // when
        Node rootNode = rootContainer.convertToNode(nodeBuilder);

        // then
        assertThat(rootNode, instanceOf(MappingNode.class));
        assertThat(rootNode.getBlockComments(), hasSize(3));
        assertThat(rootNode.getBlockComments().get(0), isBlockComment(" root1"));
        assertThat(rootNode.getBlockComments().get(1), isBlankComment());
        assertThat(rootNode.getBlockComments().get(2), isBlockComment(" root2"));

        List<NodeTuple> nodeTuples = ((MappingNode) rootNode).getValue();
        assertThat(nodeTuples, hasSize(2));
        assertThat(nodeTuples.get(0).getKeyNode(), isScalarNode(Tag.STR, "name"));
        assertThat(nodeTuples.get(0).getKeyNode().getBlockComments(), hasSize(2));
        assertThat(nodeTuples.get(0).getKeyNode().getBlockComments().get(0), isBlockComment("sc1"));
        assertThat(nodeTuples.get(0).getKeyNode().getBlockComments().get(1), isBlockComment("sc2"));
        assertThat(nodeTuples.get(0).getValueNode(), sameInstance(stringNode));
        assertThat(nodeTuples.get(0).getValueNode().getBlockComments(), empty());

        assertThat(nodeTuples.get(1).getKeyNode(), isScalarNode(Tag.STR, "debug"));
        assertThat(nodeTuples.get(1).getKeyNode().getBlockComments(), hasSize(2));
        assertThat(nodeTuples.get(1).getKeyNode().getBlockComments().get(0), isBlockComment(" dc1"));
        assertThat(nodeTuples.get(1).getKeyNode().getBlockComments().get(1), isBlockComment(" dc2"));
        assertThat(nodeTuples.get(1).getValueNode(), instanceOf(MappingNode.class));
        assertThat(nodeTuples.get(1).getValueNode().getBlockComments(), empty());
        List<NodeTuple> debugNodeTuples = ((MappingNode) nodeTuples.get(1).getValueNode()).getValue();
        assertThat(debugNodeTuples, hasSize(1));
        assertThat(debugNodeTuples.get(0).getKeyNode(), isScalarNode(Tag.STR, "log"));
        assertThat(debugNodeTuples.get(0).getValueNode(), sameInstance(boolNode));
    }

    @Test
    void shouldThrowForAlreadyExistingValue() {
        // given
        SnakeYamlNodeContainer container = new SnakeYamlNodeContainerImpl(Collections.emptyList());
        container.getOrCreateChildContainer("test", Collections::emptyList);
        ScalarNode boolNode = new ScalarNode(Tag.BOOL, "true", null, null, DumperOptions.ScalarStyle.PLAIN);

        // when
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> container.putNode("test", boolNode));

        // then
        assertThat(ex.getMessage(), equalTo("Container unexpectedly already contains entry for 'test'"));
    }

    @Test
    void shouldThrowIfPathDoesNotHaveContainer() {
        // given
        SnakeYamlNodeContainer container = new SnakeYamlNodeContainerImpl(Collections.emptyList());
        ScalarNode boolNode = new ScalarNode(Tag.BOOL, "true", null, null, DumperOptions.ScalarStyle.PLAIN);
        container.putNode("toast", boolNode);

        // when
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> container.getOrCreateChildContainer("toast", Collections::emptyList));

        // then
        assertThat(ex.getMessage(), equalTo("Unexpectedly found org.yaml.snakeyaml.nodes.ScalarNode in 'toast'"));
    }

    @Test
    void shouldReturnRootNode() {
        // given
        SnakeYamlNodeContainer container = new SnakeYamlNodeContainerImpl(Collections.emptyList());
        ScalarNode boolNode = new ScalarNode(Tag.BOOL, "true", null, null, DumperOptions.ScalarStyle.PLAIN);
        container.putNode("", boolNode);

        // when
        Node rootNode = container.getRootValueNode();

        // then
        assertThat(rootNode, sameInstance(boolNode));
    }
}
