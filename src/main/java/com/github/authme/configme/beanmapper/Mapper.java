package com.github.authme.configme.beanmapper;

import com.github.authme.configme.exception.ConfigMeException;
import com.github.authme.configme.resource.PropertyResource;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

    public <T> Map<String, T> createMap(String path, PropertyResource resource, Class<T> clazz) {
        Object object = resource.getObject(path);
        if (object instanceof Map<?, ?>) {
            Map<String, ?> section = (Map<String, ?>) object;
            Map<String, T> result = new HashMap<>();
            for (Map.Entry<String, ?> entry : section.entrySet()) {
                result.put(entry.getKey(), convert((Map<String, ?>) entry.getValue(), clazz));
            }
            return result;
        }
        return null;
    }

    public <T> T createBean(String path, PropertyResource resource, Class<T> clazz) {
        return convert((Map<String, ?>) resource.getObject(path), clazz);
    }

    private <T> T convert(Map<String, ?> entries, Class<T> clazz) {
        List<PropertyDescriptor> properties = getWritableProperties(clazz);
        return convertToBean(entries, clazz, properties);
    }

    private <T> T convertToBean(Object value, Class<T> clazz, List<PropertyDescriptor> properties) {
        if (properties.isEmpty()) {
            // TODO: Harmonize with existing looping -> Replace PropertyDescriptor argument with more generic args
            Object o;
            for (Transformer transformer : transformers) {
                o = transformer.transform(clazz, value);
                if (o != null) {
                    return (T) o;
                }
            }
            return null;
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
                    throw new IllegalStateException(format("Unsupported collection type '%s' for property name '%s'",
                        descriptor.getPropertyType(), descriptor.getName()));
                }
            }
        }
        return null;
    }

    protected Object getPropertyValue(PropertyDescriptor descriptor, Object configValue) {
        Object result = processCollection(descriptor, configValue);
        if (result != null) {
            return result;
        }

        final Class<?> propertyType = descriptor.getPropertyType();
        for (Transformer transformer : transformers) {
            result = transformer.transform(propertyType, configValue);
            if (result != null) {
                return result;
            }
        }
        List<PropertyDescriptor> properties = getWritableProperties(propertyType);
        return properties.isEmpty() ? null : convertToBean(configValue, propertyType, properties);
    }

    private static <T> T invokeDefaultConstructor(Class<T> clazz) {
        try {
            return clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new ConfigMeException("Could not create object of type '" + clazz.getName()
                + "'. It is required to have a default constructor.", e);
        }
    }

    static List<PropertyDescriptor> getWritableProperties(Class<?> clazz) {
        PropertyDescriptor[] descriptors;
        try {
            descriptors = Introspector.getBeanInfo(clazz).getPropertyDescriptors();
        } catch (IntrospectionException e) {
            throw new IllegalStateException(e);
        }
        List<PropertyDescriptor> writableProperties = new ArrayList<>(descriptors.length);
        for (PropertyDescriptor descriptor : descriptors) {
            if (descriptor.getWriteMethod() != null) {
                writableProperties.add(descriptor);
            }
        }
        return writableProperties;
    }

    private static void setBeanProperty(PropertyDescriptor property, Object bean, Object value) {
        try {
            property.getWriteMethod().invoke(bean, value);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException(e);
        }
    }

    private static Object getBeanProperty(PropertyDescriptor property, Object bean) {
        if (property.getReadMethod() == null) {
            return null;
        }
        try {
            return property.getReadMethod().invoke(bean);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException(e);
        }
    }
}
