package ch.jalu.configme.properties;

import ch.jalu.configme.resource.PropertyReader;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test for {@link StringProperty}.
 */
@ExtendWith(MockitoExtension.class)
class StringPropertyTest {

    private static PropertyReader reader;

    @BeforeAll
    static void setUpConfiguration() {
        reader = mock(PropertyReader.class);
        when(reader.getObject("str.path.test")).thenReturn("Test value");
        when(reader.getObject("str.path.wrong")).thenReturn(null);
    }

    @Test
    void shouldGetStringValue() {
        // given
        Property<String> property = new StringProperty("str.path.test", "unused default");

        // when
        String result = property.determineValue(reader).getValue();

        // then
        assertThat(result, equalTo("Test value"));
    }

    @Test
    void shouldGetStringDefault() {
        // given
        Property<String> property = new StringProperty("str.path.wrong", "given default value");

        // when
        String result = property.determineValue(reader).getValue();

        // then
        assertThat(result, equalTo("given default value"));
    }

    @Test
    void shouldDefineExportValue() {
        // given
        Property<String> property = new StringProperty("path", "def. value");

        // when
        Object exportValue = property.toExportValue("some value");

        // then
        assertThat(exportValue, equalTo("some value"));
    }

    @Test
    void shouldReturnStringForNumber() {
        // given
        Property<String> property1 = new StringProperty("one", "");
        Property<String> property2 = new StringProperty("two", "");
        given(reader.getObject(property1.getPath())).willReturn(1);
        given(reader.getObject(property2.getPath())).willReturn(-5.328);

        // when
        String value1 = property1.determineValue(reader).getValue();
        String value2 = property2.determineValue(reader).getValue();

        // then
        assertThat(value1, equalTo("1"));
        assertThat(value2, equalTo("-5.328"));
    }

    @Test
    void shouldReturnStringFromBoolean() {
        // given
        Property<String> property = new StringProperty("test", "");
        given(reader.getObject(property.getPath())).willReturn(false);

        // when
        String value = property.determineValue(reader).getValue();

        // then
        assertThat(value, equalTo("false"));
    }
}
