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
}
