package ch.jalu.configme.beanmapper.definition;

import ch.jalu.configme.beanmapper.definition.properties.BeanFieldPropertyDefinition;
import ch.jalu.configme.beanmapper.definition.properties.BeanPropertyDefinition;
import ch.jalu.configme.exception.ConfigMeException;
import ch.jalu.configme.internal.ReflectionHelper;
import ch.jalu.configme.properties.convertresult.ConvertErrorRecorder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Definition of a bean based on a regular Java class with a no-arg constructor.
 */
public class NoArgConstructorBeanDefinition implements BeanDefinition {

    private final Constructor<?> noArgConstructor;
    private final List<BeanFieldPropertyDefinition> properties;

    public NoArgConstructorBeanDefinition(@NotNull Constructor<?> noArgConstructor,
                                          @NotNull List<BeanFieldPropertyDefinition> properties) {
        this.noArgConstructor = noArgConstructor;
        this.properties = properties;
    }

    protected final @NotNull Constructor<?> getNoArgConstructor() {
        return noArgConstructor;
    }

    protected final @NotNull List<BeanFieldPropertyDefinition> getFieldProperties() {
        return properties;
    }

    @Override
    public @NotNull List<BeanPropertyDefinition> getProperties() {
        return Collections.unmodifiableList(properties);
    }

    @Override
    public @Nullable Object create(@NotNull List<Object> propertyValues,
                                   @NotNull ConvertErrorRecorder errorRecorder) {
        final Object bean = createNewInstance();

        if (propertyValues.size() != properties.size()) {
            throw new ConfigMeException("Invalid property values, " + propertyValues.size() + " were given, but "
                + noArgConstructor.getDeclaringClass() + " has " + properties.size() + " properties");
        }

        Iterator<BeanFieldPropertyDefinition> propIt = properties.iterator();
        Iterator<Object> valuesIt = propertyValues.iterator();
        while (propIt.hasNext() && valuesIt.hasNext()) {
            BeanFieldPropertyDefinition property = propIt.next();
            Object value = valuesIt.next();

            boolean isValid = handleProperty(bean, property, value, errorRecorder);
            if (!isValid) {
                return null;
            }
        }
        return bean;
    }

    /**
     * Creates a new instance with the constructor.
     *
     * @return the new instance
     */
    protected @NotNull Object createNewInstance() {
        ReflectionHelper.setAccessibleIfNeeded(noArgConstructor);
        try {
            return noArgConstructor.newInstance();
        } catch (ReflectiveOperationException e) {
            throw new ConfigMeException("Failed to call constructor for " + noArgConstructor.getDeclaringClass(), e);
        }
    }

    /**
     * Processes one property and its value, returning whether the bean is still valid, i.e. if this method returns
     * false, the entire instantiation should be aborted.
     *
     * @param bean the bean to modify
     * @param property the property to handle
     * @param value the value given for the property
     * @param errorRecorder error recorder for conversion errors
     * @return false if the bean cannot be constructed, true otherwise (to continue)
     */
    protected boolean handleProperty(@NotNull Object bean, @NotNull BeanFieldPropertyDefinition property,
                                     @Nullable Object value, @NotNull ConvertErrorRecorder errorRecorder) {
        if (value == null) {
            if (property.getValue(bean) == null) {
                return false; // No default value on field, return null -> no bean with a null value
            }
            errorRecorder.setHasError("Fallback to default value for " + property);
        } else {
            property.setValue(bean, value);
        }
        return true;
    }
}
