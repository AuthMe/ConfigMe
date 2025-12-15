package ch.jalu.configme.properties.types;

import ch.jalu.configme.properties.convertresult.ConvertErrorRecorder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;

import static ch.jalu.typeresolver.TypeInfo.of;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.matchesPattern;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Test for {@link TemporalType}.
 */
@ExtendWith(MockitoExtension.class)
public class TemporalTypeTest {

    @AfterAll
    static void reset() {
        ConvertErrorRecorder errorRecorder = new ConvertErrorRecorder();
        TemporalType.LOCAL_DATE.convert("1970-01-01", errorRecorder);
        TemporalType.LOCAL_TIME.convert("12:00:00", errorRecorder);
        TemporalType.LOCAL_DATE_TIME.convert("1970-01-01 12:00:00", errorRecorder);
    }

    @Test
    void shouldNotAllowToInstantiateTemporalTypeWithInvalidArguments() {
        // when
        IllegalArgumentException noSupportedFormats = assertThrows(IllegalArgumentException.class,
            () -> new TemporalType<>(LocalDate.class, Collections.emptyList(), LocalDate::parse));

        // then
        assertThat(noSupportedFormats.getMessage(), matchesPattern("At least one supported format must be provided."));
    }

    @Test
    void shouldConvertToGivenTemporalTypeForSupportedFormat() {
        // given
        ConvertErrorRecorder errorRecorder = new ConvertErrorRecorder();
        LocalDate localDate = LocalDate.of(1970, 1, 31);
        LocalTime localTimeFull = LocalTime.of(15, 35, 40);
        LocalTime localTimeShort = LocalTime.of(15, 35);
        LocalDateTime localDateTime = LocalDateTime.of(1970, 1, 31, 15, 35, 40);

        // when / then
        assertThat(TemporalType.LOCAL_DATE.convert("1970-01-31", of(LocalDate.class), errorRecorder), equalTo(localDate));
        assertThat(TemporalType.LOCAL_DATE.convert("31.01.1970", of(LocalDate.class), errorRecorder), equalTo(localDate));
        assertThat(TemporalType.LOCAL_DATE.convert("01/31/1970", of(LocalDate.class), errorRecorder), equalTo(localDate));
        assertThat(TemporalType.LOCAL_TIME.convert("15:35:40", of(LocalTime.class), errorRecorder), equalTo(localTimeFull));
        assertThat(TemporalType.LOCAL_TIME.convert("15.35", of(LocalTime.class), errorRecorder), equalTo(localTimeShort));
        assertThat(TemporalType.LOCAL_TIME.convert("15:35", of(LocalTime.class), errorRecorder), equalTo(localTimeShort));
        assertThat(TemporalType.LOCAL_DATE_TIME.convert("1970-01-31 15:35:40", of(LocalDateTime.class), errorRecorder), equalTo(localDateTime));
        assertThat(TemporalType.LOCAL_DATE_TIME.convert("31.01.1970 15:35:40", of(LocalDateTime.class), errorRecorder), equalTo(localDateTime));
        assertThat(TemporalType.LOCAL_DATE_TIME.convert("01/31/1970 15:35:40", of(LocalDateTime.class), errorRecorder), equalTo(localDateTime));
    }

    @Test
    void shouldReturnNullForUnsupportedFormat() {
        // given
        ConvertErrorRecorder errorRecorder = new ConvertErrorRecorder();

        // when / then
        assertThat(TemporalType.LOCAL_DATE.convert("31-01-1970", of(LocalDate.class), errorRecorder), nullValue());
        assertThat(TemporalType.LOCAL_DATE.convert("1970.01.31", of(LocalDate.class), errorRecorder), nullValue());
        assertThat(TemporalType.LOCAL_DATE.convert("1970/01/31", of(LocalDate.class), errorRecorder), nullValue());
        assertThat(TemporalType.LOCAL_TIME.convert("25:35:40", of(LocalTime.class), errorRecorder), nullValue());
        assertThat(TemporalType.LOCAL_TIME.convert("15.35.40", of(LocalTime.class), errorRecorder), nullValue());
        assertThat(TemporalType.LOCAL_TIME.convert("15:35.40", of(LocalTime.class), errorRecorder), nullValue());
        assertThat(TemporalType.LOCAL_TIME.convert("15.35:40", of(LocalTime.class), errorRecorder), nullValue());
        assertThat(TemporalType.LOCAL_DATE_TIME.convert("31-01-1970 25:35:40", of(LocalDateTime.class), errorRecorder), nullValue());
        assertThat(TemporalType.LOCAL_DATE_TIME.convert("1970.01.31 15.35.40", of(LocalDateTime.class), errorRecorder), nullValue());
        assertThat(TemporalType.LOCAL_DATE_TIME.convert("1970/01/31 15.35:40", of(LocalDateTime.class), errorRecorder), nullValue());
    }

    @Test
    void shouldReturnNullForInvalidValue() {
        // given
        ConvertErrorRecorder errorRecorder = new ConvertErrorRecorder();

        // when / then
        assertThat(TemporalType.LOCAL_DATE.convert("test", of(LocalDate.class), errorRecorder), nullValue());
        assertThat(TemporalType.LOCAL_TIME.convert("test", of(LocalTime.class), errorRecorder), nullValue());
        assertThat(TemporalType.LOCAL_DATE_TIME.convert("test", of(LocalDateTime.class), errorRecorder), nullValue());
    }

    @Test
    void shouldReturnNullForNull() {
        // given
        ConvertErrorRecorder errorRecorder = new ConvertErrorRecorder();

        // when / then
        assertThat(TemporalType.LOCAL_DATE.convert(null, of(LocalDate.class), errorRecorder), nullValue());
        assertThat(TemporalType.LOCAL_TIME.convert(null, of(LocalTime.class), errorRecorder), nullValue());
        assertThat(TemporalType.LOCAL_DATE_TIME.convert(null, of(LocalDateTime.class), errorRecorder), nullValue());
    }

    @Test
    void shouldExportValueAsString() {
        // given / when / then
        assertThat(TemporalType.LOCAL_DATE.toExportValue(LocalDate.of(1970, 1, 31)), equalTo("1970-01-31"));
        assertThat(TemporalType.LOCAL_TIME.toExportValue(LocalTime.of(13, 55, 13)), equalTo("13:55:13"));
        assertThat(TemporalType.LOCAL_DATE_TIME.toExportValue(LocalDateTime.of(1970, 1, 31, 12, 30, 47)), equalTo("1970-01-31 12:30:47"));
    }

    @Test
    void shouldExportValueInMatchingFormatAsString() {
        // given
        ConvertErrorRecorder errorRecorder = new ConvertErrorRecorder();
        LocalDate localDate = TemporalType.LOCAL_DATE.convert("01/31/1970", errorRecorder);
        LocalTime localTime = TemporalType.LOCAL_TIME.convert("11:11", errorRecorder);
        LocalDateTime localDateTime = TemporalType.LOCAL_DATE_TIME.convert("31.01.1970 13:55:13", errorRecorder);

        // when / then
        assertThat(localDate, notNullValue());
        assertThat(localTime, notNullValue());
        assertThat(localDateTime, notNullValue());
        assertThat(TemporalType.LOCAL_DATE.toExportValue(localDate), equalTo("01/31/1970"));
        assertThat(TemporalType.LOCAL_TIME.toExportValue(localTime), equalTo("11:11"));
        assertThat(TemporalType.LOCAL_DATE_TIME.toExportValue(localDateTime), equalTo("31.01.1970 13:55:13"));
    }
}
