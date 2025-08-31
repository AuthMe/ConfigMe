package ch.jalu.configme.beanmapper;

import ch.jalu.configme.properties.convertresult.ConvertErrorRecorder;
import ch.jalu.typeresolver.TypeInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Maps the values of a property reader to Java classes (referred to as beans). See the Javadoc on the default
 * implementation, {@link MapperImpl}, for more details.
 */
public interface Mapper {

    /**
     * Creates an object of the given type from the given value. Returns null if the
     * conversion is not possible. The value usually stems from a property resource and
     * is a Map of values.
     *
     * @param value the value to convert (typically a Map)
     * @param targetType the required type
     * @param errorRecorder error recorder to register errors even if a valid value is returned
     * @return object of the given type, or null if not possible
     */
    @Nullable Object convertToBean(@Nullable Object value, @NotNull TypeInfo targetType,
                                   @NotNull ConvertErrorRecorder errorRecorder);

    /**
     * Converts the given value to an object of the given class, if possible. Returns null otherwise.
     * This is a convenience method as typed alternative to
     * {@link #convertToBean(Object, TypeInfo, ConvertErrorRecorder)}.
     *
     * @param value the value to convert (typically a Map)
     * @param clazz the required class
     * @param errorRecorder error recorder to register errors even if a valid value is returned
     * @param <T> the class type
     * @return object of the given type, or null if not possible
     */
    @SuppressWarnings("unchecked")
    default <T> @Nullable T convertToBean(@Nullable Object value, @NotNull Class<T> clazz,
                                          @NotNull ConvertErrorRecorder errorRecorder) {
        return (T) convertToBean(value, new TypeInfo(clazz), errorRecorder);
    }

    /**
     * Converts a complex type such as a bean to simple types suitable for exporting. This method
     * typically returns a Map of values, or simple types like String / Number for scalar values.
     * Used by {@link ch.jalu.configme.properties.types.BeanPropertyType#toExportValue}.
     *
     * @param object the object to convert to its export value
     * @return export value to use
     */
    @Nullable Object toExportValue(@NotNull Object object);

}
