package com.github.authme.configme.beanmapper.leafproperties;

import com.github.authme.configme.properties.Property;
import com.github.authme.configme.resource.PropertyResource;

import java.util.List;

/**
 * Property of a constant value that is a collection from a bean. This is a "temporary" property type
 * to communicate to the property resource during the export that we have a collection consisting of
 * the provided property objects.
 */
public final class ConstantCollectionProperty extends Property<Property<?>[]> {

    public ConstantCollectionProperty(String path, List<Property<?>> entryProperties) {
        super(path, entryProperties.toArray(new Property<?>[entryProperties.size()]));
    }

    @Override
    public Property<?>[] getFromResource(PropertyResource resource) {
        return getDefaultValue();
    }
}
