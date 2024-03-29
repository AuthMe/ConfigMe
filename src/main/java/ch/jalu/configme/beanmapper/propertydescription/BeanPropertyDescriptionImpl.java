package ch.jalu.configme.beanmapper.propertydescription;

import ch.jalu.configme.beanmapper.ConfigMeMapperException;
import ch.jalu.typeresolver.TypeInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Default implementation of {@link BeanPropertyDescription}.
 */
public class BeanPropertyDescriptionImpl implements BeanPropertyDescription {

    private final String name;
    private final TypeInfo typeInformation;
    private final Method getter;
    private final Method setter;
    private final BeanPropertyComments comments;

    /**
     * Constructor.
     *
     * @param name name of the property in the export
     * @param typeInformation type of the property
     * @param getter getter for the property
     * @param setter setter for the property
     * @param comments the comments of the property
     */
    public BeanPropertyDescriptionImpl(@NotNull String name, @NotNull TypeInfo typeInformation,
                                       @NotNull Method getter, @NotNull Method setter,
                                       @NotNull BeanPropertyComments comments) {
        this.name = name;
        this.typeInformation = typeInformation;
        this.getter = getter;
        this.setter = setter;
        this.comments = comments;
    }

    @Override
    public @NotNull String getName() {
        return name;
    }

    @Override
    public @NotNull TypeInfo getTypeInformation() {
        return typeInformation;
    }

    /**
     * Returns the value of the property for the given bean.
     *
     * @param bean the bean to read the property from
     * @return bean value
     */
    public @Nullable Object getValue(@NotNull Object bean) {
        try {
            return getter.invoke(bean);
        } catch (@NotNull IllegalAccessException | InvocationTargetException e) {
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
    public void setValue(@NotNull Object bean, @NotNull Object value) {
        try {
            setter.invoke(bean, value);
        } catch (@NotNull IllegalAccessException | InvocationTargetException e) {
            throw new ConfigMeMapperException(
                "Could not set property '" + name + "' to value '" + value + "' on instance '" + bean + "'", e);
        }
    }

    @Override
    public @NotNull BeanPropertyComments getComments() {
        return comments;
    }

    @Override
    public @NotNull String toString() {
        return "Bean property '" + name + "' with getter '" + getter + "'";
    }
}
