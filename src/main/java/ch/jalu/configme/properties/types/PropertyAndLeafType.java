package ch.jalu.configme.properties.types;

import ch.jalu.configme.beanmapper.leafvaluehandler.MapperLeafType;
import ch.jalu.configme.properties.convertresult.ConvertErrorRecorder;
import ch.jalu.typeresolver.TypeInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

    protected boolean canConvertToType(@NotNull TypeInfo type) {
        return type.isAssignableFrom(clazz);
    }

    public final @NotNull Class<T> getType() {
        return clazz;
    }
}
