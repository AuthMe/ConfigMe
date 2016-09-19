package com.github.authme.configme.beanmapper;

import com.github.authme.configme.properties.Property;
import com.github.authme.configme.resource.PropertyResource;

/**
 * Property constructed by bean mapping.
 *
 * @param <B> the bean type
 */
public class BeanProperty<B> extends Property<B> {

    private final Class<B> beanClass;
    private final Mapper mapper;

    public BeanProperty(Class<B> beanClass, String path, B defaultValue) {
        super(path, defaultValue);
        this.beanClass = beanClass;
        this.mapper = new Mapper();
    }

    @Override
    protected B getFromResource(PropertyResource resource) {
        return mapper.convertToBean(getPath(), resource, beanClass);
    }
}
