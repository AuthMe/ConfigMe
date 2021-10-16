package ch.jalu.configme.beanmapper.leafvaluehandler;

import org.jetbrains.annotations.Nullable;

/** Boolean leaf value handler. */
public class BooleanLeafValueHandler extends AbstractLeafValueHandler {

    @Override
    public @Nullable Object convert(@Nullable Class<?> clazz, @Nullable Object value) {
        if ((clazz == boolean.class || clazz == Boolean.class) && value instanceof Boolean) {
            return value;
        }
        return null;
    }

    @Override
    public @Nullable Object toExportValue(@Nullable Object value) {
        return (value instanceof Boolean) ? value : null;
    }
}
