package ch.jalu.configme.resource.yaml;

import ch.jalu.configme.TestUtils;
import ch.jalu.configme.configurationdata.ConfigurationData;
import ch.jalu.configme.properties.convertresult.ValueWithComments;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

/**
 * Test for {@link SnakeYamlNodeBuilderImpl}.
 */
@ExtendWith(MockitoExtension.class)
class SnakeYamlNodeBuilderImplTest {

    private final SnakeYamlNodeBuilderImpl nodeBuilder = new SnakeYamlNodeBuilderImpl();

    @Test
    void shouldCreateNodeForString() {
        // given
        String value = "Test";
        ConfigurationData configurationData = mock(ConfigurationData.class);
        String path = "title.txt";
        given(configurationData.getCommentsForSection(path)).willReturn(Collections.singletonList("Title text"));

        // when
        Node node = nodeBuilder.createYamlNode(value, path, configurationData, 2);

        // then
        assertThat(node, instanceOf(ScalarNode.class));
        ScalarNode scalarNode = (ScalarNode) node;
        assertThat(scalarNode.getTag(), equalTo(Tag.STR));
        assertThat(scalarNode.getValue(), equalTo(value));
        assertThat(scalarNode.getScalarStyle(), equalTo(DumperOptions.ScalarStyle.PLAIN));

        assertThat(scalarNode.getInLineComments(), nullValue());
        assertThat(scalarNode.getEndComments(), nullValue());
        assertThat(scalarNode.getBlockComments(), hasSize(3));
        assertThat(scalarNode.getBlockComments().get(0), isBlankComment());
        assertThat(scalarNode.getBlockComments().get(1), isBlankComment());
        assertThat(scalarNode.getBlockComments().get(2), isBlockComment(" Title text"));
    }

    @Test
    void shouldCreateStringNodeWithLiteralStyle() {
        // given
        String value = "Multi-line text\nMulti-line text\nMulti-line text";
        ConfigurationData configurationData = mock(ConfigurationData.class);
        String path = "disclaimer.text";
        given(configurationData.getCommentsForSection(path)).willReturn(Collections.emptyList());

        // when
        Node node = nodeBuilder.createYamlNode(value, path, configurationData, 0);

        // then
        assertThat(node, instanceOf(ScalarNode.class));
        ScalarNode scalarNode = (ScalarNode) node;
        assertThat(scalarNode.getTag(), equalTo(Tag.STR));
        assertThat(scalarNode.getValue(), equalTo(value));
        assertThat(scalarNode.getScalarStyle(), equalTo(DumperOptions.ScalarStyle.LITERAL));

        assertThat(scalarNode.getInLineComments(), nullValue());
        assertThat(scalarNode.getEndComments(), nullValue());
        assertThat(scalarNode.getBlockComments(), empty());
    }

    @Test
    void shouldCreateNodeForEnum() {
        // given
        TimeUnit value = TimeUnit.DAYS;
        ConfigurationData configurationData = mock(ConfigurationData.class);
        String path = "duration.unit";
        given(configurationData.getCommentsForSection(path)).willReturn(Arrays.asList("comment 1", "comment 2"));

        // when
        Node node = nodeBuilder.createYamlNode(value, path, configurationData, 0);

        // then
        assertThat(node, instanceOf(ScalarNode.class));
        ScalarNode scalarNode = (ScalarNode) node;
        assertThat(scalarNode.getTag(), equalTo(Tag.STR));
        assertThat(scalarNode.getValue(), equalTo("DAYS"));
        assertThat(scalarNode.getScalarStyle(), equalTo(DumperOptions.ScalarStyle.PLAIN));

        assertThat(scalarNode.getInLineComments(), nullValue());
        assertThat(scalarNode.getEndComments(), nullValue());
        assertThat(scalarNode.getBlockComments(), hasSize(2));
        assertThat(scalarNode.getBlockComments().get(0), isBlockComment(" comment 1"));
        assertThat(scalarNode.getBlockComments().get(1), isBlockComment(" comment 2"));
    }

