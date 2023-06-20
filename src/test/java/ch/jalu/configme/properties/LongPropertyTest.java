package ch.jalu.configme.properties;

import ch.jalu.configme.properties.convertresult.PropertyValue;
import ch.jalu.configme.resource.PropertyReader;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static ch.jalu.configme.TestUtils.isErrorValueOf;
import static ch.jalu.configme.TestUtils.isValidValueOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test for {@link LongProperty}.
 */
class LongPropertyTest {

    private static PropertyReader reader;

    @BeforeAll
    static void setUpConfiguration() {
        reader = mock(PropertyReader.class);
        when(reader.getObject("long.path.test")).thenReturn(30L);
        when(reader.getObject("long.path.wrong")).thenReturn(null);
    }

    @Test
    void shouldGetLongValue() {
        // given
        Property<Long> property = new LongProperty("long.path.test", 5L);

        // when
        PropertyValue<Long> result = property.determineValue(reader);

        // then
        assertThat(result, isValidValueOf(30L));
    }

    @Test
    void shouldGetLongDefault() {
        // given
        Property<Long> property = new LongProperty("long.path.wrong", -10L);

        // when
        PropertyValue<Long> result = property.determineValue(reader);

        // then
        assertThat(result, isErrorValueOf(-10L));
    }

    @Test
    void shouldReturnValueForExport() {
        // given
        Property<Long> property = new LongProperty("some.path", -10L);

        // when
        Object exportValue = property.toExportValue(50L);

        // then
        assertThat(exportValue, equalTo(50L));
    }
}
