package ch.jalu.configme.beanmapper.leafproperties;

import ch.jalu.configme.properties.Property;
import ch.jalu.configme.resource.PropertyResource;

/**
 * Property implementation that always returns the provided value.
 *
 * @param <T> the property type
 */
final class ConstantValueProperty<T> extends Property<T> {

    /**
     * Constructor.
     *
     * @param path the path of the property
     * @param value the value to <i>always</i> return
     */
    ConstantValueProperty(String path, T value) {
        super(path, value);
    }

    @Override
    protected T getFromResource(PropertyResource resource) {
        // default value is the actual value we need (see constructor)
        return getDefaultValue();
    }
}
