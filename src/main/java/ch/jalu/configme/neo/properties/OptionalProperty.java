package ch.jalu.configme.neo.properties;

import ch.jalu.configme.neo.propertytype.OptionalType;
import ch.jalu.configme.neo.propertytype.PropertyType;

import java.util.Optional;

/**
 * Property which may be empty.
 * <p>
 * Wraps another property with an {@link Optional}: if a property is not present in the property resource,
 * {@link Optional#empty} is returned.
 */
public class OptionalProperty<T> extends BaseProperty<Optional<T>> {

    public OptionalProperty(String path, PropertyType<T> basePropertyType) {
        super(path, Optional.empty(), new OptionalType<>(basePropertyType));
    }

    public OptionalProperty(String path, T defaultValue, PropertyType<T> basePropertyType) {
        super(path, Optional.of(defaultValue), new OptionalType<>(basePropertyType));
    }
}
