package ch.jalu.configme.beanmapper;

import ch.jalu.configme.utils.TypeInformation;

import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Default implementation of {@link BeanPropertyDescription}.
 */
public class BeanPropertyDescriptionImpl implements BeanPropertyDescription {

    private final String name;
    private final TypeInformation typeInformation;
    private final Method getter;
    private final Method setter;

    /**
     * Constructor.
     *
     * @param name name of the property in the export
     * @param typeInformation type of the property
     * @param getter getter for the property
     * @param setter setter for the property
     */
    public BeanPropertyDescriptionImpl(String name, TypeInformation typeInformation, Method getter, Method setter) {
        this.name = name;
        this.typeInformation = typeInformation;
        this.getter = getter;
        this.setter = setter;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public TypeInformation getTypeInformation() {
        return typeInformation;
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

    @Override
    public String toString() {
        return "Bean property '" + name + "' with getter '" + getter + "'";
    }
}
