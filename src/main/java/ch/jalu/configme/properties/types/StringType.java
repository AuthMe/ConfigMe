package ch.jalu.configme.properties.types;

import ch.jalu.configme.properties.convertresult.ConvertErrorRecorder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

/**
 * Property type and mapper leaf type for strings.
 */
public class StringType extends PropertyAndLeafType<String> {

    /** Default string type. */
    public static final StringType STRING = new StringType();

    /** Lowercase string type. */
    public static final StringType STRING_LOWER_CASE = new StringType() {
        @Override
        protected @NotNull String transformToString(@NotNull Object object) {
            return object.toString().toLowerCase(Locale.ROOT);
        }
    };

    /**
     * Constructor.
     */
    protected StringType() {
        super(String.class);
    }

    @Override
    public @Nullable String convert(@Nullable Object object, @NotNull ConvertErrorRecorder errorRecorder) {
        return object == null ? null : transformToString(object);
    }

    @Override
    public @NotNull String toExportValue(@NotNull String value) {
        return value;
    }

    /**
     * Converts the given object to a string.
     *
     * @param object the object to convert
     * @return the converted object
     */
    protected @NotNull String transformToString(@NotNull Object object) {
        return object.toString();
    }
}
