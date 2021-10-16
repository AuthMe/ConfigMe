package ch.jalu.configme.beanmapper.leafvaluehandler;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** Enum handler. */
public class EnumLeafValueHandler extends AbstractLeafValueHandler {

    @Override
    public @Nullable Object convert(@NotNull Class<?> clazz, Object value) {
        if (value instanceof String && Enum.class.isAssignableFrom(clazz)) {
            String givenText = (String) value;
            for (Enum e : (Enum[]) clazz.getEnumConstants()) {
                if (e.name().equalsIgnoreCase(givenText)) {
                    return e;
                }
            }
        }
        return null;
    }

    @Override
    public Object toExportValue(Object value) {
        if (value instanceof Enum<?>) {
            return ((Enum<?>) value).name();
        }
        return null;
    }
}
