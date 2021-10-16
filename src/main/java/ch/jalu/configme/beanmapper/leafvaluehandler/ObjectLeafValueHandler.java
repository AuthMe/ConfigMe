package ch.jalu.configme.beanmapper.leafvaluehandler;

import org.jetbrains.annotations.Nullable;

/** Object handler. */
public class ObjectLeafValueHandler extends AbstractLeafValueHandler {

    @Override
    public @Nullable Object convert(@Nullable Class<?> clazz, @Nullable Object value) {
        if (clazz == Object.class) {
            return value;
        }
        return null;
    }

    @Override
    public @Nullable Object toExportValue(@Nullable Object value) {
        return null;
    }
}
