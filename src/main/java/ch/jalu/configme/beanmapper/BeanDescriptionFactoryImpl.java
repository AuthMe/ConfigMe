package ch.jalu.configme.beanmapper;

import ch.jalu.configme.utils.TypeInformation;

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
 * Creates all {@link BeanPropertyDescription} objects for a given class.
 * <p>
 * This description factory returns property descriptions for all properties on a class
 * for which a getter and setter is associated. Inherited properties are considered.
 * <p>
 * This implementation supports {@link ExportName} and transient properties, declared either
 * with the {@code transient} keyword or by adding the {@link java.beans.Transient} annotation.
 */
public class BeanDescriptionFactoryImpl implements BeanDescriptionFactory {

    /**
     * Returns all properties of the given bean class for which there exists a getter and setter.
     *
     * @param clazz the bean property to process
     * @return the bean class' properties to handle
     */
    @Override
    public Collection<BeanPropertyDescription> findAllWritableProperties(Class<?> clazz) {
        List<PropertyDescriptor> descriptors = getWritableProperties(clazz);

        List<BeanPropertyDescription> properties = descriptors.stream()
            .map(this::convert)
            .filter(p -> p != null)
            .collect(Collectors.toList());

        validateProperties(clazz, properties);
        return properties;
    }

    /**
     * Converts a {@link PropertyDescriptor} to a {@link BeanPropertyDescription} object.
     *
     * @param descriptor the descriptor to convert
     * @return the converted object, or null if the property should be skipped
     */
    @Nullable
    protected BeanPropertyDescription convert(PropertyDescriptor descriptor) {
        if (Boolean.TRUE.equals(descriptor.getValue("transient"))) {
            return null;
        }

        return new BeanPropertyDescriptionImpl(
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
    protected void validateProperties(Class<?> clazz, Collection<BeanPropertyDescription> properties) {
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

    /**
     * Returns the name which is used in the export files for the given property descriptor.
     *
     * @param descriptor the descriptor to get the name for
     * @return the property name
     */
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
    protected List<PropertyDescriptor> getWritableProperties(Class<?> clazz) {
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
