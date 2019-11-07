package ch.jalu.configme.resource;

import ch.jalu.configme.resource.PropertyPathTraverser.PathElement;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.function.ToIntFunction;

import static java.util.Collections.emptyList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;

/**
 * Test for {@link YamlFileResourceOptions}.
 */
class YamlFileResourceOptionsTest {

    @Test
    void shouldKeepConfiguredValues() {
        // given
        ToIntFunction<PathElement> lineFunction = PathElement::getIndentationLevel;

        // when
        YamlFileResourceOptions options = YamlFileResourceOptions.builder()
            .numberOfLinesBeforeFunction(lineFunction)
            .charset(StandardCharsets.UTF_16BE)
            .build();

        // then
        assertThat(options.getCharset(), equalTo(StandardCharsets.UTF_16BE));
        assertThat(options.getIndentFunction(), equalTo(lineFunction));
        PathElement pathElement = new PathElement(3, "test", emptyList(), false);
        assertThat(options.getNumberOfEmptyLinesBefore(pathElement), equalTo(3));
    }

    @Test
    void shouldCreateOptionsWithDefaults() {
        // given / when
        YamlFileResourceOptions options = YamlFileResourceOptions.builder().build();

        // then
        assertThat(options.getCharset(), equalTo(StandardCharsets.UTF_8));
        assertThat(options.getIndentFunction(), nullValue());
        PathElement pathElement = new PathElement(3, "test", emptyList(), false);
        assertThat(options.getNumberOfEmptyLinesBefore(pathElement), equalTo(0));
    }
}
