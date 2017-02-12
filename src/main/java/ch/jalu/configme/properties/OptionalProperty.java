package ch.jalu.configme.properties;

import ch.jalu.configme.resource.PropertyResource;

import java.util.Optional;

/**
 * Property which may be empty.
 * <p>
 * Wraps another property with an {@link Optional}: if a property is not present in the property resource,
 * {@link Optional#empty} is returned.
 */
public class OptionalProperty<T> extends Property<Optional<T>> {

    private final Property<? extends T> baseProperty;

    /**
     * Constructor.
     *
     * @param baseProperty the property to wrap
     */
    public OptionalProperty(Property<? extends T> baseProperty) {
        super(baseProperty.getPath(), Optional.empty());
        this.baseProperty = baseProperty;
    }

    @Override
    protected Optional<T> getFromResource(PropertyResource resource) {
        return Optional.ofNullable(baseProperty.getFromResource(resource));
    }

    @Override
    public boolean isPresent(PropertyResource resource) {
        // getFromResource will never return null (see above), and always returning true here prevents this
        // optional(!) property from triggering migrations
        return true;
    }

    /**
     * Returns the underlying property used to retrieve the value of the optional.
     *
     * @return the base property
     */
    public Property<? extends T> getBaseProperty() {
        return baseProperty;
    }
}
