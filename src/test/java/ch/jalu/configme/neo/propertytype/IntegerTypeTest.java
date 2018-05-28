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
 * Test for {@link IntegerType}.
 */
public class IntegerTypeTest {

    private static PropertyReader reader;

    @BeforeClass
    public static void setUpConfiguration() {
        reader = mock(PropertyReader.class);
        when(reader.getInt("int.path.test")).thenReturn(27);
        when(reader.getInt("int.path.wrong")).thenReturn(null);
    }

    @Test
    public void shouldGetIntValue() {
        // given
        PropertyType<Integer> type = new IntegerType();

        // when
        Integer result = type.getFromReader(reader, "int.path.test");

        // then
        assertThat(result, equalTo(27));
    }

    @Test
    public void shouldReturnNullForMissingValue() {
        // given
        PropertyType<Integer> type = new IntegerType();

        // when
        Integer result = type.getFromReader(reader, "int.path.wrong");

        // then
        assertThat(result, nullValue());
    }

    @Test
    public void shouldReturnValueForExport() {
        // given
        PropertyType<Integer> type = new IntegerType();

        // when
        Object exportValue = type.toExportValue(45);

        // then
        assertThat(exportValue, equalTo(45));
    }
}
