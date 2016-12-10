package com.github.authme.configme.beanmapper;

import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * Information about a bean property.
 */
public class BeanPropertyDescription {

    private final String name;
    private final Class<?> type;
    private final Type genericType;
    private final Method getter;
    private final Method setter;

    public BeanPropertyDescription(String name, Class<?> type, @Nullable Type genericType,
                                   Method getter, Method setter) {
        this.name = name;
        this.type = type;
        this.genericType = genericType;
        this.getter = getter;
        this.setter = setter;
    }

    /**
     * Returns the name of the property for the purpose of reading from and writing to a property resource.
     *
     * @return the export name of the property
     */
    public String getName() {
        return name;
    }

    /**
     * @return the property type
     */
    public Class<?> getType() {
        return type;
    }

    /**
     * @return the generic type of the property
     */
    @Nullable
    public Type getGenericType() {
        return genericType;
    }

    /**
     * Returns the value of the property for the given bean.
     *
     * @param bean the bean to read the property from
     * @return bean value
     */
    @Nullable
    public Object getValue(Object bean) {
        try {
            return getter.invoke(bean);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new ConfigMeMapperException(
                "Could not get property '" + name + "' from instance '" + bean + "'", e);
        }
    }

    /**
     * Sets the given property to the given value on the provided bean.
     *
     * @param bean the bean to modify
     * @param value the value to set the property to
     */
    public void setValue(Object bean, Object value) {
        try {
            setter.invoke(bean, value);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new ConfigMeMapperException(
                "Could not set property '" + name + "' to value '" + value + "' on instance '" + bean + "'", e);
        }
    }
}
