package ch.jalu.configme.beanmapper;

import ch.jalu.configme.resource.PropertyReader;
import ch.jalu.configme.utils.TypeInformation;

import javax.annotation.Nullable;

public interface Mapper {

    @Nullable
    Object convertToBean(PropertyReader reader, String path, TypeInformation typeInformation);

    @Nullable
    @SuppressWarnings("unchecked")
    default <T> T convertToBean(PropertyReader reader, String path, Class<T> clazz) {
        return (T) convertToBean(reader, path, new TypeInformation(clazz));
    }

    @Nullable
    Object toExportValue(@Nullable Object object);

}
