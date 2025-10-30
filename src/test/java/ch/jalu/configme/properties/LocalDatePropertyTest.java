package ch.jalu.configme.properties;

import ch.jalu.configme.properties.convertresult.PropertyValue;
import ch.jalu.configme.resource.PropertyReader;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static ch.jalu.configme.TestUtils.isErrorValueOf;
import static ch.jalu.configme.TestUtils.isValidValueOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test for {@link LocalDateProperty}.
 */
@ExtendWith(MockitoExtension.class)
class LocalDatePropertyTest {

    private static PropertyReader reader;

    @BeforeAll
    static void setUpConfiguration() {
        reader = mock(PropertyReader.class);
        when(reader.getObject("local-date.path.test")).thenReturn("1999-01-31");
        when(reader.getObject("local-date.path.wrong")).thenReturn("31-01-1999");
    }

    @Test
    void shouldGetLocalDateValue() {
        // given
        Property<LocalDate> property = new LocalDateProperty("local-date.path.test", LocalDate.of(1970, 1, 31));

        // when
        PropertyValue<LocalDate> result = property.determineValue(reader);

        //then
        assertThat(result, isValidValueOf(LocalDate.of(1999, 1, 31)));
    }

    @Test
    void shouldGetLocalDateDefault() {
        // given
        LocalDate defaultValue = LocalDate.of(1970, 1, 31);
        Property<LocalDate> property = new LocalDateProperty("local-date.path.wrong", defaultValue);

        // when
        PropertyValue<LocalDate> result = property.determineValue(reader);

        // then
        assertThat(result, isErrorValueOf(defaultValue));
    }

    @Test
    void shouldReturnValueForExport() {
        // given
        Property<LocalDate> property = new LocalDateProperty("export.path.local-date", LocalDate.of(1970, 1, 31));

        // when
        Object exportValue = property.toExportValue(LocalDate.of(2000, 12, 31));

        // then
        assertThat(exportValue, equalTo("2000-12-31"));
    }
}
