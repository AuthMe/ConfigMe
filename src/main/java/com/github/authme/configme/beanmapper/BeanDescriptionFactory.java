package com.github.authme.configme.beanmapper;

import javax.annotation.Nullable;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Creates all {@link BeanPropertyDescription} objects for a given class.
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
    public Collection<BeanPropertyDescription> collectWritableFields(Class<?> clazz) {
        List<PropertyDescriptor> descriptors = getWritableProperties(clazz);

        List<BeanPropertyDescription> properties = descriptors.stream()
            .map(d -> convert(d, getAssociatedField(clazz, d)))
            .filter(p -> p != null)
            .collect(Collectors.toList());

        validateProperties(clazz, properties);
        return properties;
    }

    /**
     * Tries to find the field of the class that is associated with the given property.
     *
     * @param clazz the class to process
     * @param property the property to match to a field
     * @return the associated field, or null if not found
     */
    @Nullable
    protected Field getAssociatedField(Class<?> clazz, PropertyDescriptor property) {
        // TODO #36: Need to consider inheritance? #getDeclaredField is only for fields on class itself
        // Might be an idea to use property.writeMethod.declaringClass.getDeclaredField() instead

        Field field = getFieldSilently(clazz, property.getName(), property.getPropertyType());
        // Special case for boolean (primitive) type -> isProp() might be getter for property "isProp" (and not "prop")
        if (field == null && boolean.class == property.getPropertyType()) {
            String alternativeName = "is" + capitalizeFirst(property.getName());
            return getFieldSilently(clazz, alternativeName, boolean.class);
        }
        return field;
    }

    /**
     * Converts a {@link PropertyDescriptor} to a {@link BeanPropertyDescription} object.
     *
     * @param descriptor the descriptor to convert
     * @param field the associated field if found, or null otherwise
     * @return the converted object, or null if the property should be skipped
     */
    @Nullable
    protected BeanPropertyDescription convert(PropertyDescriptor descriptor, @Nullable Field field) {
        if (field != null && Modifier.isTransient(field.getModifiers())) {
            return null;
        }
        return new BeanPropertyDescription(
            descriptor.getName(),
            descriptor.getPropertyType(),
            descriptor.getWriteMethod().getGenericParameterTypes()[0],
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
        // With #32 we will allow custom names for properties
        long totalUniqueNames = properties.stream().map(BeanPropertyDescription::getName).distinct().count();
        if (totalUniqueNames != properties.size()) {
            throw new ConfigMeMapperException("Found properties with the same name in '" + clazz + "'");
        }
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

    /**
     * Returns the field on the provided class with the given name if it exists and matches the required type.
     *
     * @param clazz the class to get a field from
     * @param name the name of the field
     * @param requiredType the type the field's type needs to match <i>exactly</i>
     * @return the field if it matched, or null otherwise
     */
    @Nullable
    private static Field getFieldSilently(Class<?> clazz, String name, Class<?> requiredType) {
        try {
            Field field = clazz.getDeclaredField(name);
            if (field.getType() == requiredType) {
                return field;
            }
        } catch (NoSuchFieldException e) {
            // noop
        }
        return null;
    }

    private static String capitalizeFirst(String str) {
        if (str.length() < 1) {
            throw new IllegalArgumentException(str);
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
