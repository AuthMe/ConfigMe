package ch.jalu.configme.neo.properties;

import ch.jalu.configme.neo.resource.PropertyReader;

import java.util.Optional;

/**
 * Property which may be empty.
 * <p>
 * Wraps another property with an {@link Optional}: if a property is not present in the property resource,
 * {@link Optional#empty} is returned.
 */
public class OptionalProperty<T> extends BaseProperty<Optional<T>> {

    private final BaseProperty<T> baseProperty;

    /**
     * Constructor.
     *
     * @param baseProperty the property to wrap
     */
    public OptionalProperty(BaseProperty<T> baseProperty) {
        super(baseProperty.getPath(), Optional.empty());
        this.baseProperty = baseProperty;
    }

    @Override
    protected Optional<T> getFromResource(PropertyReader reader) {
        return Optional.ofNullable(baseProperty.getFromResource(reader));
    }

    @Override
    public boolean isPresent(PropertyReader reader) {
        // getFromResource will never return null, and always returning true here prevents this
        // optional(!) property from triggering migrations
        return true;
    }

    @Override
    public Object toExportRepresentation(Optional<T> value) {
        return value.isPresent()
            ? baseProperty.toExportRepresentation(value.get())
            : null;
    }

    /**
     * Returns the underlying property used to retrieve the value of the optional.
     *
     * @return the base property
     */
    public BaseProperty<? extends T> getBaseProperty() {
        return baseProperty;
    }
}
