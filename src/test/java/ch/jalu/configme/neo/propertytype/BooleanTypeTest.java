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
 * Test for {@link BooleanType}.
 */
public class BooleanTypeTest {

    private static PropertyReader reader;

    @BeforeClass
    public static void setUpConfiguration() {
        reader = mock(PropertyReader.class);
        when(reader.getBoolean("bool.path.test")).thenReturn(true);
        when(reader.getBoolean("bool.path.wrong")).thenReturn(null);
    }

    @Test
    public void shouldGetBoolValue() {
        // given
        PropertyType<Boolean> type = new BooleanType();

        // when
        Boolean result = type.getFromReader(reader, "bool.path.test");

        // then
        assertThat(result, equalTo(true));
    }

    @Test
    public void shouldReturnNullForMissingValue() {
        // given
        PropertyType<Boolean> type = new BooleanType();

        // when
        Boolean result = type.getFromReader(reader, "bool.path.wrong");

        // then
        assertThat(result, nullValue());
    }

    @Test
    public void shouldReturnExportRepresentation() {
        // given
        PropertyType<Boolean> type = new BooleanType();

        // when
        Object exportValue = type.toExportValue(true);

        // then
        assertThat(exportValue, equalTo(true));
    }
}
