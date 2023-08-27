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
 * Java beans, indicating that the mapper doesn't need to visit a type.
 * <p>
 * The default implementation is {@link LeafValueHandlerImpl}. With the aid of {@link MapperLeafType} entries, it
 * provides leaf type conversions when applicable. To implement additional conversions, you can implement your own
 * {@link MapperLeafType} implementation and add it to {@link LeafValueHandlerImpl}. Alternatively, a custom
 * implementation of this interface can be created if more control is needed.
 */
public interface LeafValueHandler {

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
     * way of {@link #convert}. Null is returned if the value cannot be converted as a leaf type.
     *
     * @param value the value to convert
     * @param exportContext the export context (usually not needed)
     * @return the value suitable for exporting, or null if not applicable
     */
    @Nullable Object toExportValue(@Nullable Object value, @NotNull ExportContext exportContext);

}
