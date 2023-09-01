package ch.jalu.configme.properties.types;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import ch.jalu.configme.properties.convertresult.ConvertErrorRecorder;

/**
 * Property type and mapper leaf type for regex.
 */
public class RegexType extends PropertyAndLeafType<Pattern> {
    
    /** Default regex type. */
    public static final RegexType REGEX = new RegexType();

    /**
     * Constructor. Use {@link RegexType#REGEX} for the standard behavior.
     */
    protected RegexType() {
        super(Pattern.class);
    }

    @Override
    public @Nullable Pattern convert(@Nullable Object object, @NotNull ConvertErrorRecorder errorRecorder) {
        if (object instanceof String) {
            String pattern = (String) object;
            try {
                return Pattern.compile(pattern);
            } catch (PatternSyntaxException ignored) {
            }
        }
        return null;
    }

    @Override
    public @Nullable Object toExportValue(@NotNull Pattern value) {
        return value.pattern();
    }

}
