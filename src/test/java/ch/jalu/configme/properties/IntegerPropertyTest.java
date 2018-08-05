package ch.jalu.configme.properties;

import ch.jalu.configme.resource.PropertyReader;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test for {@link IntegerProperty}.
 */
public class IntegerPropertyTest {

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
        Property<Integer> property = new IntegerProperty("int.path.test", 3);

        // when
        int result = property.determineValue(reader);

        // then
        assertThat(result, equalTo(27));
    }

    @Test
    public void shouldGetIntDefault() {
        // given
        Property<Integer> property = new IntegerProperty("int.path.wrong", -10);

        // when
        int result = property.determineValue(reader);

        // then
        assertThat(result, equalTo(-10));
    }

    @Test
    public void shouldReturnValueForExport() {
        // given
        Property<Integer> property = new IntegerProperty("some.path", -5);

        // when
        Object exportValue = property.toExportValue(45);

        // then
        assertThat(exportValue, equalTo(45));
    }
}
