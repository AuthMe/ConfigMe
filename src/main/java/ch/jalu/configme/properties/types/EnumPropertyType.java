package ch.jalu.configme.properties.types;

import ch.jalu.configme.properties.convertresult.ConvertErrorRecorder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EnumPropertyType<E extends Enum<E>> implements PropertyType<E> {

    private Class<E> enumType;

    public EnumPropertyType(@NotNull Class<E> enumType) {
        this.enumType = enumType;
    }

    public static <E extends Enum<E>> @NotNull EnumPropertyType<E> of(@NotNull Class<E> type) {
        return new EnumPropertyType<>(type);
    }

    @Override
    public @Nullable E convert(@Nullable Object object, @NotNull ConvertErrorRecorder errorRecorder) {
        // If object is enum, then return this object, casting to E
        if (enumType.isInstance(object)) {
            return (E) object;
        }

        // If object is not string, then we return null, because else we cant find an enum
        if (!(object instanceof String)) {
            return null;
        }

        String name = (String) object;
        for (E entry : enumType.getEnumConstants()) {
            if (entry.name().equalsIgnoreCase(name)) {
                return entry;
            }
        }

        return null;
    }

    @Override
    public @NotNull Object toExportValue(@NotNull E value) {
        return value.name();
    }
}
