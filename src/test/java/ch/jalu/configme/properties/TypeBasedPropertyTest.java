package ch.jalu.configme.properties;

import ch.jalu.configme.properties.types.NumberType;
import ch.jalu.configme.properties.types.PropertyType;
import ch.jalu.configme.properties.types.StringType;
import ch.jalu.configme.resource.PropertyReader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static ch.jalu.configme.TestUtils.isErrorValueOf;
import static ch.jalu.configme.TestUtils.isValidValueOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.sameInstance;
import static org.mockito.BDDMockito.given;

/**
 * Test for {@link TypeBasedProperty}.
 */
@ExtendWith(MockitoExtension.class)
class TypeBasedPropertyTest {

    @Mock
    private PropertyReader reader;

    @Test
    void shouldReturnValueFromResource() {
        // given
        Property<String> property = new TypeBasedProperty<>("common.path", StringType.STRING, "default");
        given(reader.getObject("common.path")).willReturn("some string");

        // when / then
        assertThat(property.determineValue(reader), isValidValueOf("some string"));
    }

    @Test
    void shouldReturnDefaultValue() {
        // given
        Property<String> property = new TypeBasedProperty<>("common.path", StringType.STRING, "default");

        // when / then
        assertThat(property.determineValue(reader), isErrorValueOf("default"));
    }

    @Test
    void shouldReturnValueAsExportValue() {
        // given
        Property<String> property = new TypeBasedProperty<>("common.path", StringType.STRING, "default");
        String given = "given string";

        // when / then
        assertThat(property.toExportValue(given), equalTo(given));
    }

    @Test
    void shouldReturnPropertyType() {
        // given
        TypeBasedProperty<Integer> property = new TypeBasedProperty<>("common.size", NumberType.INTEGER, 5);

        // when
        PropertyType<Integer> type = property.getType();

        // then
        assertThat(type, sameInstance(NumberType.INTEGER));
    }
}