    @Test
    void shouldCreateNodeForInt() {
        // given
        int value = 330;
        ConfigurationData configurationData = mock(ConfigurationData.class);
        String path = "title.size";
        given(configurationData.getCommentsForSection(path)).willReturn(Collections.emptyList());

        // when
        Node node = nodeBuilder.createYamlNode(value, path, configurationData, 0);

        // then
        assertThat(node, instanceOf(ScalarNode.class));
        ScalarNode scalarNode = (ScalarNode) node;
        assertThat(scalarNode.getTag(), equalTo(Tag.INT));
        assertThat(scalarNode.getValue(), equalTo(Integer.toString(value)));

        assertThat(scalarNode.getInLineComments(), nullValue());
        assertThat(scalarNode.getEndComments(), nullValue());
        assertThat(scalarNode.getBlockComments(), empty());
    }

    @Test
    void shouldCreateNodeForDouble() {
        // given
        double value = 3.14159;
        ConfigurationData configurationData = mock(ConfigurationData.class);
        String path = "constants.pi";
        given(configurationData.getCommentsForSection(path)).willReturn(Collections.emptyList());

        // when
        Node node = nodeBuilder.createYamlNode(value, path, configurationData, 1);

        // then
        assertThat(node, instanceOf(ScalarNode.class));
        ScalarNode scalarNode = (ScalarNode) node;
        assertThat(scalarNode.getTag(), equalTo(Tag.FLOAT));
        assertThat(scalarNode.getValue(), equalTo(Double.toString(value)));

        assertThat(scalarNode.getInLineComments(), nullValue());
        assertThat(scalarNode.getEndComments(), nullValue());
        assertThat(scalarNode.getBlockComments(), hasSize(1));
        assertThat(scalarNode.getBlockComments().get(0), isBlankComment());
    }

    @Test
    void shouldCreateNodeForBigDecimal() {
        // given
        BigDecimal value = new BigDecimal("3.141592653598");
        ConfigurationData configurationData = mock(ConfigurationData.class);
        String path = "constants.piPrecise";
        given(configurationData.getCommentsForSection(path)).willReturn(Collections.singletonList("Pi up to 12 digits"));

        // when
        Node node = nodeBuilder.createYamlNode(value, path, configurationData, 1);

        // then
        assertThat(node, instanceOf(ScalarNode.class));
        ScalarNode scalarNode = (ScalarNode) node;
        assertThat(scalarNode.getTag(), equalTo(Tag.FLOAT));
        assertThat(scalarNode.getValue(), equalTo("3.141592653598"));

        assertThat(scalarNode.getInLineComments(), nullValue());
        assertThat(scalarNode.getEndComments(), nullValue());
        assertThat(scalarNode.getBlockComments(), hasSize(2));
        assertThat(scalarNode.getBlockComments().get(0), isBlankComment());
        assertThat(scalarNode.getBlockComments().get(1), isBlockComment(" Pi up to 12 digits"));
    }

    @Test
    void shouldCreateNodeForBoolean() {
        // given
        Object value = true;
        ConfigurationData configurationData = mock(ConfigurationData.class);
        String path = "output.debug";
        given(configurationData.getCommentsForSection(path)).willReturn(Collections.emptyList());

        // when
        Node node = nodeBuilder.createYamlNode(value, path, configurationData, 0);

        // then
        assertThat(node, instanceOf(ScalarNode.class));
        ScalarNode scalarNode = (ScalarNode) node;
        assertThat(scalarNode.getTag(), equalTo(Tag.BOOL));
        assertThat(scalarNode.getValue(), equalTo("true"));

        assertThat(scalarNode.getInLineComments(), nullValue());
        assertThat(scalarNode.getEndComments(), nullValue());
        assertThat(scalarNode.getBlockComments(), empty());
    }

