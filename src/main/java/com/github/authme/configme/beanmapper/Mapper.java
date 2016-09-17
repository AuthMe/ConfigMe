package com.github.authme.configme.beanmapper;

import com.github.authme.configme.exception.ConfigMeException;
import com.github.authme.configme.resource.PropertyResource;

import javax.annotation.Nullable;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.github.authme.configme.beanmapper.MapperUtils.getBeanProperty;
import static com.github.authme.configme.beanmapper.MapperUtils.getWritableProperties;
import static com.github.authme.configme.beanmapper.MapperUtils.invokeDefaultConstructor;
import static com.github.authme.configme.beanmapper.MapperUtils.setBeanProperty;
import static java.lang.String.format;

/**
 * Mapper to a bean.
 */
public class Mapper {

    private final Transformer[] transformers;

    public Mapper() {
        this(Transformers.getDefaultTransformers());
    }

    public Mapper(Transformer... transformers) {
        this.transformers = transformers;
    }

    @Nullable
    public <T> Map<String, T> createMap(String path, PropertyResource resource, Class<T> clazz) {
        Object object = resource.getObject(path);
        if (object instanceof Map<?, ?>) {
            final List<PropertyDescriptor> properties = getWritableProperties(clazz);
            Map<String, ?> section = (Map<String, ?>) object;
            Map<String, T> result = new HashMap<>();
            for (Map.Entry<String, ?> entry : section.entrySet()) {
                result.put(entry.getKey(), convertToBean(entry.getValue(), clazz, properties));
            }
            return result;
        }
        return null;
    }

    public <T> T createBean(String path, PropertyResource resource, Class<T> clazz) {
        return convertToBean(resource.getObject(path), clazz, getWritableProperties(clazz));
    }

    private <T> T convertToBean(Object value, Class<T> clazz, List<PropertyDescriptor> properties) {
        if (properties.isEmpty()) {
            return (T) getValueFromTransformers(clazz, value);
        }

        if (!(value instanceof Map<?, ?>)) {
            return null;
        }
        Map<?, ?> entries = (Map<?, ?>) value;

        T bean = invokeDefaultConstructor(clazz);
        for (PropertyDescriptor propertyDescriptor : properties) {
            Object result = getPropertyValue(propertyDescriptor, entries.get(propertyDescriptor.getName()));
            if (result != null) {
                setBeanProperty(propertyDescriptor, bean, result);
            } else if (getBeanProperty(propertyDescriptor, bean) == null) {
                // TODO: Allow to set exception mode
                throw new IllegalStateException("No value found for mandatory property '"
                    + propertyDescriptor.getName() + "' in '" + clazz + "'");
            }
        }
        return bean;
    }

    protected Collection<?> processCollection(PropertyDescriptor descriptor, Object value) {
        if (Iterable.class.isAssignableFrom(descriptor.getPropertyType())
                && (value instanceof Iterable<?> || value instanceof Map<?, ?>)) {
            Class<?> collectionType = MapperUtils.getGenericClassSafely(descriptor);
            if (collectionType != null) {
                if (value instanceof Map<?, ?>) {
                    // TODO: Need different handling for map
                    value = ((Map<?,?>) value).values();
                }
                List<PropertyDescriptor> propertiesInType = getWritableProperties(collectionType);
                List list = new ArrayList<>();
                for (Object o : (Iterable<?>) value) {
                    list.add(convertToBean(o, collectionType, propertiesInType));
                }

                if (descriptor.getPropertyType().isAssignableFrom(List.class)) {
                    return list;
                } else if (descriptor.getPropertyType().isAssignableFrom(Set.class)) {
                    return new HashSet<>(list);
                } else {
                    throw new ConfigMeException(format("Unsupported collection type '%s' for property name '%s'",
                        descriptor.getPropertyType(), descriptor.getName()));
                }
            }
        }
        return null;
    }

    private Object getValueFromTransformers(Class<?> type, Object value) {
        Object result;
        for (Transformer transformer : transformers) {
            result = transformer.transform(type, value);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    protected Object getPropertyValue(PropertyDescriptor descriptor, Object value) {
        Object result = processCollection(descriptor, value);
        if (result != null) {
            return result;
        }

        Class<?> type = descriptor.getPropertyType();
        result = getValueFromTransformers(type, value);
        if (result != null) {
            return result;
        }

        List<PropertyDescriptor> properties = getWritableProperties(type);
        return properties.isEmpty() ? null : convertToBean(value, type, properties);
    }
}
