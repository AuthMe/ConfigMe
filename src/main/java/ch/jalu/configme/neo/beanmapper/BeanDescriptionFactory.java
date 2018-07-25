package ch.jalu.configme.neo.beanmapper;

import ch.jalu.configme.beanmapper.ConfigMeMapperException;
import ch.jalu.configme.neo.utils.TypeInformation;

import javax.annotation.Nullable;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Creates all {@link BeanProperty} objects for a given class.
 * <p>
 * The returned bean field objects are required to be writable properties, i.e. any fields
 * which don't have an associated setter (or getter) will be ignored.
 */
public class BeanDescriptionFactory {

    /**
     * Returns all properties of the given bean class for which there exists a getter and setter.
     *
     * @param clazz the bean property to process
     * @return the bean class' properties to handle
     */
    public Collection<BeanProperty> collectWritableFields(Class<?> clazz) {
        List<PropertyDescriptor> descriptors = getWritableProperties(clazz);

        List<BeanProperty> properties = descriptors.stream()
            .map(this::convert)
            .filter(p -> p != null)
            .collect(Collectors.toList());

        validateProperties(clazz, properties);
        return properties;
    }

    /**
     * Converts a {@link PropertyDescriptor} to a {@link BeanProperty} object.
     *
     * @param descriptor the descriptor to convert
     * @return the converted object, or null if the property should be skipped
     */
    @Nullable
    protected BeanProperty convert(PropertyDescriptor descriptor) {
        if (Boolean.TRUE.equals(descriptor.getValue("transient"))) {
            return null;
        }

        return new BeanPropertyImpl(
            getPropertyName(descriptor),
            createTypeInfo(descriptor),
            descriptor.getReadMethod(),
            descriptor.getWriteMethod());
    }

    /**
     * Validates the class' properties.
     *
     * @param clazz the class to which the properties belong
     * @param properties the properties that will be used on the class
     */
    protected void validateProperties(Class<?> clazz, Collection<BeanProperty> properties) {
        Set<String> names = new HashSet<>(properties.size());
        properties.forEach(property -> {
            if (property.getName().isEmpty()) {
                throw new ConfigMeMapperException("Custom name of " + property + " may not be empty");
            }
            if (!names.add(property.getName())) {
                throw new ConfigMeMapperException(
                    clazz + " has multiple properties with name '" + property.getName() + "'");
            }
        });
    }

    protected String getPropertyName(PropertyDescriptor descriptor) {
        if (descriptor.getReadMethod().isAnnotationPresent(ExportName.class)) {
            return descriptor.getReadMethod().getAnnotation(ExportName.class).value();
        } else if (descriptor.getWriteMethod().isAnnotationPresent(ExportName.class)) {
            return descriptor.getWriteMethod().getAnnotation(ExportName.class).value();
        }
        return descriptor.getName();
    }

    protected TypeInformation createTypeInfo(PropertyDescriptor descriptor) {
        return new TypeInformation(descriptor.getWriteMethod().getGenericParameterTypes()[0]);
    }

    /**
     * Returns all properties of the given class that are writable
     * (all bean properties with an associated read and write method).
     *
     * @param clazz the class to process
     * @return all writable properties of the bean class
     */
    private static List<PropertyDescriptor> getWritableProperties(Class<?> clazz) {
        PropertyDescriptor[] descriptors;
        try {
            descriptors = Introspector.getBeanInfo(clazz).getPropertyDescriptors();
        } catch (IntrospectionException e) {
            throw new IllegalStateException(e);
        }
        List<PropertyDescriptor> writableProperties = new ArrayList<>(descriptors.length);
        for (PropertyDescriptor descriptor : descriptors) {
            if (descriptor.getWriteMethod() != null && descriptor.getReadMethod() != null) {
                writableProperties.add(descriptor);
            }
        }
        return writableProperties;
    }
}