    @Test
    void shouldCreateNodeForList() {
        // given
        Object value = Arrays.asList(3, 4.5);
        ConfigurationData configurationData = mock(ConfigurationData.class);
        String path = "calc.coefficients";
        given(configurationData.getCommentsForSection(path)).willReturn(Collections.singletonList("Coefficients"));
        given(configurationData.getCommentsForSection(path + "[0]")).willReturn(Collections.emptyList());
        given(configurationData.getCommentsForSection(path + "[1]")).willReturn(Collections.singletonList("\n"));

        // when
        Node node = nodeBuilder.createYamlNode(value, path, configurationData, 1);

        // then
        verifyNoMoreInteractions(configurationData);

        assertThat(node, instanceOf(SequenceNode.class));
        SequenceNode sequenceNode = (SequenceNode) node;
        assertThat(sequenceNode.getTag(), equalTo(Tag.SEQ));

        assertThat(sequenceNode.getInLineComments(), nullValue());
        assertThat(sequenceNode.getEndComments(), nullValue());
        assertThat(sequenceNode.getBlockComments(), hasSize(2));
        assertThat(sequenceNode.getBlockComments().get(0), isBlankComment());
        assertThat(sequenceNode.getBlockComments().get(1), isBlockComment(" Coefficients"));

        List<Node> values = sequenceNode.getValue();
        assertThat(values, hasSize(2));

        assertThat(values.get(0), instanceOf(ScalarNode.class));
        assertThat(values.get(0).getTag(), equalTo(Tag.INT));
        assertThat(((ScalarNode) values.get(0)).getValue(), equalTo("3"));
        assertThat(values.get(0).getBlockComments(), empty());

        assertThat(values.get(1), instanceOf(ScalarNode.class));
        assertThat(values.get(1).getTag(), equalTo(Tag.FLOAT));
        assertThat(((ScalarNode) values.get(1)).getValue(), equalTo("4.5"));
        assertThat(values.get(1).getBlockComments(), hasSize(1));
        assertThat(values.get(1).getBlockComments().get(0), isBlankComment());
    }

    @Test
    void shouldCreateNodeForArray() {
        // given
        Boolean[] value = {true, false, true};
        ConfigurationData configurationData = mock(ConfigurationData.class);
        String path = "calc.flags";

        // when
        Node result = nodeBuilder.createYamlNode(value, path, configurationData, 0);

        // then
        verify(configurationData).getCommentsForSection(path);
        verify(configurationData).getCommentsForSection(path + "[0]");
        verify(configurationData).getCommentsForSection(path + "[1]");
        verify(configurationData).getCommentsForSection(path + "[2]");
        verifyNoMoreInteractions(configurationData);

        assertThat(result, instanceOf(SequenceNode.class));
        SequenceNode sequenceNode = (SequenceNode) result;
        assertThat(sequenceNode.getTag(), equalTo(Tag.SEQ));

        assertThat(sequenceNode.getInLineComments(), nullValue());
        assertThat(sequenceNode.getEndComments(), nullValue());
        assertThat(sequenceNode.getBlockComments(), empty());

        List<Node> nodes = sequenceNode.getValue();
        assertThat(nodes, hasSize(3));
        nodes.forEach(node -> {
            assertThat(node.getBlockComments(), empty());
        });

        assertThat(nodes.get(0), isScalarNode(Tag.BOOL, "true"));
        assertThat(nodes.get(1), isScalarNode(Tag.BOOL, "false"));
        assertThat(nodes.get(2), isScalarNode(Tag.BOOL, "true"));
    }

