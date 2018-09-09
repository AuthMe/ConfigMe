package ch.jalu.configme.properties;

import ch.jalu.configme.properties.types.PropertyType;
import ch.jalu.configme.resource.PropertyReader;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;

/**
 * Test for {@link ArrayProperty}.
 */
@RunWith(MockitoJUnitRunner.class)
public class ArrayPropertyTest {

    @Mock
    private PropertyReader reader;

    @Test
    public void shouldReturnArrayFromSingletonValue() {
        // given
        ArrayProperty<String> property = new ArrayProperty<>(
            "singleton",
            new String[] {"multiline", "message"},
            PropertyType.stringType());

        given(reader.getObject("singleton")).willReturn("hello");

        // when
        String[] result = property.getFromReader(reader);

        // then
        assertThat(result, equalTo(new String[] {"hello"}));
    }

    @Test
    public void shouldReturnArrayFromResource() {
        Property<String[]> property = new ArrayProperty<>(
            "array",
            new String[] {"multiline", "message"},
            PropertyType.stringType()
        );

        given(reader.getObject("array")).willReturn(Arrays.asList("qwerty", "123"));

        assertThat(property.determineValue(reader), equalTo(new String[] {"qwerty", "123"}));
    }

    @Test
    public void shouldReturnDefaultValue() {
        Property<String[]> property = new ArrayProperty<>(
            "array",
            new String[] {"multiline", "message c:"},
            PropertyType.stringType()
        );

        given(reader.getObject("array")).willReturn(null);

        assertThat(property.determineValue(reader), equalTo(new String[] {"multiline", "message c:"}));
    }

    @Test
    public void shouldReturnValueAsExportValue() {
        // given
        Property<String[]> property = new ArrayProperty<>(
            "array",
            new String[] {},
            PropertyType.stringType());

        String[] given = new String[] {"hello, chert", "how in hell?"};

        // when / then
        assertThat(property.toExportValue(given), equalTo(new String[] {"hello, chert", "how in hell?"}));
    }
}
