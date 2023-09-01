package ch.jalu.configme.properties.types;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;

import ch.jalu.configme.properties.convertresult.ConvertErrorRecorder;

import static ch.jalu.typeresolver.TypeInfo.of;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Test for {@link RegexType}.
 */
public class RegexTypeTest {

    @Test
    void shouldSupportPatternClassesAndParentsOnly() {
        // given / when / then
        assertTrue(RegexType.REGEX.canConvertToType(of(Pattern.class)));
        assertTrue(RegexType.REGEX.canConvertToType(of(Serializable.class)));
        assertTrue(RegexType.REGEX.canConvertToType(of(Object.class)));

        assertFalse(RegexType.REGEX.canConvertToType(of(String.class)));
        assertFalse(RegexType.REGEX.canConvertToType(of(Integer.class)));
        assertFalse(RegexType.REGEX.canConvertToType(of(List.class)));
        assertFalse(RegexType.REGEX.canConvertToType(of(boolean[].class)));
        assertFalse(RegexType.REGEX.canConvertToType(of(Date.class)));
    }

    @Test
    void shouldConvertForValidRegex() {
        // given
        ConvertErrorRecorder errorRecorder = new ConvertErrorRecorder();

        // then / when
        assertInstanceOf(Pattern.class, RegexType.REGEX.convert("s_.*?", errorRecorder));
        assertInstanceOf(Pattern.class, RegexType.REGEX.convert("\\s*", errorRecorder));
        assertInstanceOf(Pattern.class, RegexType.REGEX.convert("Hello", errorRecorder));
        assertInstanceOf(Pattern.class, RegexType.REGEX.convert("$", errorRecorder));
    }

    @Test
    void shouldReturnNullForInvalidRegex() {
        // given
        ConvertErrorRecorder errorRecorder = new ConvertErrorRecorder();

        // when / then
        assertThat(RegexType.REGEX.convert("[abc", errorRecorder), nullValue());
        assertThat(RegexType.REGEX.convert("(", errorRecorder), nullValue());
        assertThat(RegexType.REGEX.convert("[a-z", errorRecorder), nullValue());
        assertThat(RegexType.REGEX.convert("a{,3}", errorRecorder), nullValue());
        assertThat(RegexType.REGEX.convert("{2,1}", errorRecorder), nullValue());
    }

    @Test
    void shouldReturnNullForNull() {
        // given
        ConvertErrorRecorder errorRecorder = new ConvertErrorRecorder();

        // when / then
        assertThat(RegexType.REGEX.convert(null, errorRecorder), nullValue());
    }

    @Test
    void shouldReturnNullForUnsupportedTypes() {
        // given
        ConvertErrorRecorder errorRecorder = new ConvertErrorRecorder();

        // when / then
        assertThat(RegexType.REGEX.convert(3, of(Pattern.class), errorRecorder), nullValue());
        assertThat(RegexType.REGEX.convert(1, of(Pattern.class), errorRecorder), nullValue());
        assertThat(RegexType.REGEX.convert('c', of(Pattern.class), errorRecorder), nullValue());
        assertThat(RegexType.REGEX.convert(false, of(Pattern.class), errorRecorder), nullValue());
        assertThat(RegexType.REGEX.convert(TimeUnit.SECONDS, of(Pattern.class), errorRecorder), nullValue());
    }

    @Test
    void shouldExportValueAsString() {
        // given 
        Pattern pattern1 = Pattern.compile("#\\w+");
        Pattern pattern2 = Pattern.compile("^[A-Za-z]+$");
    
        // when / then
        assertThat(RegexType.REGEX.toExportValue(pattern1), equalTo("#\\w+"));
        assertThat(RegexType.REGEX.toExportValue(pattern2), equalTo("^[A-Za-z]+$"));
    }
}
