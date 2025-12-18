package ch.jalu.configme.properties;

import ch.jalu.configme.properties.convertresult.PropertyValue;
import ch.jalu.configme.resource.PropertyReader;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static ch.jalu.configme.TestUtils.isErrorValueOf;
import static ch.jalu.configme.TestUtils.isValidValueOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test for {@link LocalDateTimeProperty}.
 */
@ExtendWith(MockitoExtension.class)
class LocalDateTimePropertyTest {

    private static PropertyReader reader;

    @BeforeAll
    static void setUpConfiguration() {
        reader = mock(PropertyReader.class);
        when(reader.getObject("local-date-time.path.test")).thenReturn("2001-10-20 19:55:30");
        when(reader.getObject("local-date-time.path.wrong")).thenReturn("20-2001-10 55:19:30");
    }

    @Test
    void shouldGetLocalDateTimeValue() {
        // given
        Property<LocalDateTime> property = new LocalDateTimeProperty("local-date-time.path.test", LocalDateTime.of(1970, 1, 31, 12, 0));

        // when
        PropertyValue<LocalDateTime> result = property.determineValue(reader);

        // then
        assertThat(result, isValidValueOf(LocalDateTime.of(2001, 10, 20, 19, 55, 30)));
    }

    @Test
    void shouldGetLocalDateTimeDefault() {
        // given
        LocalDateTime defaultDateTime = LocalDateTime.of(1970, 1, 31, 12, 0);
        Property<LocalDateTime> property = new LocalDateTimeProperty("local-date-time.path.wrong", defaultDateTime);

        // when
        PropertyValue<LocalDateTime> result = property.determineValue(reader);

        // then
        assertThat(result, isErrorValueOf(defaultDateTime));
    }

    @Test
    void shouldReturnValueForExport() {
        // given
        Property<LocalDateTime> property = new LocalDateTimeProperty("export.path.local-date-time", LocalDateTime.of(1970, 1, 31, 12, 0));

        // when
        Object exportedValue = property.toExportValue(LocalDateTime.of(2001, 10, 22, 11, 39, 42));

        // then
        assertThat(exportedValue, equalTo("2001-10-22 11:39:42"));
    }
}
