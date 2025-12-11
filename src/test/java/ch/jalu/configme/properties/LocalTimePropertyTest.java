package ch.jalu.configme.properties;

import ch.jalu.configme.properties.convertresult.PropertyValue;
import ch.jalu.configme.resource.PropertyReader;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;

import static ch.jalu.configme.TestUtils.isErrorValueOf;
import static ch.jalu.configme.TestUtils.isValidValueOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test for {@link LocalTimeProperty}.
 */
@ExtendWith(MockitoExtension.class)
class LocalTimePropertyTest {

    private static PropertyReader reader;

    @BeforeAll
    static void setUpConfiguration() {
        reader = mock(PropertyReader.class);
        when(reader.getObject("local-time.path.test")).thenReturn("19:55:30");
        when(reader.getObject("local-time.path.wrong")).thenReturn("55:19:30");
    }

    @Test
    void shouldGetLocalTimeValue() {
        // given
        Property<LocalTime> property = new LocalTimeProperty("local-time.path.test", LocalTime.of(12, 0));

        // when
        PropertyValue<LocalTime> result = property.determineValue(reader);

        // then
        assertThat(result, isValidValueOf(LocalTime.of(19, 55, 30)));
    }

    @Test
    void shouldGetLocalTimeDefault() {
        // given
        LocalTime defaultTime = LocalTime.of(12, 0);
        Property<LocalTime> property = new LocalTimeProperty("local-time.path.wrong", defaultTime);

        // when
        PropertyValue<LocalTime> result = property.determineValue(reader);

        // then
        assertThat(result, isErrorValueOf(defaultTime));
    }

    @Test
    void shouldReturnValueForExport() {
        // given
        Property<LocalTime> property = new LocalTimeProperty("export.path.local-time", LocalTime.of(23, 59));

        // when
        Object exportedValue = property.toExportValue(LocalTime.of(19, 55));

        // then
        assertThat(exportedValue, equalTo("19:55:00"));
    }
}
