package ch.jalu.configme.properties;

import ch.jalu.configme.properties.convertresult.ConvertErrorRecorder;
import ch.jalu.configme.properties.types.InlineArrayPropertyType;
import ch.jalu.configme.resource.PropertyReader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

/**
 * Test for {@link InlineArrayProperty}.
 */
@ExtendWith(MockitoExtension.class)
class InlineArrayPropertyTest {

    @Test
    void shouldReturnArrayFromInlineConvertHelper() {
        // given
        BaseProperty<String[]> property = new InlineArrayProperty<>(
            "inline_value",
            InlineArrayPropertyType.STRING,
            new String[] {"multiline", "message"}
        );
        PropertyReader reader = mock(PropertyReader.class);
        given(reader.getObject("inline_value")).willReturn("hello\nkek");

        // when
        String[] result = property.getFromReader(reader, new ConvertErrorRecorder());

        // then
        assertThat(result, equalTo(new String[] {"hello", "kek"}));
    }

    @Test
    void shouldReturnConvertedValueAsExportValue() {
        // given
        Property<String[]> property = new InlineArrayProperty<>(
            "array",
            InlineArrayPropertyType.STRING,
            new String[] {}
        );
        String[] given = new String[] {"hello, chert", "how in hell?"};

        // when / then
        assertThat(property.toExportValue(given), equalTo("hello, chert\nhow in hell?"));
    }

    @Test
    void shouldLogErrorForFailedConversion() {
        // given
        String value = "3,four,5";
        InlineArrayProperty<Integer> property = new InlineArrayProperty<>("path", InlineArrayPropertyType.INTEGER,  new Integer[0]);
        ConvertErrorRecorder errorRecorder = new ConvertErrorRecorder();
        PropertyReader reader = mock(PropertyReader.class);
        given(reader.getObject("path")).willReturn(value);

        // when
        Integer[] result = property.getFromReader(reader, errorRecorder);

        // then
        assertThat(result, arrayContaining(3, 5));
        assertThat(errorRecorder.isFullyValid(), equalTo(false));
    }
    @Test
    public void testBigDecimalSupport() {
        InlineArrayPropertyType<BigDecimal> type = new InlineArrayPropertyType<>(BigDecimal.class, ',');
        BigDecimal[] array = new BigDecimal[]{BigDecimal.valueOf(1.23), BigDecimal.valueOf(4.56)};
        String serialized = type.toPrimitive(array);
        BigDecimal[] deserialized = type.fromPrimitive(serialized);
        assertArrayEquals(array, deserialized);
    }

}
