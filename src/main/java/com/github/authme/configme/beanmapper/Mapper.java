package com.github.authme.configme.beanmapper;

import com.github.authme.configme.exception.ConfigMeException;
import com.github.authme.configme.resource.PropertyResource;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private <T> T convert(Map<String, ?> entries, Class<T> clazz) {
        List<PropertyDescriptor> properties = getWritableProperties(clazz);
        T bean = invokeDefaultConstructor(clazz);
        for (PropertyDescriptor propertyDescriptor : properties) {
            Object result = getPropertyValue(
                propertyDescriptor.getPropertyType(), entries.get(propertyDescriptor.getName()));
            if (result != null) {
                setProperty(propertyDescriptor, result, bean);
            } else if (getProperty(propertyDescriptor, bean) == null) {
                throw new IllegalStateException("No value found for mandatory property '"
                    + propertyDescriptor.getName() + "' in '" + clazz + "'");
            }
        }
        return bean;
    }

    private Object getPropertyValue(Class<?> propertyType, Object configValue) {
        Object result = null;
        for (Transformer transformer : transformers) {
            result = transformer.transform(propertyType, configValue);
            if (result != null) {
                break;
            }
        }
        return result;
    }

    private static <T> T invokeDefaultConstructor(Class<T> clazz) {
        try {
            return clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new ConfigMeException("Could not create object of type '" + clazz.getName()
                + "'. It is required to have a default constructor.", e);
        }
    }

    private static List<PropertyDescriptor> getWritableProperties(Class<?> clazz) {
        PropertyDescriptor[] descriptors;
        try {
            descriptors = Introspector.getBeanInfo(clazz, Object.class).getPropertyDescriptors();
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

    private static void setProperty(PropertyDescriptor property, Object value, Object bean) {
        try {
            property.getWriteMethod().invoke(bean, value);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException(e);
        }
    }

    private static Object getProperty(PropertyDescriptor property, Object bean) {
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
