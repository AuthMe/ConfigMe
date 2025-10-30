package ch.jalu.configme.properties.types;

import ch.jalu.configme.properties.convertresult.ConvertErrorRecorder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.Temporal;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;

/**
 * Property type and mapper leaf type for temporal values.
 *
 * @param <T> the expected temporal type
 */
public class TemporalType<T extends Temporal> extends PropertyAndLeafType<T> {

    /** Local Date temporal type. */
    public static final TemporalType<LocalDate> LOCAL_DATE = new TemporalType<>(
        LocalDate.class, Arrays.asList("yyyy-MM-dd", "dd.MM.yyyy", "MM/dd/yyyy"), LocalDate::parse);

    private final List<String> supportedFormats;
    private final BiFunction<String, DateTimeFormatter, T> temporalParser;

    protected TemporalType(Class<T> clazz, List<String> supportedFormats,
                           BiFunction<String, DateTimeFormatter, T> defaultParser) {
        super(clazz);
        this.supportedFormats = supportedFormats;
        this.temporalParser = defaultParser;
    }

    @Override
    public @Nullable T convert(@Nullable Object object, @NotNull ConvertErrorRecorder errorRecorder) {
        if (!(object instanceof String)) {
            return null;
        }
        return convertToTemporalType((String) object);
    }

    @Override
    public @Nullable Object toExportValue(@NotNull T value) {
        if (value instanceof LocalDate) {
            return LocalDate.from(value).toString();
        }
        return value.toString();
    }

    private T convertToTemporalType(String temporalText) {
        for (String format: this.supportedFormats) {
            try {
                return this.temporalParser.apply(temporalText, DateTimeFormatter.ofPattern(format));
            } catch (DateTimeParseException e) {
                // try next format
            }
        }
        return null;
    }
}
