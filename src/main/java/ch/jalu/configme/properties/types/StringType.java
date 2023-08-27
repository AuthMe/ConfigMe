package ch.jalu.configme.properties.types;

import ch.jalu.configme.properties.convertresult.ConvertErrorRecorder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public class StringType extends PropertyAndLeafType<String> {

    public static final StringType STRING = new StringType();

    public static final StringType STRING_LOWER_CASE = new StringType() {
        @Override
        protected @NotNull String transformToString(@NotNull Object object) {
            return object.toString().toLowerCase(Locale.ROOT);
        }
    };

    protected StringType() {
        super(String.class);
    }

    @Override
    public @Nullable String convert(@Nullable Object object, @NotNull ConvertErrorRecorder errorRecorder) {
        return object == null ? null : transformToString(object);
    }

    @Override
    public @Nullable Object toExportValue(@Nullable String value) {
        return value;
    }

    protected @NotNull String transformToString(@NotNull Object object) {
        return object.toString();
    }
}