    @Test
    void shouldCreateNodeForMap() {
        // given
        Map<String, Integer> factors = new LinkedHashMap<>();
        factors.put("S", 6);
        factors.put("A", 3);
        factors.put("C", 2);
        ConfigurationData configurationData = mock(ConfigurationData.class);
        String path = "calc.factors";

        given(configurationData.getCommentsForSection(path)).willReturn(Collections.singletonList("\n"));
        given(configurationData.getCommentsForSection(path + "[k=S]")).willReturn(Collections.emptyList());
        given(configurationData.getCommentsForSection(path + "[k=A]")).willReturn(Collections.singletonList("Alpha comp."));
        given(configurationData.getCommentsForSection(path + "[k=C]")).willReturn(Collections.emptyList());

        // when
        Node result = nodeBuilder.createYamlNode(factors, path, configurationData, 0);

        // then
        verifyNoMoreInteractions(configurationData);

        assertThat(result, instanceOf(MappingNode.class));
        MappingNode mapNode = (MappingNode) result;
        assertThat(mapNode.getTag(), equalTo(Tag.MAP));

        assertThat(mapNode.getInLineComments(), nullValue());
        assertThat(mapNode.getEndComments(), nullValue());
        assertThat(mapNode.getBlockComments(), hasSize(1));
        assertThat(mapNode.getBlockComments().get(0), isBlankComment());

        List<NodeTuple> nodeTuples = mapNode.getValue();
        assertThat(nodeTuples, hasSize(3));

        assertThat(nodeTuples.get(0).getKeyNode(), isScalarNode(Tag.STR, "S"));
        assertThat(nodeTuples.get(0).getValueNode(), isScalarNode(Tag.INT, "6"));
        assertThat(nodeTuples.get(1).getKeyNode(), isScalarNode(Tag.STR, "A"));
        assertThat(nodeTuples.get(1).getValueNode(), isScalarNode(Tag.INT, "3"));
        assertThat(nodeTuples.get(2).getKeyNode(), isScalarNode(Tag.STR, "C"));
        assertThat(nodeTuples.get(2).getValueNode(), isScalarNode(Tag.INT, "2"));

        assertThat(nodeTuples.get(0).getKeyNode().getBlockComments(), nullValue());
        assertThat(nodeTuples.get(0).getValueNode().getBlockComments(), empty());
        assertThat(nodeTuples.get(1).getKeyNode().getBlockComments(), hasSize(1));
        assertThat(nodeTuples.get(1).getKeyNode().getBlockComments().get(0), isBlockComment(" Alpha comp."));
        assertThat(nodeTuples.get(1).getValueNode().getBlockComments(), empty());
        assertThat(nodeTuples.get(2).getKeyNode().getBlockComments(), nullValue());
        assertThat(nodeTuples.get(2).getValueNode().getBlockComments(), empty());
    }

    @Test
    void shouldHandleEmptyMap() {
        // given
        Map<String, Integer> factors = new LinkedHashMap<>();
        ConfigurationData configurationData = mock(ConfigurationData.class);
        String path = "calc.factors";

        given(configurationData.getCommentsForSection(path)).willReturn(Collections.singletonList("Overridden factors"));

        // when
        Node result = nodeBuilder.createYamlNode(factors, path, configurationData, 0);

        // then
        verify(configurationData, only()).getCommentsForSection(path);

        assertThat(result, instanceOf(MappingNode.class));
        MappingNode mapNode = (MappingNode) result;
        assertThat(mapNode.getTag(), equalTo(Tag.MAP));
        assertThat(mapNode.getValue(), empty());

        assertThat(mapNode.getInLineComments(), nullValue());
        assertThat(mapNode.getEndComments(), nullValue());
        assertThat(mapNode.getBlockComments(), hasSize(1));
        assertThat(mapNode.getBlockComments().get(0), isBlockComment(" Overridden factors"));
    }

