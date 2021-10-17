package ch.jalu.configme.properties;

import ch.jalu.configme.SettingsManager;
import ch.jalu.configme.properties.convertresult.ConvertErrorRecorder;
import ch.jalu.configme.resource.PropertyReader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Property whose value is a regex pattern.
 */
public class RegexProperty extends BaseProperty<Pattern> {

    /**
     * Constructor.
     *
     * @param path the path of the property
     * @param defaultValue the default value of the property
     */
    public RegexProperty(@NotNull String path, @NotNull Pattern defaultValue) {
        super(path, defaultValue);
    }

    /**
     * Constructor.
     *
     * @param path the path of the property
     * @param defaultRegexValue the default value of the property
     */
    public RegexProperty(@NotNull String path, @NotNull String defaultRegexValue) {
        this(path, Pattern.compile(defaultRegexValue));
    }

    @Override
    protected @Nullable Pattern getFromReader(@NotNull PropertyReader reader, @NotNull ConvertErrorRecorder errorRecorder) {
        String pattern = reader.getString(getPath());
        if (pattern != null) {
            try {
                return Pattern.compile(pattern);
            } catch (PatternSyntaxException ignored) {
            }
        }
        return null;
    }

    @Override
    public @NotNull Object toExportValue(@NotNull Pattern value) {
        return value.pattern();
    }

    /**
     * Convenience method to evaluate whether the pattern set for this property matches the provided {@code value}.
     *
     * @param value the value to check whether it conforms to the configured pattern
     * @param settingsManager settings manager with which the configured pattern is retrieved
     * @return true if the value matches the pattern, false otherwise
     */
    public boolean matches(@NotNull String value, @NotNull SettingsManager settingsManager) {
        Matcher matcher = settingsManager.getProperty(this).matcher(value);
        return matcher.matches();
    }
}
