package ch.jalu.configme.beanmapper.leafvaluehandler;

import ch.jalu.configme.beanmapper.context.ExportContext;
import ch.jalu.configme.beanmapper.context.MappingContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The leaf value handler is used in {@link ch.jalu.configme.beanmapper.MapperImpl} to convert "simple" values that
 * were read from a resource to its desired type.
 * <p>
 * The bean mapper handles complex types such as maps and collections, and recursively calls the bean mapping process
 * accordingly. This class complements the mapper by halting this process and providing "leaf values" to be set into
 * Java beans.
 * <p>
 * The default implementation is {@link LeafValueHandlerImpl}. With the aid of {@link MapperLeafType} entries, it
 * provides conversions for the leaf types it supports. To implement additional conversions, implement your own
 * {@link MapperLeafType} implementations and add them to {@link LeafValueHandlerImpl}. Alternatively, you can create
 * your own implementation of this interface if you need more control over the conversion process.
 */
public interface LeafValueHandler {

    /** Marker object to signal that null is meant to be used as value. */
    Object RETURN_NULL = new Object();

    /**
     * Converts the given value to the target type (as defined by the mapping context), if supported. Otherwise,
     * null is returned. If a value is returned, its type is guaranteed to match the target type.
     *
     * @param value the value to convert (read from a property resource)
     * @param mappingContext mapping context with the target type
     * @return the converted value, or null if not applicable
     */
    @Nullable Object convert(@Nullable Object value, @NotNull MappingContext mappingContext);

    /**
     * Converts the value of a property to a value suitable for exporting. This method converts the opposite
     * way of {@link #convert}. Null is returned if this leaf value handler does not support the object's type.
     * If the leaf value handler determines that {@code null} should be used as export value, then {@link #RETURN_NULL}
     * is returned, which the caller needs to unwrap to {@code null}.
     *
     * @param value the value to convert
     * @param exportContext the export context (usually not needed)
     * @return the value suitable for exporting, or null if not applicable
     */
    @Nullable Object toExportValue(@Nullable Object value, @NotNull ExportContext exportContext);

    /**
     * Returns null if the object is {@link #RETURN_NULL}, otherwise the given object. Used to process return values
     * from methods like {@link #toExportValue}, where {@code null} means the instance doesn't support the value,
     * while {@link #RETURN_NULL} means null should be used as export value.
     *
     * @param object the object to potentially unwrap
     * @param <T> the object type
     * @return null, or the provided object
     */
    static <T> @Nullable T unwrapReturnNull(@Nullable T object) {
        return object == RETURN_NULL ? null : object;
    }
}
