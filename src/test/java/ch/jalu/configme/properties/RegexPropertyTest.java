package ch.jalu.configme.properties;

import ch.jalu.configme.SettingsManager;
import ch.jalu.configme.properties.convertresult.ConvertErrorRecorder;
import ch.jalu.configme.resource.PropertyReader;
import org.junit.jupiter.api.Test;

import java.util.regex.Pattern;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

/**
 * Test for {@link RegexProperty}.
 */
class RegexPropertyTest {

    @Test
    void shouldCompileDefaultStringParamToPattern() {
        // given
        String pattern = "(19|20)[0-9]{2}";

        // when
        RegexProperty property = new RegexProperty("validYears", pattern);

        // then
        assertThat(property.getDefaultValue().pattern(), equalTo(pattern));
    }

    @Test
    void shouldLoadValue() {
        // given
        RegexProperty property = new RegexProperty("names.whitelist", "s_.*?");
        PropertyReader reader = mock(PropertyReader.class);
        given(reader.getString("names.whitelist")).willReturn("m[0-9]+");
        ConvertErrorRecorder convertErrorRecorder = new ConvertErrorRecorder();

        // when
        Pattern result = property.getFromReader(reader, convertErrorRecorder);

        // then
        assertThat(result.pattern(), equalTo("m[0-9]+"));
    }

    @Test
    void shouldReturnNullForMissingValue() {
        // given
        RegexProperty property = new RegexProperty("names.whitelist", "s_.*?");
        PropertyReader reader = mock(PropertyReader.class);
        given(reader.getString("names.whitelist")).willReturn(null);
        ConvertErrorRecorder convertErrorRecorder = new ConvertErrorRecorder();

        // when
        Pattern result = property.getFromReader(reader, convertErrorRecorder);

        // then
        assertThat(result, nullValue());
    }

    @Test
    void shouldHandlePatternErrorGracefully() {
        // given
        RegexProperty property = new RegexProperty("names.whitelist", "s_.*?");
        PropertyReader reader = mock(PropertyReader.class);
        given(reader.getString("names.whitelist")).willReturn("m[0-9+");
        ConvertErrorRecorder convertErrorRecorder = new ConvertErrorRecorder();

        // when
        Pattern result = property.getFromReader(reader, convertErrorRecorder);

        // then
        assertThat(result, nullValue());
    }

    @Test
    void shouldExportPattern() {
        // given
        RegexProperty property = new RegexProperty("names.blacklist", "__\\w+");

        // when / then
        assertThat(property.toExportValue(Pattern.compile("m[0-9]{1,3}")), equalTo("m[0-9]{1,3}"));
        assertThat(property.toExportValue(Pattern.compile("\\$(\\w\\?)+")), equalTo("\\$(\\w\\?)+"));
    }

    @Test
    void shouldReturnWhetherPatternMatchesGivenValue() {
        // given
        RegexProperty property = new RegexProperty("validYears", "200[0-7]");
        SettingsManager settingsManager = mock(SettingsManager.class);
        given(settingsManager.getProperty(property)).willReturn(Pattern.compile("(19|20)[0-9]{2}"));

        // when / then
        assertThat(property.matches("1996", settingsManager), equalTo(true));
        assertThat(property.matches("2021", settingsManager), equalTo(true));
        assertThat(property.matches("1883", settingsManager), equalTo(false));
    }
}