    @Test
    void shouldHandleEmptyArray() {
        // given
        Object value = new Object[0];
        ConfigurationData configurationData = mock(ConfigurationData.class);
        String path = "calc.specialRules";

        given(configurationData.getCommentsForSection(path)).willReturn(Collections.singletonList("Overridden factors"));

        // when
        Node result = nodeBuilder.createYamlNode(new ValueWithComments(value, Arrays.asList("R1", "R2")), path,
            configurationData, 1);

        // then
        verify(configurationData, only()).getCommentsForSection(path);

        assertThat(result, instanceOf(SequenceNode.class));
        SequenceNode sequenceNode = (SequenceNode) result;
        assertThat(sequenceNode.getTag(), equalTo(Tag.SEQ));
        assertThat(sequenceNode.getValue(), empty());

        assertThat(sequenceNode.getInLineComments(), nullValue());
        assertThat(sequenceNode.getEndComments(), nullValue());
        assertThat(sequenceNode.getBlockComments(), hasSize(4));
        assertThat(sequenceNode.getBlockComments().get(0), isBlankComment());
        assertThat(sequenceNode.getBlockComments().get(1), isBlockComment(" Overridden factors"));
        assertThat(sequenceNode.getBlockComments().get(2), isBlockComment(" R1"));
        assertThat(sequenceNode.getBlockComments().get(3), isBlockComment(" R2"));
    }

    @Test
    void shouldThrowForUnknownValueTypes() {
        // given
        ConfigurationData configurationData = mock(ConfigurationData.class);

        // when
        IllegalArgumentException ex1 = assertThrows(IllegalArgumentException.class,
            () -> nodeBuilder.createYamlNode(Optional.of(3), "", configurationData, 3));
        IllegalArgumentException ex2 = assertThrows(IllegalArgumentException.class,
            () -> nodeBuilder.createYamlNode(null, "", configurationData, 3));

        // then
        assertThat(ex1.getMessage(), equalTo("Unsupported value of type: java.util.Optional"));
        if (!TestUtils.hasBytecodeCheckForNotNullAnnotation()) {
            assertThat(ex2.getMessage(), equalTo("Unsupported value of type: null"));
        }
    }

    @Test
    void shouldCreateCommentNodes() {
        // given / when
        List<CommentLine> nodeForEmptyString = nodeBuilder.createCommentLines("").collect(Collectors.toList());
        List<CommentLine> nodeForNewLine = nodeBuilder.createCommentLines("\n").collect(Collectors.toList());
        List<CommentLine> nodeForText = nodeBuilder.createCommentLines("Text").collect(Collectors.toList());

        // then
        assertThat(nodeForEmptyString, contains(isBlockComment(" ")));
        assertThat(nodeForNewLine, contains(isBlankComment()));
        assertThat(nodeForText, contains(isBlockComment(" Text")));
    }

    @Test
    void shouldCreateCommentNodesForTextWithNewLines() {
        // given
        String text = "Alpha\nBravo\n\nCharlie\n";

        // when
        List<CommentLine> commentLines = nodeBuilder.createCommentLines(text).collect(Collectors.toList());

        // then
        assertThat(commentLines, hasSize(5));
        assertThat(commentLines.get(0), isBlockComment(" Alpha"));
        assertThat(commentLines.get(1), isBlockComment(" Bravo"));
        assertThat(commentLines.get(2), isBlockComment(" "));
        assertThat(commentLines.get(3), isBlockComment(" Charlie"));
        assertThat(commentLines.get(4), isBlockComment(" "));
    }

    @Test
    void shouldTransferCommentsFromValueToKey() {
        // given
        Node keyNode = new ScalarNode(Tag.STR, "key", null, null, DumperOptions.ScalarStyle.PLAIN);
        Node valueNode = new ScalarNode(Tag.INT, "34", null, null, DumperOptions.ScalarStyle.PLAIN);
        valueNode.setBlockComments(new ArrayList<>());
        CommentLine commentLine = new CommentLine(null, null, "Test", CommentType.BLOCK);
        valueNode.getBlockComments().add(commentLine);

        // when
        nodeBuilder.transferComments(valueNode, keyNode);

        // then
        assertThat(keyNode.getBlockComments(), contains(commentLine));
        assertThat(valueNode.getBlockComments(), empty());
    }

