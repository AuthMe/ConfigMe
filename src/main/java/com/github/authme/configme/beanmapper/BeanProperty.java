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
        this(beanClass, path, defaultValue, new Mapper());
    }

    public BeanProperty(Class<B> beanClass, String path, B defaultValue, Mapper mapper) {
        super(path, defaultValue);
        this.beanClass = beanClass;
        this.mapper = mapper;
    }

    @Override
    protected B getFromResource(PropertyResource resource) {
        return mapper.convertToBean(getPath(), resource, beanClass);
    }
}
