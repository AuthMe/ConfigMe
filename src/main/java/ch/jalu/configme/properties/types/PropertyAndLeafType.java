package ch.jalu.configme.properties.types;

import ch.jalu.configme.beanmapper.leafvaluehandler.MapperLeafType;
import ch.jalu.configme.properties.convertresult.ConvertErrorRecorder;
import ch.jalu.typeresolver.TypeInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Abstract class to implement {@link PropertyType} and {@link MapperLeafType} within the same class.
 * <p>
 * Both implemented interfaces are independent of each other, but for many basic types, we want to make use of the same
 * conversion logic and behavior. This class facilitates such implementations. This class should not be used for
 * types where the conversion logic is not suitable as a property type <b>and</b> as a mapper leaf type—for
 * instance, {@link EnumPropertyType} is a property type implementation for properties where the enum class is
 * specifically defined, whereas {@link ch.jalu.configme.beanmapper.leafvaluehandler.EnumLeafType EnumLeafType} is the
 * equivalent mapper leaf type which generically handles all enums.
 *
 * @param <T> the type this instance produces
 */
public abstract class PropertyAndLeafType<T> implements PropertyType<T>, MapperLeafType {

    private final Class<T> clazz;

    /**
     * Constructor.
     *
     * @param clazz the class this type implementation produces
     */
    public PropertyAndLeafType(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public @Nullable Object convert(@Nullable Object value, @NotNull TypeInfo targetType,
                                    @NotNull ConvertErrorRecorder errorRecorder) {
        if (canConvertToType(targetType)) {
            return convert(value, errorRecorder);
        }
        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public @Nullable Object toExportValueIfApplicable(@Nullable Object value) {
        if (clazz.isInstance(value)) {
            return toExportValue((T) value);
        }
        return null;
    }

    /**
     * Specifies whether this object can convert to the given type. Used by
     * {@link #convert(Object, TypeInfo, ConvertErrorRecorder)}.
     *
     * @param type the target type
     * @return true if this object can convert to the given type, false otherwise
     */
    protected boolean canConvertToType(@NotNull TypeInfo type) {
        return type.isAssignableFrom(clazz);
    }

    /**
     * @return the class of the values this type converts to
     */
    public final @NotNull Class<T> getType() {
        return clazz;
    }
}
