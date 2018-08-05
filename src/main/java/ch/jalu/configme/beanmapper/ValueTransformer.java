package ch.jalu.configme.beanmapper;

import javax.annotation.Nullable;

public interface ValueTransformer {

    @Nullable
    Object value(Class<?> clazz, @Nullable Object value);

    @Nullable
    Object toExportValue(@Nullable Object value);

}
