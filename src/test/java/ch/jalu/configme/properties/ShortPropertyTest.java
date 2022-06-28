package ch.jalu.configme.properties;

import static ch.jalu.configme.TestUtils.isErrorValueOf;
import static ch.jalu.configme.TestUtils.isValidValueOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import ch.jalu.configme.properties.convertresult.PropertyValue;
import ch.jalu.configme.resource.PropertyReader;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Test for {@link ShortProperty}.
 */
class ShortPropertyTest {

    private static PropertyReader reader;

    @BeforeAll
    static void setUpConfiguration() {
        reader = mock(PropertyReader.class);
        when(reader.getObject("short.path.test")).thenReturn((short) 15);
        when(reader.getObject("short.path.wrong")).thenReturn(null);
    }

    @Test
    void shouldGetLongValue() {
        // given
        Property<Short> property = new ShortProperty("short.path.test", (short) 10);

        // when
        PropertyValue<Short> result = property.determineValue(reader);

        // then
        assertThat(result, isValidValueOf((short) 15));
    }

    @Test
    void shouldGetLongDefault() {
        // given
        Property<Short> property = new ShortProperty("short.path.wrong", (short) -5);

        // when
        PropertyValue<Short> result = property.determineValue(reader);

        // then
        assertThat(result, isErrorValueOf((short) -5));
    }

    @Test
    void shouldReturnValueForExport() {
        // given
        Property<Short> property = new ShortProperty("some.path", (short) -3);

        // when
        Object exportValue = property.toExportValue((short) 25);

        // then
        assertThat(exportValue, equalTo((short) 25));
    }
}
