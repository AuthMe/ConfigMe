package ch.jalu.configme.properties.types;

import javax.annotation.Nullable;

/**
 * Property type: provides methods for converting between property resource and a defined type
 * and allows to be used in generic structures such as an array property or map property.
 *
 * @param <T> type of the values the property type handles
 */
public interface PropertyType<T> {

    /**
     * Converts the given object (typically read from a property resource) to the given type, if possible.
     * Returns null otherwise.
     *
     * @param object the object to convert
     * @return the converted value, or null
     */
    @Nullable
    T convert(@Nullable Object object);

    /**
     * Converts the given value to its export value. (Converts in the opposite way of {@link #convert}.)
     *
     * @param value the value to convert
     * @return the value to use in the property export
     */
    Object toExportValue(T value);

}
