package ch.jalu.configme.neo.beanmapper;

import ch.jalu.configme.neo.resource.PropertyReader;

import javax.annotation.Nullable;

public interface Mapper {

    @Nullable
    <T> T convertToBean(PropertyReader reader, String path, Class<T> clazz); // TODO: change to typeinfo?

    @Nullable
    Object toExportValue(@Nullable Object object);

}
