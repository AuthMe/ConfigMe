package ch.jalu.configme.properties;

import ch.jalu.configme.properties.convertresult.ConvertErrorRecorder;
import ch.jalu.configme.properties.convertresult.PropertyValue;
import ch.jalu.configme.properties.types.PropertyType;
import ch.jalu.configme.properties.types.StringType;
import ch.jalu.configme.resource.PropertyReader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static ch.jalu.configme.TestUtils.isErrorValueOf;
import static ch.jalu.configme.TestUtils.isValidValueOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

/**
 * Test for {@link ArrayProperty}.
 */
@ExtendWith(MockitoExtension.class)
class ArrayPropertyTest {

    @Mock
    private PropertyReader reader;

    @Test
    void shouldReturnNullForNonCollectionTypes() {
        // given
        ArrayProperty<String> property = new ArrayProperty<>(
            "singleton", StringType.STRING.arrayType(),
            "multiline", "message");

        given(reader.getObject("singleton")).willReturn("hello");

        // when
        String[] result = property.getFromReader(reader, new ConvertErrorRecorder());

        // then
        assertThat(result, nullValue());
    }

    @Test
    void shouldHandleNull() {
        // given
        ArrayProperty<String> property = new ArrayProperty<>(
            "singleton", StringType.STRING, String[]::new,
            new String[] {"multiline", "message"});

        given(reader.getObject("singleton")).willReturn(null);

        // when
        String[] result = property.getFromReader(reader, new ConvertErrorRecorder());

        // then
        assertThat(result, nullValue());
    }

    @Test
    void shouldReturnArrayFromResource() {
        // given
        Property<String[]> property = new ArrayProperty<>(
            "array", StringType.STRING, String[]::new,
            new String[] {"multiline", "message"});
        given(reader.getObject("array")).willReturn(Arrays.asList("qwerty", "123"));

        // when
        PropertyValue<String[]> result = property.determineValue(reader);

        // then
        assertThat(result, isValidValueOf(new String[] {"qwerty", "123"}));
    }

    @Test
    void shouldReturnDefaultValue() {
        // given
        Property<String[]> property = new ArrayProperty<>(
            "array", StringType.STRING, String[]::new,
            new String[] {"multiline", "message c:"});

        given(reader.getObject("array")).willReturn(null);

        // when
        PropertyValue<String[]> result = property.determineValue(reader);

        // then
        assertThat(result, isErrorValueOf(new String[] {"multiline", "message c:"}));
    }

    @Test
    void shouldReturnValueAsExportValue() {
        // given
        Property<String[]> property = new ArrayProperty<>(
            "array", StringType.STRING, String[]::new,
            new String[] {});

        String[] givenArray = new String[] {"hello, chert", "how in hell?"};

        // when
        Object exportValue = property.toExportValue(givenArray);

        // then
        assertThat(exportValue, instanceOf(List.class));
        assertThat((List<?>) exportValue, contains(givenArray));
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldCreatePropertyWithCustomArrayType() {
        // given
        String path = "engine.probabilities";
        PropertyType<Integer[]> intArrayType = mock(PropertyType.class);

        ConvertErrorRecorder errorRecorder = new ConvertErrorRecorder();
        String value = "test";
        given(intArrayType.convert(value, errorRecorder)).willReturn(new Integer[]{3, 4});
        given(reader.getObject(path)).willReturn(value);

        // when
        ArrayProperty<Integer> property = new ArrayProperty<>(path, intArrayType, 3, 8);

        // then
        assertThat(property.getPath(), equalTo(path));
        assertThat(property.getDefaultValue(), arrayContaining(3, 8));
        assertThat(property.getFromReader(reader, errorRecorder), arrayContaining(3, 4));
    }
}
