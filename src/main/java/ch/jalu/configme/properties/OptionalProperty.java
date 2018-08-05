package ch.jalu.configme.properties;

import ch.jalu.configme.resource.PropertyReader;

import java.util.Optional;

/**
 * Property which may be empty.
 * <p>
 * Wraps another property with an {@link Optional}: if a property is not present in the property resource,
 * {@link Optional#empty} is returned.
 */
public class OptionalProperty<T> extends BaseProperty<Optional<T>> {

    private final Property<T> baseProperty;

    public OptionalProperty(Property<T> baseProperty) {
        super(baseProperty.getPath(), Optional.empty());
        this.baseProperty = baseProperty;
    }

    public OptionalProperty(Property<T> baseProperty, T defaultValue) {
        super(baseProperty.getPath(), Optional.of(defaultValue));
        this.baseProperty = baseProperty;
    }

    @Override
    protected Optional<T> getFromResource(PropertyReader reader) {
        return baseProperty.isPresent(reader)
            ? Optional.of(baseProperty.determineValue(reader))
            : Optional.empty();
    }

    @Override
    public boolean isPresent(PropertyReader reader) {
        // getFromResource will never return null, and always returning true here prevents this
        // optional(!) property from triggering migrations
        return true;
    }

    @Override
    public Object toExportValue(Optional<T> value) {
        return value.map(baseProperty::toExportValue).orElse(null);
    }
}
