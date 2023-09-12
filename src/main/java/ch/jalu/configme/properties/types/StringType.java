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

    /**
     * @return array property type whose elements are managed by {@code this} String type
     */
    public @NotNull ArrayPropertyType<String> arrayType() {
        return new ArrayPropertyType<>(this, String[]::new);
    }

    /**
     * Creates an inline array property type with the given separator.
     * See {@link InlineArrayPropertyType} for more details.
     *
     * @param separator the sequence that acts as separator for multiple entries
     * @return inline array type with {@code this} type and the given separator
     */
    public @NotNull InlineArrayPropertyType<String> inlineArrayType(@NotNull String separator) {
        return new InlineArrayPropertyType<>(this, separator, false, String[]::new);
    }
}
