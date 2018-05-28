package ch.jalu.configme.neo.propertytype;

import ch.jalu.configme.neo.resource.PropertyReader;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test for {@link StringType}.
 */
public class StringTypeTest {

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
        PropertyType<String> type = new StringType();

        // when
        String result = type.getFromReader(reader, "str.path.test");

        // then
        assertThat(result, equalTo("Test value"));
    }

    @Test
    public void shouldReturnNullForMissingValue() {
        // given
        PropertyType<String> type = new StringType();

        // when
        String result = type.getFromReader(reader, "str.path.wrong");

        // then
        assertThat(result, nullValue());
    }

    @Test
    public void shouldDefineExportValue() {
        // given
        PropertyType<String> type = new StringType();

        // when
        Object exportValue = type.toExportValue("some value");

        // then
        assertThat(exportValue, equalTo("some value"));
    }
}
