package ch.jalu.configme.properties;

import ch.jalu.configme.properties.types.PrimitivePropertyType;
import ch.jalu.configme.resource.PropertyReader;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;

/**
 * Test for {@link CommonProperty}.
 */
@RunWith(MockitoJUnitRunner.class)
public class CommonPropertyTest {

    @Mock
    private PropertyReader reader;

    @Test
    public void shouldReturnValueFromResource() {
        // given
        Property<String> property = new CommonProperty<>("common.path", "default", PrimitivePropertyType.STRING);
        given(reader.getObject("common.path")).willReturn("some string");

        // when / then
        assertThat(property.determineValue(reader), equalTo("some string"));
    }

    @Test
    public void shouldReturnDefaultValue() {
        // given
        Property<String> property = new CommonProperty<>("common.path", "default", PrimitivePropertyType.STRING);

        // when / then
        assertThat(property.determineValue(reader), equalTo("default"));
    }

    @Test
    public void shouldReturnValueAsExportValue() {
        // given
        Property<String> property = new CommonProperty<>("common.path", "default", PrimitivePropertyType.STRING);
        String given = "given string";

        // when / then
        assertThat(property.toExportValue(given), equalTo(given));
    }

}
