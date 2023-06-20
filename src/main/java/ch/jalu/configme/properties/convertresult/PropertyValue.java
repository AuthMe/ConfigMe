package ch.jalu.configme.properties.convertresult;

import ch.jalu.configme.properties.Property;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Return value of {@link Property#determineValue}. Wraps the value to associate with the property, along with a field
 * indicating whether the value in the resource is present and fully valid according to the property's type.
 * <p>
 * The value is the property's default value (for example, if no value is present in the resource), or it is a value
 * as constructed from the property resource. Note that {@link #isValidInResource} may be false even if the value is
 * based on the resource's data, namely when a resave of the resource is desired, usually because the data in the
 * resource was not fully valid. In any case, the value <b>must</b> always pass the check from
 * {@link Property#isValidValue}.
 *
 * @param <T> the value type wrapped by this instance
 */
public class PropertyValue<T> {

    private final T value;
    private final boolean isValidInResource;

    /**
     * Constructor.
     *
     * @param value the value associated with the property
     * @param isValidInResource true if the value in the resource was fully valid
     */
    public PropertyValue(/* PV */ T value, boolean isValidInResource) {
        this.value = value;
        this.isValidInResource = isValidInResource;
    }

    /**
     * Creates a new instance for the given value which indicates that it is fully valid in the resource.
     *
     * @param value the value to wrap
     * @param <T> the value type
     * @return property value with the given value and the valid flag set to true
     */
    public static <T> @NotNull PropertyValue<T> withValidValue(/* PV */ T value) {
        return new PropertyValue<>(value, true);
    }

    /**
     * Creates a new instance for the given value which indicates that its representation in the resource is NOT valid.
     *
     * @param value the value to wrap
     * @param <T> the value type
     * @return property value with the given value and the valid flag set to false
     */
    public static <T> @NotNull PropertyValue<T> withValueRequiringRewrite(/* PV */ T value) {
        return new PropertyValue<>(value, false);
    }

    /**
     * @return the value to associate with the property
     */
    public /* PV */ T getValue() {
        return value;
    }

    /**
     * Returns true if the data in the resource is fully valid for creating the value of the property.
     * False otherwise (i.e. if there is an issue with the value or part of it, or if the value is missing altogether).
     * Note that this flag does not refer to the value contained in this instance; rather, it specifies whether the data
     * in the property resource was present and fully valid at the property's path for the property's type.
     *
     * @return true if the data in the resource is fully valid for the property
     */
    public boolean isValidInResource() {
        return isValidInResource;
    }

    @Override
    public @NotNull String toString() {
        return "PropertyValue[valid=" + isValidInResource + ", value='" + value + "']";
    }
}
