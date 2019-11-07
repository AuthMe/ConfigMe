package ch.jalu.configme.properties;

import ch.jalu.configme.configurationdata.PropertyValue;
import ch.jalu.configme.resource.PropertyReader;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static ch.jalu.configme.TestUtils.isErrorValueOf;
import static ch.jalu.configme.TestUtils.isValidValueOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test for {@link BooleanProperty}.
 */
public class BooleanPropertyTest {

    private static PropertyReader reader;

    @BeforeAll
    public static void setUpConfiguration() {
        reader = mock(PropertyReader.class);
        when(reader.getObject("bool.path.test")).thenReturn(true);
        when(reader.getObject("bool.path.wrong")).thenReturn(null);
    }

    @Test
    public void shouldGetBoolValue() {
        // given
        Property<Boolean> property = new BooleanProperty("bool.path.test", false);

        // when
        PropertyValue<Boolean> result = property.determineValue(reader);

        // then
        assertThat(result, isValidValueOf(true));
    }

    @Test
    public void shouldGetBoolDefault() {
        // given
        Property<Boolean> property = new BooleanProperty("bool.path.wrong", true);

        // when
        PropertyValue<Boolean> result = property.determineValue(reader);

        // then
        assertThat(result, isErrorValueOf(true));
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