    @Test
    void shouldCombineComments() {
        // given
        ValueWithComments valueWithComments = new ValueWithComments("3", Arrays.asList("VWC1", "VWC2"));
        String path = "some.path";
        ConfigurationData configurationData = mock(ConfigurationData.class);
        given(configurationData.getCommentsForSection(path)).willReturn(Arrays.asList("CD1", "CD2"));

        // when
        List<CommentLine> comments = nodeBuilder.collectComments(valueWithComments, path, configurationData, 1);

        // then
        assertThat(comments, hasSize(5));
        assertThat(comments.get(0), isBlankComment());
        assertThat(comments.get(1), isBlockComment(" CD1"));
        assertThat(comments.get(2), isBlockComment(" CD2"));
        assertThat(comments.get(3), isBlockComment(" VWC1"));
        assertThat(comments.get(4), isBlockComment(" VWC2"));
    }

    @Test
    void shouldCombineCommentsWherePresent() {
        // given
        Object value = true;
        String path = "some.path";
        ConfigurationData configurationData = mock(ConfigurationData.class);
        given(configurationData.getCommentsForSection(path)).willReturn(Arrays.asList("CD1", "\n", "CD2"));

        // when
        List<CommentLine> comments = nodeBuilder.collectComments(value, path, configurationData, 1);

        // then
        assertThat(comments, hasSize(4));
        assertThat(comments.get(0), isBlankComment());
        assertThat(comments.get(1), isBlockComment(" CD1"));
        assertThat(comments.get(2), isBlankComment());
        assertThat(comments.get(3), isBlockComment(" CD2"));
    }

    @Test
    void shouldReturnCollectionOfUsedUniqueIds() {
        // given
        UUID uniqueCommentId = UUID.fromString("0000-00-00-00-001");
        Object value = new ValueWithComments(true, Arrays.asList("com", "com"), uniqueCommentId);
        ConfigurationData configurationData = mock(ConfigurationData.class);
        nodeBuilder.createYamlNode(value, "some.path", configurationData, 0);

        // when
        Set<UUID> usedCommentIds = nodeBuilder.getUsedUniqueCommentIds();

        // then
        assertThat(usedCommentIds, contains(uniqueCommentId));
    }

    static Matcher<Node> isScalarNode(Tag expectedTag, String expectedValue) {
        return new TypeSafeMatcher<Node>() {
            @Override
            protected boolean matchesSafely(Node node) {
                return node instanceof ScalarNode
                    && node.getTag() == expectedTag
                    && expectedValue.equals(((ScalarNode) node).getValue());
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("ScalarNode with Tag." + expectedTag + " and value '" + expectedValue + "'");
            }

            @Override
            protected void describeMismatchSafely(Node item, Description mismatchDescription) {
                if (item instanceof ScalarNode) {
                    mismatchDescription.appendText("ScalarNode with Tag." + item.getTag() + " and value '"
                        + ((ScalarNode) item).getValue() + "'");
                } else {
                    mismatchDescription.appendText("Node of type '" + item.getClass() + "'");
                }
            }
        };
    }

    static Matcher<CommentLine> isBlockComment(String expectedComment) {
        return new TypeSafeMatcher<CommentLine>() {
            @Override
            protected boolean matchesSafely(CommentLine commentLine) {
                return commentLine.getCommentType() == CommentType.BLOCK
                    && expectedComment.equals(commentLine.getValue());
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("Comment line type=BLOCK with value=" + expectedComment);
            }
        };
    }

    static Matcher<CommentLine> isBlankComment() {
        return new TypeSafeMatcher<CommentLine>() {
            @Override
            protected boolean matchesSafely(CommentLine commentLine) {
                return commentLine.getCommentType() == CommentType.BLANK_LINE
                    && "".equals(commentLine.getValue());
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("Comment line type=BLOCK with value=''");
            }
        };
    }
}
