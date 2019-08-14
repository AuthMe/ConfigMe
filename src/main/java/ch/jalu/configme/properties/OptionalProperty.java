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
    protected Optional<T> getFromReader(PropertyReader reader) {
        return isBasePropertyPresent(reader)
            ? Optional.ofNullable(baseProperty.determineValue(reader).getValue())
            : Optional.empty();
    }

    protected boolean isBasePropertyPresent(PropertyReader reader) {
        if (baseProperty instanceof BaseProperty<?>) {
            return ((BaseProperty<?>) baseProperty).getFromReader(reader) != null;
        }
        return reader.contains(baseProperty.getPath());
    }

    @Override
    public Object toExportValue(Optional<T> value) {
        return value.map(baseProperty::toExportValue).orElse(null);
    }
}
