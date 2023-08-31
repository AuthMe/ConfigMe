package ch.jalu.configme.properties.types;

import ch.jalu.configme.properties.convertresult.ConvertErrorRecorder;
import ch.jalu.typeresolver.TypeInfo;
import ch.jalu.typeresolver.primitives.PrimitiveType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Property type and mapper leaf type for boolean values.
 */
public class BooleanType extends PropertyAndLeafType<Boolean> {

    /** Instance of this class. Named {@code BOOLEAN} rather than {@code INSTANCE} so it can be statically imported. */
    public static final BooleanType BOOLEAN = new BooleanType();

    /**
     * Constructor. Use {@link BooleanType#BOOLEAN} for the standard behavior.
     */
    protected BooleanType() {
        super(Boolean.class);
    }

    @Override
    public @Nullable Boolean convert(@Nullable Object object, @NotNull ConvertErrorRecorder errorRecorder) {
        if (object instanceof Boolean) {
            return (Boolean) object;
        } else if (object instanceof String) {
            return convertFromString((String) object);
        }
        return null;
    }

    @Override
    public @NotNull Boolean toExportValue(@NotNull Boolean value) {
        return value;
    }

    @Override
    public boolean canConvertToType(@NotNull TypeInfo typeInformation) {
        Class<?> requiredClass = PrimitiveType.toReferenceType(typeInformation.toClass());
        return requiredClass != null && requiredClass.isAssignableFrom(Boolean.class);
    }

    /**
     * Converts the String value to its boolean value, if applicable.
     *
     * @param value the value to convert
     * @return boolean value represented by the string, or null if not applicable
     */
    protected @Nullable Boolean convertFromString(@NotNull String value) {
        // Note: Explicitly check for true/false because Boolean#parseBoolean returns false for
        // any value it doesn't recognize
        if ("true".equalsIgnoreCase(value)) {
            return true;
        } else if ("false".equalsIgnoreCase(value)) {
            return false;
        }
        return null;
    }
}
