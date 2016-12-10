package com.github.authme.configme.beanmapper;

import com.github.authme.configme.properties.Property;
import com.github.authme.configme.resource.PropertyResource;

import java.util.Collection;

/**
 * Property constructed by bean mapping.
 *
 * @param <B> the bean type
 */
public class BeanProperty<B> extends Property<B> {

    private final Class<B> beanClass;
    private final Mapper mapper;

    public BeanProperty(Class<B> beanClass, String path, B defaultValue) {
        this(beanClass, path, defaultValue, ConfigMeMapper.getSingleton());
    }

    public BeanProperty(Class<B> beanClass, String path, B defaultValue, Mapper mapper) {
        super(path, defaultValue);
        this.beanClass = beanClass;
        this.mapper = mapper;
    }

    @Override
    protected B getFromResource(PropertyResource resource) {
        // Note #22: the property resource contains a bean object if the property's value was set
        // via the settings manager
        Object object = resource.getObject(getPath());
        if (beanClass.isInstance(object)) {
            return (B) object;
        }
        return mapper.convertToBean(getPath(), resource, beanClass);
    }

    public Collection<BeanPropertyDescription> getWritableProperties(Class<?> clazz) {
        return mapper.getWritableProperties(clazz);
    }
}
