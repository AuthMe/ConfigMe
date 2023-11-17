package ch.jalu.configme.beanmapper.instantiation;

import ch.jalu.configme.beanmapper.propertydescription.BeanPropertyDescription;
import ch.jalu.configme.beanmapper.propertydescription.BeanFieldPropertyDescription;
import ch.jalu.configme.exception.ConfigMeException;
import ch.jalu.configme.properties.convertresult.ConvertErrorRecorder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class BeanZeroArgConstrInstantiation implements BeanInstantiation {

    private final Constructor<?> zeroArgsConstructor;
    private final List<BeanFieldPropertyDescription> properties;

    public BeanZeroArgConstrInstantiation(@NotNull Constructor<?> zeroArgsConstructor,
                                          @NotNull List<BeanFieldPropertyDescription> properties) {
        this.zeroArgsConstructor = zeroArgsConstructor;
        this.properties = properties;
    }

    @Override
    public @NotNull List<BeanPropertyDescription> getProperties() {
        return Collections.unmodifiableList(properties);
    }

    @Override
    public @Nullable Object create(@NotNull List<Object> propertyValues,
                                   @NotNull ConvertErrorRecorder errorRecorder) {
        final Object bean;
        try {
            bean = zeroArgsConstructor.newInstance();
        } catch (ReflectiveOperationException e) {
            throw new ConfigMeException("Failed to call constructor for "
                + zeroArgsConstructor.getDeclaringClass());
        }

        if (propertyValues.size() != properties.size()) {
            throw new ConfigMeException("Invalid property values, " + propertyValues.size() + " were given, but "
                + zeroArgsConstructor.getDeclaringClass() + " has " + properties.size() + " properties");
        }

        Iterator<BeanFieldPropertyDescription> propIt = properties.iterator();
        Iterator<Object> valuesIt = propertyValues.iterator();
        while (propIt.hasNext() && valuesIt.hasNext()) {
            BeanFieldPropertyDescription property = propIt.next();
            Object value = valuesIt.next();
            if (value == null) {
                if (property.getValue(bean) == null) {
                    return null; // No default value on field, return null -> no bean with a null value
                }
                errorRecorder.setHasError("Fallback to default value");
            } else {
                property.setValue(bean, value);
            }
        }
        return bean;
    }
}
