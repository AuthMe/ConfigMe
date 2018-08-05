package ch.jalu.configme.properties;

import ch.jalu.configme.resource.PropertyReader;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test for {@link StringProperty}.
 */
public class StringPropertyTest {

    private static PropertyReader reader;

    @BeforeClass
    public static void setUpConfiguration() {
        reader = mock(PropertyReader.class);
        when(reader.getString("str.path.test")).thenReturn("Test value");
        when(reader.getString("str.path.wrong")).thenReturn(null);
    }

    @Test
    public void shouldGetStringValue() {
        // given
        Property<String> property = new StringProperty("str.path.test", "unused default");

        // when
        String result = property.determineValue(reader);

        // then
        assertThat(result, equalTo("Test value"));
    }

    @Test
    public void shouldGetStringDefault() {
        // given
        Property<String> property = new StringProperty("str.path.wrong", "given default value");

        // when
        String result = property.determineValue(reader);

        // then
        assertThat(result, equalTo("given default value"));
    }

    @Test
    public void shouldDefineExportValue() {
        // given
        Property<String> property = new StringProperty("path", "def. value");

        // when
        Object exportValue = property.toExportValue("some value");

        // then
        assertThat(exportValue, equalTo("some value"));
    }
}
