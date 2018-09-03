package ch.jalu.configme.beanmapper;

import ch.jalu.configme.resource.PropertyReader;
import ch.jalu.configme.utils.TypeInformation;

import javax.annotation.Nullable;

/**
 * Creates JavaBeans based on the values coming from a property reader. See the JavaDoc on the default implementation,
 * {@link MapperImpl}, for more details.
 */
public interface Mapper {

    /**
     * Creates an object of the given type based on the reader's values at the given path. Returns null if the
     * conversion is not possible.
     *
     * @param reader the reader to get values from
     * @param path the path to use
     * @param typeInformation the required type
     * @return object of the given type, or null if not possible
     */
    @Nullable
    Object convertToBean(PropertyReader reader, String path, TypeInformation typeInformation);

    /**
     * Converts the values of the given reader at the provided path to an object of the given class, if possible.
     * Returns null otherwise. This is a convenience method as typed alternative to
     * {@link #convertToBean(PropertyReader, String, TypeInformation)}.
     *
     * @param reader the reader to get values from
     * @param path the path to use
     * @param clazz the required class
     * @param <T> the class type
     * @return object of the given type, or null if not possible
     */
    @Nullable
    @SuppressWarnings("unchecked")
    default <T> T convertToBean(PropertyReader reader, String path, Class<T> clazz) {
        return (T) convertToBean(reader, path, new TypeInformation(clazz));
    }

    /**
     * Converts a complex type such as a JavaBean object to simple types suitable for exporting. This method
     * typically returns a Map of values, or simple types like String / Number for scalar values.
     * Used in the {@link ch.jalu.configme.properties.BeanProperty#toExportValue} method.
     *
     * @param object the object to get to its export value
     * @return export value to use
     */
    @Nullable
    Object toExportValue(@Nullable Object object);

}
