package ch.jalu.configme.neo.beanmapper;

import javax.annotation.Nullable;

public interface ValueTransformer {

    @Nullable
    Object value(Class<?> clazz, Object value);

    @Nullable
    Object toExportValue(Object value);

}
