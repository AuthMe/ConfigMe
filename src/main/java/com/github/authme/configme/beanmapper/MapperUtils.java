package com.github.authme.configme.beanmapper;

import com.github.authme.configme.exception.ConfigMeException;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Mapper utilities.
 */
final class MapperUtils {

    private MapperUtils() {
    }

    /**
     *
     *
     * @param descriptor
     * @return
     */
    // http://stackoverflow.com/questions/5640058/how-to-use-a-lis-of-parameterized-property-names-and-type-for-a-java-bean
    static Class<?> getGenericClassSafely(PropertyDescriptor descriptor) {
        Type type = descriptor.getWriteMethod().getGenericParameterTypes()[0];
        if (type instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) type;
            Type[] genericTypes = pt.getActualTypeArguments();
            if (genericTypes.length > 0 && genericTypes[0] instanceof Class<?>) {
                return (Class<?>) genericTypes[0];
            }
        }
        return null;
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

    static void setBeanProperty(PropertyDescriptor property, Object bean, Object value) {
        try {
            property.getWriteMethod().invoke(bean, value);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException(e);
        }
    }

    static Object getBeanProperty(PropertyDescriptor property, Object bean) {
        if (property.getReadMethod() == null) {
            return null;
        }
        try {
            return property.getReadMethod().invoke(bean);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException(e);
        }
    }

    static <T> T invokeDefaultConstructor(Class<T> clazz) {
        try {
            return clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new ConfigMeException("Could not create object of type '" + clazz.getName()
                + "'. It is required to have a default constructor.", e);
        }
    }

}
