package ch.jalu.configme.properties.types;

import ch.jalu.configme.properties.convertresult.ConvertErrorRecorder;
import ch.jalu.typeresolver.reference.NestedTypeReference;
import org.junit.jupiter.api.Test;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static ch.jalu.typeresolver.TypeInfo.of;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;

/**
 * Test for {@link StringType}.
 */
class StringTypeTest {

    @Test
    void shouldConvertForMatchingType() {
        // given
        ConvertErrorRecorder errorRecorder = new ConvertErrorRecorder();
        int value = 4;

        // when / then
        assertThat(StringType.STRING.convert(value, of(String.class), errorRecorder), equalTo("4"));
        assertThat(StringType.STRING.convert(value, of(CharSequence.class), errorRecorder), equalTo("4"));
        assertThat(StringType.STRING.convert(value, of(Serializable.class), errorRecorder), equalTo("4"));
        assertThat(StringType.STRING.convert(value, of(Object.class), errorRecorder), equalTo("4"));
    }

    @Test
    void shouldNotConvertIfTypeDoesNotMatch() {
        // given
        ConvertErrorRecorder errorRecorder = new ConvertErrorRecorder();

        // when / then
        assertThat(StringType.STRING.convert("test", of(Character.class), errorRecorder), nullValue());
        assertThat(StringType.STRING.convert("test", of(List.class), errorRecorder), nullValue());
        assertThat(StringType.STRING.convert("test", of(String[].class), errorRecorder), nullValue());
        assertThat(StringType.STRING.convert("test", new NestedTypeReference<List<?>>() { }, errorRecorder), nullValue());
    }

    @Test
    void shouldConvertToString() {
        // given
        ConvertErrorRecorder errorRecorder = new ConvertErrorRecorder();

        // when / then
        assertThat(StringType.STRING.convert("Test text", errorRecorder), equalTo("Test text"));
        assertThat(StringType.STRING.convert(Boolean.FALSE, errorRecorder), equalTo("false"));
        assertThat(StringType.STRING.convert(34.56, errorRecorder), equalTo("34.56"));
        assertThat(StringType.STRING.convert(new ArrayList<>(), errorRecorder), equalTo("[]"));
        assertThat(StringType.STRING.convert(null, errorRecorder), nullValue());
    }

    @Test
    void shouldConvertToStringInLowercase() {
        // given
        ConvertErrorRecorder errorRecorder = new ConvertErrorRecorder();

        // when / then
        assertThat(StringType.STRING_LOWER_CASE.convert("Test text", errorRecorder), equalTo("test text"));
        assertThat(StringType.STRING_LOWER_CASE.convert(Boolean.FALSE, errorRecorder), equalTo("false"));
        assertThat(StringType.STRING_LOWER_CASE.convert(34.56, errorRecorder), equalTo("34.56"));
        assertThat(StringType.STRING_LOWER_CASE.convert(new ArrayList<>(), errorRecorder), equalTo("[]"));
        assertThat(StringType.STRING_LOWER_CASE.convert(null, errorRecorder), nullValue());
    }

    @Test
    void shouldReturnStringAsExportValue() {
        // given / when / then
        assertThat(StringType.STRING.toExportValue("Abc"), equalTo("Abc"));
        assertThat(StringType.STRING_LOWER_CASE.toExportValue("tests"), equalTo("tests"));
    }

    @Test
    void shouldNotReturnExportValueIfIsNotString() {
        // given / when / then
        assertThat(StringType.STRING.toExportValueIfApplicable("test"), equalTo("test"));
        assertThat(StringType.STRING_LOWER_CASE.toExportValueIfApplicable("test"), equalTo("test"));

        assertThat(StringType.STRING.toExportValueIfApplicable(5), nullValue());
        assertThat(StringType.STRING_LOWER_CASE.toExportValueIfApplicable(5), nullValue());
        assertThat(StringType.STRING.toExportValueIfApplicable(null), nullValue());
        assertThat(StringType.STRING_LOWER_CASE.toExportValueIfApplicable(null), nullValue());
        assertThat(StringType.STRING.toExportValueIfApplicable('t'), nullValue());
        assertThat(StringType.STRING_LOWER_CASE.toExportValueIfApplicable('t'), nullValue());
    }
}
