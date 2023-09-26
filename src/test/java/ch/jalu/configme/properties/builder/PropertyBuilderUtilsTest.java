package ch.jalu.configme.properties.builder;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Test for {@link PropertyBuilderUtils}.
 */
class PropertyBuilderUtilsTest {

    @Test
    void shouldThrowForNullPath() {
        // given / when
        IllegalStateException ex = assertThrows(IllegalStateException.class,
            () -> PropertyBuilderUtils.requireNonNullPath(null));

        // then
        assertThat(ex.getMessage(), equalTo("The path of the property must be defined"));
    }

    @Test
    void shouldAcceptNonNullPath() {
        // given
        String path = "test";

        // when
        PropertyBuilderUtils.requireNonNullPath(path);

        // then - no exception
    }

    @Test
    void shouldNotThrowIfDefaultValueIsEmpty() {
        // given / when
        PropertyBuilderUtils.verifyDefaultValueIsEmpty(true);

        // then - no exception
    }

    @Test
    void shouldThrowIfDefaultValueIsNotEmpty() {
        // given / when
        IllegalStateException ex = assertThrows(IllegalStateException.class,
            () -> PropertyBuilderUtils.verifyDefaultValueIsEmpty(false));

        // then
        assertThat(ex.getMessage(), equalTo("Default values have already been defined! Use addToDefaultValue to add entries individually"));
    }
}
