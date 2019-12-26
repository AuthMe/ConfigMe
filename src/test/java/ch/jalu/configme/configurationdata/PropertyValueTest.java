package ch.jalu.configme.configurationdata;

import ch.jalu.configme.properties.convertresult.PropertyValue;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

/**
 * Test for {@link PropertyValue}.
 */
class PropertyValueTest {

    @Test
    void shouldCreateValidInstance() {
        // given / when
        PropertyValue<String> value = PropertyValue.withValidValue("test");

        // then
        assertThat(value.getValue(), equalTo("test"));
        assertThat(value.isValidInResource(), equalTo(true));
    }

    @Test
    void shouldCreateInvalidInstance() {
        // given / when
        PropertyValue<Integer> value = PropertyValue.withValueRequiringRewrite(33);

        // then
        assertThat(value.getValue(), equalTo(33));
        assertThat(value.isValidInResource(), equalTo(false));
    }

    @Test
    void shouldIncludeValuesInToString() {
        // given
        PropertyValue<Double> value = PropertyValue.withValidValue(-3.254);

        // when
        String stringRepresentation = value.toString();

        // then
        assertThat(stringRepresentation, equalTo("PropertyValue[valid=true, value='-3.254']"));
    }
}
