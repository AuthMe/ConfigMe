package ch.jalu.configme.properties;

import ch.jalu.configme.SettingsManager;
import ch.jalu.configme.properties.types.RegexType;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Property whose value is a regex pattern.
 */
public class RegexProperty extends TypeBasedProperty<Pattern> {

    /**
     * Constructor.
     *
     * @param path the path of the property
     * @param defaultValue the default value of the property
     */
    public RegexProperty(@NotNull String path, @NotNull Pattern defaultValue) {
        super(path, defaultValue, RegexType.REGEX);
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
