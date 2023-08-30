package ch.jalu.configme.properties.types;

import ch.jalu.configme.properties.convertresult.ConvertErrorRecorder;
import org.junit.jupiter.api.Test;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static ch.jalu.typeresolver.TypeInfo.of;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test for {@link BooleanType}.
 */
class BooleanTypeTest {

    @Test
    void shouldSupportBooleanClassesAndParentsOnly() {
        // given / when / then
        assertTrue(BooleanType.BOOLEAN.canConvertToType(of(boolean.class)));
        assertTrue(BooleanType.BOOLEAN.canConvertToType(of(Boolean.class)));
        assertTrue(BooleanType.BOOLEAN.canConvertToType(of(Serializable.class)));
        assertTrue(BooleanType.BOOLEAN.canConvertToType(of(Object.class)));

        assertFalse(BooleanType.BOOLEAN.canConvertToType(of(int.class)));
        assertFalse(BooleanType.BOOLEAN.canConvertToType(of(boolean[].class)));
        assertFalse(BooleanType.BOOLEAN.canConvertToType(of(String.class)));
        assertFalse(BooleanType.BOOLEAN.canConvertToType(of(List.class)));
    }

    @Test
    void shouldConvertFromBooleanValue() {
        // given
        ConvertErrorRecorder errorRecorder = new ConvertErrorRecorder();

        // when / then
        assertThat(BooleanType.BOOLEAN.convert(true, errorRecorder), equalTo(true));
        assertThat(BooleanType.BOOLEAN.convert(false, errorRecorder), equalTo(false));
    }

    @Test
    void shouldConvertFromStringValue() {
        // given
        ConvertErrorRecorder errorRecorder = new ConvertErrorRecorder();

        // when / then
        assertThat(BooleanType.BOOLEAN.convert("true", errorRecorder), equalTo(true));
        assertThat(BooleanType.BOOLEAN.convert("false", errorRecorder), equalTo(false));
        assertThat(BooleanType.BOOLEAN.convert("FALSE", errorRecorder), equalTo(false));
        assertThat(BooleanType.BOOLEAN.convert("True", errorRecorder), equalTo(true));
        assertThat(BooleanType.BOOLEAN.convert("other", errorRecorder), nullValue());
        assertThat(BooleanType.BOOLEAN.convert("t", errorRecorder), nullValue());
        assertThat(BooleanType.BOOLEAN.convert("", errorRecorder), nullValue());
    }

    @Test
    void shouldReturnNullForNull() {
        // given
        ConvertErrorRecorder errorRecorder = new ConvertErrorRecorder();

        // when / then
        assertThat(BooleanType.BOOLEAN.convert(null, errorRecorder), nullValue());
    }

    @Test
    void shouldReturnNullForUnsupportedTypes() {
        // given
        ConvertErrorRecorder errorRecorder = new ConvertErrorRecorder();

        // when / then
        assertThat(BooleanType.BOOLEAN.convert(3, of(Boolean.class), errorRecorder), nullValue());
        assertThat(BooleanType.BOOLEAN.convert(1, of(boolean.class), errorRecorder), nullValue());
        assertThat(BooleanType.BOOLEAN.convert('t', of(Boolean.class), errorRecorder), nullValue());
        assertThat(BooleanType.BOOLEAN.convert(TimeUnit.MINUTES, of(Boolean.class), errorRecorder), nullValue());
    }

    @Test
    void shouldExportValueAsBoolean() {
        // given / when / then
        assertThat(BooleanType.BOOLEAN.toExportValue(true), equalTo(true));
        assertThat(BooleanType.BOOLEAN.toExportValue(false), equalTo(false));
    }

    @Test
    void shouldCreateExportValueOnlyForBooleans() {
        // given / when / then
        assertThat(BooleanType.BOOLEAN.toExportValueIfApplicable(true), equalTo(true));
        assertThat(BooleanType.BOOLEAN.toExportValueIfApplicable(false), equalTo(false));

        assertThat(BooleanType.BOOLEAN.toExportValueIfApplicable(3), nullValue());
        assertThat(BooleanType.BOOLEAN.toExportValueIfApplicable(null), nullValue());
        assertThat(BooleanType.BOOLEAN.toExportValueIfApplicable("false"), nullValue());
        assertThat(BooleanType.BOOLEAN.toExportValueIfApplicable(new ArrayList<>()), nullValue());
    }
}
