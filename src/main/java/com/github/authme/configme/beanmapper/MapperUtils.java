package com.github.authme.configme.beanmapper;

import java.beans.PropertyDescriptor;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

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
    public static Class<?> getGenericClassSafely(PropertyDescriptor descriptor) {
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

}
