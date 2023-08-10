package ch.jalu.configme.resource;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

/**
 * Test for {@link PropertyPathTraverser}.
 */
class PropertyPathTraverserTest {

    private final PropertyPathTraverser propertyPathTraverser = new PropertyPathTraverser();

    @Test
    void shouldReturnPathElements() {
        // given / when
        List<PropertyPathTraverser.PathElement> result = propertyPathTraverser.getPathElements("prop.test");

        // then
        assertThat(result, hasSize(2));

        assertThat(result.get(0).getName(), equalTo("prop"));
        assertThat(result.get(0).getFullPath(), equalTo("prop"));
        assertThat(result.get(0).getIndentationLevel(), equalTo(0));
        assertThat(result.get(0).isFirstElement(), equalTo(true));
        assertThat(result.get(0).isFirstOfGroup(), equalTo(true));
        assertThat(result.get(0).isEndOfPath(), equalTo(false));

        assertThat(result.get(1).getName(), equalTo("test"));
        assertThat(result.get(1).getFullPath(), equalTo("prop.test"));
        assertThat(result.get(1).getIndentationLevel(), equalTo(1));
        assertThat(result.get(1).isFirstElement(), equalTo(false));
        assertThat(result.get(1).isFirstOfGroup(), equalTo(false));
        assertThat(result.get(1).isEndOfPath(), equalTo(true));
    }

    @Test
    void shouldCombineRootCommentWithThatOfParent() {
        // given / when
        List<PropertyPathTraverser.PathElement> result = propertyPathTraverser.getPathElements("prop.test");

        // then
        assertThat(result, hasSize(2));

        assertThat(result.get(0).getName(), equalTo("prop"));
        assertThat(result.get(0).getFullPath(), equalTo("prop"));
        assertThat(result.get(0).getIndentationLevel(), equalTo(0));
        assertThat(result.get(0).isFirstElement(), equalTo(true));
        assertThat(result.get(0).isFirstOfGroup(), equalTo(true));
        assertThat(result.get(0).isEndOfPath(), equalTo(false));

        assertThat(result.get(1).getName(), equalTo("test"));
        assertThat(result.get(1).getFullPath(), equalTo("prop.test"));
        assertThat(result.get(1).getIndentationLevel(), equalTo(1));
        assertThat(result.get(1).isFirstElement(), equalTo(false));
        assertThat(result.get(1).isFirstOfGroup(), equalTo(false));
        assertThat(result.get(1).isEndOfPath(), equalTo(true));
    }

    @Test
    void shouldHandleEmptyStringPath() {
        // given / when
        List<PropertyPathTraverser.PathElement> result = propertyPathTraverser.getPathElements("");

        // then
        assertThat(result, hasSize(1));
        assertThat(result.get(0).getName(), equalTo(""));
        assertThat(result.get(0).getFullPath(), equalTo(""));
        assertThat(result.get(0).getIndentationLevel(), equalTo(0));
        assertThat(result.get(0).isFirstElement(), equalTo(true));
        assertThat(result.get(0).isFirstOfGroup(), equalTo(true));
        assertThat(result.get(0).isEndOfPath(), equalTo(true));
    }

    @Test
    void shouldReturnAllElements() {
        // given
        propertyPathTraverser.getPathElements("some.longer.test");

        // when
        List<PropertyPathTraverser.PathElement> result = propertyPathTraverser.getPathElements("some.longer.path.value");

        // then
        assertThat(result, hasSize(4));

        assertThat(result.get(0).getName(), equalTo("some"));
        assertThat(result.get(0).getFullPath(), equalTo("some"));
        assertThat(result.get(0).getIndentationLevel(), equalTo(0));
        assertThat(result.get(0).isFirstElement(), equalTo(false)); // because "some.longer.test" was already visited
        assertThat(result.get(0).isFirstOfGroup(), equalTo(false));
        assertThat(result.get(0).isEndOfPath(), equalTo(false));

        assertThat(result.get(1).getName(), equalTo("longer"));
        assertThat(result.get(1).getFullPath(), equalTo("some.longer"));
        assertThat(result.get(1).getIndentationLevel(), equalTo(1));
        assertThat(result.get(1).isFirstElement(), equalTo(false));
        assertThat(result.get(1).isFirstOfGroup(), equalTo(false));
        assertThat(result.get(1).isEndOfPath(), equalTo(false));

        assertThat(result.get(2).getName(), equalTo("path"));
        assertThat(result.get(2).getFullPath(), equalTo("some.longer.path"));
        assertThat(result.get(2).getIndentationLevel(), equalTo(2));
        assertThat(result.get(2).isFirstElement(), equalTo(false));
        assertThat(result.get(2).isFirstOfGroup(), equalTo(true));
        assertThat(result.get(2).isEndOfPath(), equalTo(false));

        assertThat(result.get(3).getName(), equalTo("value"));
        assertThat(result.get(3).getFullPath(), equalTo("some.longer.path.value"));
        assertThat(result.get(3).getIndentationLevel(), equalTo(3));
        assertThat(result.get(3).isFirstElement(), equalTo(false));
        assertThat(result.get(3).isFirstOfGroup(), equalTo(false));
        assertThat(result.get(3).isEndOfPath(), equalTo(true));
    }
}
