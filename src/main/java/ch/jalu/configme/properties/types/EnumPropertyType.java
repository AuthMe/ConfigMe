package ch.jalu.configme.properties.types;

import ch.jalu.configme.properties.convertresult.ConvertErrorRecorder;
import ch.jalu.typeresolver.EnumUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Property type for an enum type.
 *
 * @param <E> the enum type
 */
public class EnumPropertyType<E extends Enum<E>> implements PropertyType<E> {

    private final Class<E> enumType;

    /**
     * Constructor. You can also create instances with {@link EnumPropertyType#of}.
     *
     * @param enumType the enum type this type should convert to
     */
    public EnumPropertyType(@NotNull Class<E> enumType) {
        this.enumType = enumType;
    }

    public static <E extends Enum<E>> @NotNull EnumPropertyType<E> of(@NotNull Class<E> type) {
        return new EnumPropertyType<>(type);
    }

    @Override
    @SuppressWarnings("unchecked")
    public @Nullable E convert(@Nullable Object object, @NotNull ConvertErrorRecorder errorRecorder) {
        if (object instanceof String) {
            return EnumUtils.tryValueOfCaseInsensitive(enumType, (String) object).orElse(null);
        } else if (enumType.isInstance(object)) {
            return (E) object;
        }
        return null;
    }

    @Override
    public @NotNull Object toExportValue(@NotNull E value) {
        return value.name();
    }

    public final @NotNull Class<E> getType() {
        return enumType;
    }
}
