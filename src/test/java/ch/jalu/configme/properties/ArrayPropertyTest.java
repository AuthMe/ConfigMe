package ch.jalu.configme.properties;

import ch.jalu.configme.properties.helper.InlineConvertHelper;
import ch.jalu.configme.properties.helper.PrimitiveConvertHelper;
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

@RunWith(MockitoJUnitRunner.class)
public class ArrayPropertyTest {

    @Mock
    private PropertyReader reader;

    @Test
    public void shouldReturnArrayFromInlineConvertHelper() {
        ArrayProperty<String> property = new ArrayProperty<>(
            "inline_value",
            new String[] {"multiline", "message"},
            PropertyType.stringType(),
            InlineConvertHelper.stringHelper()
        );

        given(reader.getObject("inline_value")).willReturn("hello\\nkek");

        String[] result = property.getFromResource(reader);

        assertThat(result, equalTo(new String[] {"hello", "kek"}));
    }

    @Test
    public void shouldReturnArrayFromSingletonValue() {
        ArrayProperty<String> property = new ArrayProperty<>(
            "signleton",
            new String[] {"multiline", "message"},
            PropertyType.stringType(),
            null
        );

        given(reader.getObject("signleton")).willReturn("hello");

        String[] result = property.getFromResource(reader);

        assertThat(result, equalTo(new String[] {"hello"}));
    }

    @Test
    public void shouldReturnArrayFromResource() {
        Property<String[]> property = new ArrayProperty<>(
            "array",
            new String[] {"multiline", "message"},
            PropertyType.stringType(),
            null
        );

        given(reader.getObject("array")).willReturn(Arrays.asList("qwerty", "123"));

        assertThat(property.determineValue(reader), equalTo(new String[] {"qwerty", "123"}));
    }

    @Test
    public void shouldReturnDefaultValue() {
        Property<String[]> property = new ArrayProperty<>(
            "array",
            new String[] {"multiline", "message c:"},
            PropertyType.stringType(),
            null
        );

        given(reader.getObject("array")).willReturn(null);

        assertThat(property.determineValue(reader), equalTo(new String[] {"multiline", "message c:"}));
    }

    @Test
    public void shouldReturnValueAsExportValue() {
        Property<String[]> property = new ArrayProperty<>(
            "array",
            new String[] {},
            PropertyType.stringType(),
            null
        );

        String[] given = new String[] {"hello, chert", "how in hell?"};

        assertThat(property.toExportValue(given), equalTo(new String[] {"hello, chert", "how in hell?"}));
    }

    @Test
    public void shouldReturnConvertedValueAsExportValue() {
        Property<String[]> property = new ArrayProperty<>(
            "array",
            new String[] {},
            PropertyType.stringType(),
            PrimitiveConvertHelper.DEFAULT_STRING
        );

        String[] given = new String[] {"hello, chert", "how in hell?"};

        assertThat(property.toExportValue(given), equalTo("hello, chert\nhow in hell?"));
    }

}
