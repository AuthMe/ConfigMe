package ch.jalu.configme.configurationdata;

import ch.jalu.configme.properties.Property;
import ch.jalu.configme.properties.StringProperty;
import org.junit.Test;

import static ch.jalu.configme.TestUtils.isErrorValueOf;
import static ch.jalu.configme.TestUtils.isValidValueOf;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * Test for {@link PropertyValue}.
 */
public class PropertyValueTest {

    @Test
    public void shouldCreateValidInstance() {
        // given / when
        PropertyValue<String> value = PropertyValue.withValidValue("test");

        // then
        assertThat(value.getValue(), equalTo("test"));
        assertThat(value.isValidInResource(), equalTo(true));
    }

    @Test
    public void shouldCreateInvalidInstance() {
        // given / when
        PropertyValue<Integer> value = PropertyValue.withValueRequiringRewrite(33);

        // then
        assertThat(value.getValue(), equalTo(33));
        assertThat(value.isValidInResource(), equalTo(false));
    }

    @Test
    public void shouldCreateValidValueAsDefinedByProperty() {
        // given
        Property<String> property = new Min10LengthStringProperty();
        String givenValue = "1234567890test";

        // when
        PropertyValue<String> propertyValue = PropertyValue.defaultIfInvalid(givenValue, property);

        // then
        assertThat(propertyValue, isValidValueOf(givenValue));
    }

    @Test
    public void shouldCreateInvalidValueAsDefinedByProperty() {
        // given
        Property<String> property = new Min10LengthStringProperty();

        // when
        PropertyValue<String> propertyValue = PropertyValue.defaultIfInvalid("abcd", property);

        // then
        assertThat(propertyValue, isErrorValueOf(property.getDefaultValue()));
    }

    @Test
    public void shouldIncludeValuesInToString() {
        // given
        PropertyValue<Double> value = PropertyValue.withValidValue(-3.254);

        // when
        String stringRepresentation = value.toString();

        // then
        assertThat(stringRepresentation, equalTo("PropertyValue[valid=true, value='-3.254']"));
    }

    private static final class Min10LengthStringProperty extends StringProperty {

        public Min10LengthStringProperty() {
            super("path.irrelevant", "my default value");
        }

        @Override
        public boolean isValidValue(String value) {
            return super.isValidValue(value) && value.length() >= 10;
        }
    }
}
