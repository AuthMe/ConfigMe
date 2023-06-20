package ch.jalu.configme.properties;

import ch.jalu.configme.properties.convertresult.PropertyValue;
import ch.jalu.configme.resource.PropertyReader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static ch.jalu.configme.TestUtils.isErrorValueOf;
import static ch.jalu.configme.TestUtils.isValidValueOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.BDDMockito.given;

/**
 * Test for {@link FloatProperty}.
 */
@ExtendWith(MockitoExtension.class)
class FloatPropertyTest {

    @Mock
    private PropertyReader reader;

    @Test
    void shouldReturnFloatFromResource() {
        // given
        Property<Float> property = new FloatProperty("test.path", -4.11f);
        given(reader.getObject("test.path")).willReturn(-2508.346);

        // when
        PropertyValue<Float> result = property.determineValue(reader);

        // then
        assertThat(result, isValidValueOf(-2508.346f));
    }

    @Test
    void shouldReturnDefaultValue() {
        // given
        Property<Float> property = new FloatProperty("property.path", 140f);
        given(reader.getObject("property.path")).willReturn(null);

        // when
        PropertyValue<Float> result = property.determineValue(reader);

        // then
        assertThat(result, isErrorValueOf(140f));
    }

    @Test
    void shouldReturnValueAsExportValue() {
        // given
        Property<Float> property = new FloatProperty("property.path", 44f);
        float givenValue = 0.25f;

        // when
        Object exportValue = property.toExportValue(givenValue);

        // then
        assertThat(exportValue, equalTo(givenValue));
    }
}
