package ch.jalu.configme.properties.types;

import ch.jalu.configme.properties.convertresult.ConvertErrorRecorder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
    /** Local Time temporal type. */
    public static final TemporalType<LocalTime> LOCAL_TIME = new TemporalType<>(
        LocalTime.class, Arrays.asList("HH:mm:ss", "HH.mm", "HH:mm"), LocalTime::parse);
    /** Local Date Time temporal type. */
    public static final TemporalType<LocalDateTime> LOCAL_DATE_TIME = new TemporalType<>(
        LocalDateTime.class, Arrays.asList("yyyy-MM-dd HH:mm:ss", "dd.MM.yyyy HH:mm:ss", "MM/dd/yyyy HH:mm:ss"),
        LocalDateTime::parse);

    private final List<String> supportedFormats;
    private final BiFunction<String, DateTimeFormatter, T> temporalParser;
    private String defaultExportFormat;

    /**
     * Constructor.
     *
     * @param clazz the temporal type this type should convert to
     * @param supportedFormats list of conversion formats supported for this type
     * @param defaultParser function which can parse a value to this type in one of the given supportedFormats
     */
    public TemporalType(@NotNull Class<T> clazz, @NotNull List<String> supportedFormats,
                        @NotNull BiFunction<String, DateTimeFormatter, T> defaultParser) {
        super(clazz);
        if (supportedFormats.isEmpty()) {
            throw new IllegalArgumentException("At least one supported format must be provided.");
        }
        this.supportedFormats = supportedFormats;
        this.temporalParser = defaultParser;
        this.defaultExportFormat = supportedFormats.get(0);
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
        return DateTimeFormatter.ofPattern(this.defaultExportFormat).format(value);
    }

    private T convertToTemporalType(String temporalText) {
        for (String format: this.supportedFormats) {
            try {
                T parsedValue = this.temporalParser.apply(temporalText, DateTimeFormatter.ofPattern(format));
                this.defaultExportFormat = format;
                return parsedValue;
            } catch (DateTimeParseException e) {
                // try next format
            }
        }
        return null;
    }
}
