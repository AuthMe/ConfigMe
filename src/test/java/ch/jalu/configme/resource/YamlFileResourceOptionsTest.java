package ch.jalu.configme.resource;

import ch.jalu.configme.resource.PropertyPathTraverser.PathElement;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.function.ToIntFunction;

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
            .indentationSize(2)
            .build();

        // then
        assertThat(options.getCharset(), equalTo(StandardCharsets.UTF_16BE));
        assertThat(options.getIndentFunction(), equalTo(lineFunction));
        PathElement pathElement = new PathElement(3, "test", "test", false);
        assertThat(options.getNumberOfEmptyLinesBefore(pathElement), equalTo(3));
        assertThat(options.getIndentationSize(), equalTo(2));
    }

    @Test
    void shouldCreateOptionsWithDefaults() {
        // given / when
        YamlFileResourceOptions options = YamlFileResourceOptions.builder().build();

        // then
        assertThat(options.getCharset(), equalTo(StandardCharsets.UTF_8));
        assertThat(options.getIndentFunction(), nullValue());
        assertThat(options.getIndentationSize(), equalTo(4));
        PathElement pathElement = new PathElement(3, "test", "test", false);
        assertThat(options.getNumberOfEmptyLinesBefore(pathElement), equalTo(0));
    }
}
