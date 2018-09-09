package ch.jalu.configme.properties;

import ch.jalu.configme.properties.inlinearray.StandardInlineArrayConverters;
import ch.jalu.configme.resource.PropertyReader;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

/**
 * Test for {@link InlineArrayProperty}.
 */
public class InlineArrayPropertyTest {

    @Test
    public void shouldReturnArrayFromInlineConvertHelper() {
        // given
        BaseProperty<String[]> property = new InlineArrayProperty<>(
            "inline_value",
            new String[] {"multiline", "message"},
            StandardInlineArrayConverters.STRING
        );
        PropertyReader reader = mock(PropertyReader.class);
        given(reader.getString("inline_value")).willReturn("hello\nkek");

        // when
        String[] result = property.getFromReader(reader);

        // then
        assertThat(result, equalTo(new String[] {"hello", "kek"}));
    }

    @Test
    public void shouldReturnConvertedValueAsExportValue() {
        // given
        Property<String[]> property = new InlineArrayProperty<>(
            "array",
            new String[] {},
            StandardInlineArrayConverters.STRING
        );
        String[] given = new String[] {"hello, chert", "how in hell?"};

        // when / then
        assertThat(property.toExportValue(given), equalTo("hello, chert\nhow in hell?"));
    }
}
