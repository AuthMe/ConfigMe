package com.github.authme.configme.beanmapper;

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
            final String propertyName = propertyDescriptor.getName();
            final Class<?> propertyType = propertyDescriptor.getPropertyType();
            final Object configValue = entries.get(propertyName);

            if (propertyType.isInstance(configValue)) {
                setProperty(propertyDescriptor, configValue, bean);
            } else {
                Object mappedValue = handleDefaultTypes(propertyType, configValue);
                if (mappedValue != null) {
                    setProperty(propertyDescriptor, mappedValue, bean);
                } else if (getProperty(propertyDescriptor, bean) == null) {
                    throw new IllegalStateException("No value found for mandatory property '"
                        + propertyDescriptor.getName() + "' in '" + clazz + "'");
                }
            }
        }
        return bean;
    }

    private static <T> T invokeDefaultConstructor(Class<T> clazz) {
        try {
            return clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }

    private static Object handleDefaultTypes(Class<?> propertyType, Object configValue) {
        if (propertyType.isEnum() && configValue instanceof String) {
            Class<? extends Enum<?>> enumClass = (Class<? extends Enum<?>>) propertyType;
            for (Enum<?> e : enumClass.getEnumConstants()) {
                if (e.name().equals(configValue)) {
                    return e;
                }
            }
            return null;
        }

        return null;
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
