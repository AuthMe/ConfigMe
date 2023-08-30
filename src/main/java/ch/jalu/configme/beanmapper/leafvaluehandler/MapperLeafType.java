package ch.jalu.configme.beanmapper.leafvaluehandler;

import ch.jalu.configme.properties.convertresult.ConvertErrorRecorder;
import ch.jalu.typeresolver.TypeInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A leaf type represents a "simple" type like a String or a number. Used by the bean mapper's
 * {@link LeafValueHandlerImpl}, a leaf type provides a simple conversion for a specific type.
 *
 * @see LeafValueHandler
 */
public interface MapperLeafType {

    /**
     * Converts the given value to the specified target type, if possible. Returns null otherwise.
     * This method <b>must</b> either return {@code null} or an object of the given target type.
     *
     * @param value the value to convert
     * @param targetType the required type
     * @param errorRecorder error recorder to register errors even if a valid value is returned
     * @return value of the given type, or null if not applicable
     */
    @Nullable Object convert(@Nullable Object value, @NotNull TypeInfo targetType,
                             @NotNull ConvertErrorRecorder errorRecorder);

    /**
     * Converts the given value to a type suitable for exporting. Used by the mapper
     * when {@link ch.jalu.configme.beanmapper.MapperImpl#toExportValue(Object)} is called.
     * Returns null if the leaf value handler cannot handle the value.
     * <p>
     * Return {@link ch.jalu.configme.beanmapper.MapperImpl#RETURN_NULL} to signal that null should be used
     * as the export value (returning {@code null} itself means this leaf value handler cannot handle it).
     *
     * @param value the value to convert to an export value, if possible
     * @return value to use in the export, or null if not applicable
     */
    @Nullable Object toExportValueIfApplicable(@Nullable Object value);

}
