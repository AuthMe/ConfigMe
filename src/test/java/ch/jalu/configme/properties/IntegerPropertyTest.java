package ch.jalu.configme.properties;

import ch.jalu.configme.configurationdata.PropertyValue;
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
 * Test for {@link IntegerProperty}.
 */
class IntegerPropertyTest {

    private static PropertyReader reader;

    @BeforeAll
    static void setUpConfiguration() {
        reader = mock(PropertyReader.class);
        when(reader.getObject("int.path.test")).thenReturn(27);
        when(reader.getObject("int.path.wrong")).thenReturn(null);
    }

    @Test
    void shouldGetIntValue() {
        // given
        Property<Integer> property = new IntegerProperty("int.path.test", 3);

        // when
        PropertyValue<Integer> result = property.determineValue(reader);

        // then
        assertThat(result, isValidValueOf(27));
    }

    @Test
    void shouldGetIntDefault() {
        // given
        Property<Integer> property = new IntegerProperty("int.path.wrong", -10);

        // when
        PropertyValue<Integer> result = property.determineValue(reader);

        // then
        assertThat(result, isErrorValueOf(-10));
    }

    @Test
    void shouldReturnValueForExport() {
        // given
        Property<Integer> property = new IntegerProperty("some.path", -5);

        // when
        Object exportValue = property.toExportValue(45);

        // then
        assertThat(exportValue, equalTo(45));
    }
}
