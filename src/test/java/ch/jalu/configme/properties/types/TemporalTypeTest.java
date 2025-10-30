package ch.jalu.configme.properties.types;

import ch.jalu.configme.properties.convertresult.ConvertErrorRecorder;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static ch.jalu.typeresolver.TypeInfo.of;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;

/**
 * Test for {@link TemporalType}.
 */
public class TemporalTypeTest {

    @Test
    void shouldConvertToGivenTemporalTypeForSupportedFormat() {
        // given
        ConvertErrorRecorder errorRecorder = new ConvertErrorRecorder();
        LocalDate localDate = LocalDate.of(1970, 1, 31);

        // when / then
        assertThat(TemporalType.LOCAL_DATE.convert("1970-01-31", of(LocalDate.class), errorRecorder), equalTo(localDate));
        assertThat(TemporalType.LOCAL_DATE.convert("31.01.1970", of(LocalDate.class), errorRecorder), equalTo(localDate));
        assertThat(TemporalType.LOCAL_DATE.convert("01/31/1970", of(LocalDate.class), errorRecorder), equalTo(localDate));
    }

    @Test
    void shouldReturnNullForUnsupportedFormat() {
        // given
        ConvertErrorRecorder errorRecorder = new ConvertErrorRecorder();

        // when / then
        assertThat(TemporalType.LOCAL_DATE.convert("31-01-1970", of(LocalDate.class), errorRecorder), nullValue());
        assertThat(TemporalType.LOCAL_DATE.convert("1970.01.31", of(LocalDate.class), errorRecorder), nullValue());
        assertThat(TemporalType.LOCAL_DATE.convert("1970/01/31", of(LocalDate.class), errorRecorder), nullValue());
    }

    @Test
    void shouldReturnNullForInvalidValue() {
        // given
        ConvertErrorRecorder errorRecorder = new ConvertErrorRecorder();

        // when / then
        assertThat(TemporalType.LOCAL_DATE.convert("test", of(LocalDate.class), errorRecorder), nullValue());
    }

    @Test
    void shouldReturnNullForNull() {
        // given
        ConvertErrorRecorder errorRecorder = new ConvertErrorRecorder();

        // when / then
        assertThat(TemporalType.LOCAL_DATE.convert(null, of(LocalDate.class), errorRecorder), nullValue());
    }

    @Test
    void shouldExportValueAsString() {
        // given / when / then
        assertThat(TemporalType.LOCAL_DATE.toExportValue(LocalDate.of(1970, 1, 31)), equalTo("1970-01-31"));
    }
}
