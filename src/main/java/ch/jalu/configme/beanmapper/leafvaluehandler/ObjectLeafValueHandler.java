package ch.jalu.configme.beanmapper.leafvaluehandler;

import org.jetbrains.annotations.Nullable;

/** Object handler. */
public class ObjectLeafValueHandler extends AbstractLeafValueHandler {

    @Override
    public @Nullable Object convert(Class<?> clazz, Object value) {
        if (clazz == Object.class) {
            return value;
        }
        return null;
    }

    @Override
    public Object toExportValue(Object value) {
        return null;
    }
}
