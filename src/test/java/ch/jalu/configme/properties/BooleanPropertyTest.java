package ch.jalu.configme.properties;

import ch.jalu.configme.resource.PropertyReader;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test for {@link BooleanProperty}.
 */
public class BooleanPropertyTest {

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
        Property<Boolean> property = new BooleanProperty("bool.path.test", false);

        // when
        boolean result = property.determineValue(reader);

        // then
        assertThat(result, equalTo(true));
    }

    @Test
    public void shouldGetBoolDefault() {
        // given
        Property<Boolean> property = new BooleanProperty("bool.path.wrong", true);

        // when
        boolean result = property.determineValue(reader);

        // then
        assertThat(result, equalTo(true));
    }

    @Test
    public void shouldReturnExportRepresentation() {
        // given
        Property<Boolean> property = new BooleanProperty("bool.path.test", true);

        // when
        Object exportValue = property.toExportValue(true);

        // then
        assertThat(exportValue, equalTo(true));
    }
}
