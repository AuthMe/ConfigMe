package ch.jalu.configme.beanmapper.leafvaluehandler;

import org.jetbrains.annotations.Nullable;

/** String handler. */
public class StringLeafValueHandler extends AbstractLeafValueHandler {

    @Override
    public @Nullable Object convert(@Nullable Class<?> clazz, @Nullable Object value) {
        if (clazz == String.class
            && (value instanceof String || value instanceof Number || value instanceof Boolean)) {
            return value.toString();
        }
        return null;
    }

    @Override
    public @Nullable Object toExportValue(@Nullable Object value) {
        return (value instanceof String) ? value : null;
    }
}
