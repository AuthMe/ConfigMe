package ch.jalu.configme.resource;

import ch.jalu.configme.configurationdata.ConfigurationData;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

/**
 * Test for {@link PropertyPathTraverser}.
 */
public class PropertyPathTraverserTest {

    private ConfigurationData configurationData = mock(ConfigurationData.class);

    private PropertyPathTraverser propertyPathTraverser = new PropertyPathTraverser(configurationData);

    @Test
    public void shouldReturnPathElements() {
        // given
        given(configurationData.getCommentsForSection("")).willReturn(Collections.singletonList("root comment"));
        given(configurationData.getCommentsForSection("prop.test")).willReturn(Collections.singletonList("prop.test comment"));

        // when
        List<PropertyPathTraverser.PathElement> result = propertyPathTraverser.getPathElements("prop.test");

        // then
        assertThat(result, hasSize(2));
        assertThat(result.get(0).name, equalTo("prop"));
        assertThat(result.get(0).comments, contains("root comment"));
        assertThat(result.get(0).indentationLevel, equalTo(0));
        assertThat(result.get(1).name, equalTo("test"));
        assertThat(result.get(1).comments, contains("prop.test comment"));
        assertThat(result.get(1).indentationLevel, equalTo(1));
    }

    @Test
    public void shouldCombineRootCommentWithThatOfParent() {
        // given
        given(configurationData.getCommentsForSection("")).willReturn(Arrays.asList("root1", "root2"));
        given(configurationData.getCommentsForSection("prop")).willReturn(Collections.singletonList("prop 1"));

        // when
        List<PropertyPathTraverser.PathElement> result = propertyPathTraverser.getPathElements("prop.test");

        // then
        assertThat(result, hasSize(2));
        assertThat(result.get(0).name, equalTo("prop"));
        assertThat(result.get(0).comments, contains("root1", "root2", "prop 1"));
        assertThat(result.get(0).indentationLevel, equalTo(0));
        assertThat(result.get(1).name, equalTo("test"));
        assertThat(result.get(1).comments, empty());
        assertThat(result.get(1).indentationLevel, equalTo(1));
    }

    @Test
    public void shouldHandleEmptyStringPath() {
        // given
        given(configurationData.getCommentsForSection("")).willReturn(Arrays.asList("c1", "d2", "e3"));

        // when
        List<PropertyPathTraverser.PathElement> result = propertyPathTraverser.getPathElements("");

        // then
        assertThat(result, hasSize(1));
        assertThat(result.get(0).name, equalTo(""));
        assertThat(result.get(0).comments, contains("c1", "d2", "e3"));
        assertThat(result.get(0).indentationLevel, equalTo(0));
    }

    @Test
    public void shouldReturnOnlyNewElements() {
        // given
        given(configurationData.getCommentsForSection("some.longer.path")).willReturn(Collections.singletonList("The comment"));
        propertyPathTraverser.getPathElements("some.longer.test");

        // when
        List<PropertyPathTraverser.PathElement> result = propertyPathTraverser.getPathElements("some.longer.path.value");

        // then
        assertThat(result, hasSize(2));
        assertThat(result.get(0).name, equalTo("path"));
        assertThat(result.get(0).comments, contains("The comment"));
        assertThat(result.get(0).indentationLevel, equalTo(2));
        assertThat(result.get(1).name, equalTo("value"));
        assertThat(result.get(1).comments, empty());
        assertThat(result.get(1).indentationLevel, equalTo(3));
    }
}
