package ch.jalu.configme.properties;

import ch.jalu.configme.configurationdata.PropertyValue;
import ch.jalu.configme.resource.PropertyReader;

import java.util.Optional;

/**
 * Property which may be empty.
 * <p>
 * Wraps another property with an {@link Optional}: if a property is not present in the property resource,
 * {@link Optional#empty} is returned.
 */
public class OptionalProperty<T> implements Property<Optional<T>> {

    private final Property<T> baseProperty;
    private final Optional<T> defaultValue;

    public OptionalProperty(Property<T> baseProperty) {
        this.baseProperty = baseProperty;
        this.defaultValue = Optional.empty();
    }

    public OptionalProperty(Property<T> baseProperty, T defaultValue) {
        this.baseProperty = baseProperty;
        this.defaultValue = Optional.of(defaultValue);
    }

    @Override
    public String getPath() {
        return baseProperty.getPath();
    }

    @Override
    public PropertyValue<Optional<T>> determineValue(PropertyReader reader) {
        PropertyValue<T> basePropertyValue = baseProperty.determineValue(reader);
        Optional<T> value = basePropertyValue.isValidInResource()
            ? Optional.ofNullable(basePropertyValue.getValue())
            : Optional.empty();

        // Propagate the false "valid" property if the reader has a value at the base property's path
        // and the base property says it's invalid -> triggers a rewrite to get rid of the invalid value.
        boolean isWrongInResource = !basePropertyValue.isValidInResource() && reader.contains(baseProperty.getPath());
        return isWrongInResource
            ? PropertyValue.withValueRequiringRewrite(value)
            : PropertyValue.withValidValue(value);
    }

    @Override
    public Optional<T> getDefaultValue() {
        return defaultValue;
    }

    @Override
    public boolean isValidValue(Optional<T> value) {
        if (value == null) {
            return false;
        }
        return value.map(baseProperty::isValidValue).orElse(true);
    }

    @Override
    public Object toExportValue(Optional<T> value) {
        return value.map(baseProperty::toExportValue).orElse(null);
    }
}
