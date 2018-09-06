package ch.jalu.configme.properties;

import ch.jalu.configme.properties.types.PropertyType;
import ch.jalu.configme.resource.PropertyReader;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class CommonPropertyTest {

    @Mock
    private PropertyReader reader;

    @Test
    public void shouldReturnValueFromResource() {
        Property<String> property = new CommonProperty<>("common.path", "default", PropertyType.stringType());

        given(reader.getObject("common.path")).willReturn("some string");

        assertThat(property.determineValue(reader), equalTo("some string"));
    }

    @Test
    public void shouldReturnDefaultValue() {
        Property<String> property = new CommonProperty<>("common.path", "default", PropertyType.stringType());

        assertThat(property.determineValue(reader), equalTo("default"));
    }

    @Test
    public void shouldReturnValueAsExportValue() {
        Property<String> property = new CommonProperty<>("common.path", "default", PropertyType.stringType());

        String given = "given string";

        assertThat(property.toExportValue(given), equalTo(given));
    }

}
