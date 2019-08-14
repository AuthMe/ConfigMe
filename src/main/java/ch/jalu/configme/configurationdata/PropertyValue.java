package ch.jalu.configme.configurationdata;

import ch.jalu.configme.properties.Property;

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
    protected PropertyValue(T value, boolean isValidInResource) {
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
    public static <T> PropertyValue<T> withValidValue(T value) {
        return new PropertyValue<>(value, true);
    }

    /**
     * Creates a new instance for the given value which indicates that its representation in the resource is NOT valid.
     *
     * @param value the value to wrap
     * @param <T> the value type
     * @return property value with the given value and the valid flag set to false
     */
    public static <T> PropertyValue<T> withValueRequiringRewrite(T value) {
        return new PropertyValue<>(value, false);
    }

    /**
     * Creates a new instance with the given value if the provided property declares it as valid, otherwise uses the
     * property's default value, setting the valid flag to false.
     *
     * @param value the value to check and potentially use
     * @param property the property to check the value with and whose default value should be used as fallback
     * @param <T> the property type
     * @return instance with the appropriate value and validity flag
     */
    public static <T> PropertyValue<T> defaultIfInvalid(T value, Property<T> property) {
        return property.isValidValue(value)
            ? withValidValue(value)
            : withValueRequiringRewrite(property.getDefaultValue());
    }

    /**
     * @return the value to associate with the property
     */
    public T getValue() {
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
    public String toString() {
        return "PropertyValue[valid=" + isValidInResource + ", value='" + value + "']";
    }
}
