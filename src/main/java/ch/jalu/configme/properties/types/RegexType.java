package ch.jalu.configme.properties.types;

import ch.jalu.configme.properties.convertresult.ConvertErrorRecorder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Property type and mapper leaf type for regex.
 */
public class RegexType extends PropertyAndLeafType<Pattern> {
    
    /** Default regex type. */
    public static final RegexType REGEX = new RegexType();

    /** Case-insensitive regex type. */
    public static final RegexType REGEX_CASE_INSENSITIVE = new RegexType() {
        @Override
        protected @NotNull Pattern compileToPattern(@NotNull String regex) {
            return Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        }
    };

    /**
     * Constructor. Use {@link RegexType#REGEX} for the standard behavior.
     */
    protected RegexType() {
        super(Pattern.class);
    }

    @Override
    public @Nullable Pattern convert(@Nullable Object object, @NotNull ConvertErrorRecorder errorRecorder) {
        if (object instanceof String) {
            String regex = (String) object;
            try {
                return compileToPattern(regex);
            } catch (PatternSyntaxException ignored) {
            }
        }
        return null;
    }

    @Override
    public @Nullable Object toExportValue(@NotNull Pattern value) {
        return value.pattern();
    }

    /**
     * Compiles the given string to a pattern object.
     *
     * @param regex the string to compile
     * @return the pattern object
     */
    protected @NotNull Pattern compileToPattern(@NotNull String regex) {
        return Pattern.compile(regex);
    }
}
