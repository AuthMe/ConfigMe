package com.github.authme.configme.beanmapper;

import com.github.authme.configme.resource.PropertyResource;

import javax.annotation.Nullable;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.github.authme.configme.beanmapper.MapperUtils.getBeanProperty;
import static com.github.authme.configme.beanmapper.MapperUtils.getGenericClassesSafely;
import static com.github.authme.configme.beanmapper.MapperUtils.getWritableProperties;
import static com.github.authme.configme.beanmapper.MapperUtils.invokeDefaultConstructor;
import static com.github.authme.configme.beanmapper.MapperUtils.setBeanProperty;
import static java.lang.String.format;

/**
 * Maps a section of a property resource to the provided JavaBean class. The mapping is based on the field names,
 * which must correspond to the field in the property resource. For example, if a JavaBean class has a field called
 * {@code length} and should be mapped from the property resource's value at path {@code definition}, the mapper will
 * look up {@code definition.length} to get the value for the JavaBean field.
 * <p>
 * Classes must be JavaBeans. These are simple classes with private fields, accompanied with getters and setters.
 * <b>The mapper only considers fields which have an associated setter method.</b> JavaBean classes without any setter
 * method are considered as type that needs to be converted to. To support these, you need to implement your own
 * {@link Transformer} and instantiate the mapper with it.
 * <p>
 * <b>Recursion:</b> the mapping of values to a JavaBean is performed recursively, i.e. a JavaBean may have other
 * JavaBeans as fields at an arbitrary "depth."
 * <p>
 * <b>Collections</b> are only supported if they are explicitly typed, i.e. a field of {@code List&lt;String>}
 * is supported but {@code List&lt;?>} or {@code List&lt;T extends Number>} are not supported. Specifically, you may
 * only declare fields of type {@link List} or {@link Set}, or a parent type ({@link Collection} or {@link Iterable}).
 * <p>
 * JavaBeans may have <b>optional fields</b>. If the mapper cannot map the property resource value to the corresponding
 * field, it only treats it as a failure if the field's value is {@code null}. If it has a default
 * value assigned to it, the default value remains and the mapping process continues. A JavaBean field whose value is
 * {@code null} signifies a failure and stops the mapping process immediately.
 */
public class Mapper {

    private final Transformer[] transformers;

    /**
     * Creates a new JavaBean mapper with the default type transformers.
     */
    public Mapper() {
        this(Transformers.getDefaultTransformers());
    }

    /**
     * Creates a new JavaBean mapper with the given transformers.
     *
     * @param transformers the transformers to use for mapping values
     * @see Transformers#getDefaultTransformers
     */
    public Mapper(Transformer... transformers) {
        this.transformers = transformers;
    }

    @Nullable
    public <T> Map<String, T> createMap(String path, PropertyResource resource, Class<T> clazz) {
        Object object = resource.getObject(path);
        if (object instanceof Map<?, ?>) {
            final List<PropertyDescriptor> properties = getWritableProperties(clazz);
            Map<String, ?> section = (Map<String, ?>) object;
            Map<String, T> result = new HashMap<>();
            for (Map.Entry<String, ?> entry : section.entrySet()) {
                result.put(entry.getKey(), convertToBean(entry.getValue(), clazz, properties));
            }
            return result;
        }
        return null;
    }

    @Nullable
    public <T> T createBean(String path, PropertyResource resource, Class<T> clazz) {
        return convertToBean(resource.getObject(path), clazz, getWritableProperties(clazz));
    }

    /**
     * Converts the provided value to the requested JavaBeans class if possible.
     *
     * @param value the value from the property resource
     * @param clazz the JavaBean class
     * @param properties the JavaBean class' properties
     * @param <T> the JavaBean type
     * @return the converted value, or null if not possible
     */
    @Nullable
    private <T> T convertToBean(Object value, Class<T> clazz, List<PropertyDescriptor> properties) {
        // No properties = not a bean, i.e. we've reached a leaf node.
        if (properties.isEmpty()) {
            return (T) getValueFromTransformers(clazz, value);
        }

        // We have a bean and expect the property resource value to be a map in order to perform the mapping.
        // If we don't have a map, we need to stop the mapping process here.
        if (!(value instanceof Map<?, ?>)) {
            return null;
        }

        Map<?, ?> entries = (Map<?, ?>) value;
        T bean = invokeDefaultConstructor(clazz);
        for (PropertyDescriptor propertyDescriptor : properties) {
            Object result = getPropertyValue(propertyDescriptor, entries.get(propertyDescriptor.getName()));
            if (result != null) {
                setBeanProperty(propertyDescriptor, bean, result);
            } else if (getBeanProperty(propertyDescriptor, bean) == null) {
                // TODO: Allow to set exception mode
                throw new ConfigMeMapperException("No suitable value found for mandatory property '"
                    + propertyDescriptor.getName() + "' in '" + clazz + "'");
            }
        }
        return bean;
    }

    @Nullable
    protected Collection<?> processCollection(PropertyDescriptor descriptor, Object value) {
        if (Iterable.class.isAssignableFrom(descriptor.getPropertyType()) && value instanceof Iterable<?>) {
            Class<?> collectionType = MapperUtils.getGenericClassSafely(descriptor);
            List<PropertyDescriptor> propertiesInType = getWritableProperties(collectionType);
            List list = new ArrayList<>();
            for (Object o : (Iterable<?>) value) {
                list.add(convertToBean(o, collectionType, propertiesInType));
            }

            if (descriptor.getPropertyType().isAssignableFrom(List.class)) {
                return list;
            } else if (descriptor.getPropertyType().isAssignableFrom(Set.class)) {
                return new HashSet<>(list);
            } else {
                throw new ConfigMeMapperException(format("Unsupported collection type '%s' for property name '%s'",
                    descriptor.getPropertyType(), descriptor.getName()));
            }
        }
        return null;
    }

    @Nullable
    protected Map<?, ?> processMap(PropertyDescriptor descriptor, Object value) {
        if (Map.class.isAssignableFrom(descriptor.getPropertyType()) && value instanceof Map<?, ?>) {
            Map<String, ?> entries = (Map<String, ?>) value;
            Class<?>[] mapTypes = getGenericClassesSafely(descriptor);
            if (mapTypes[0] != String.class) {
                // TODO: For now the key can only be a String
                throw new ConfigMeMapperException("Map key type can only be String for now");
            }
            Map result = new HashMap<>();
            final List<PropertyDescriptor> mapValueTypeProperties = getWritableProperties(mapTypes[1]);
            for (Map.Entry<String, ?> entry : entries.entrySet()) {
                result.put(entry.getKey(), convertToBean(entry.getValue(), mapTypes[1], mapValueTypeProperties));
            }
            return result;
        }
        return null;
    }

    private Object getValueFromTransformers(Class<?> type, Object value) {
        Object result;
        for (Transformer transformer : transformers) {
            result = transformer.transform(type, value);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    protected Object getPropertyValue(PropertyDescriptor descriptor, Object value) {
        Object result;
        if ((result = processCollection(descriptor, value)) != null) {
            return result;
        } else if ((result = processMap(descriptor, value)) != null) {
            return result;
        }

        Class<?> type = descriptor.getPropertyType();
        result = getValueFromTransformers(type, value);
        if (result != null) {
            return result;
        }

        List<PropertyDescriptor> properties = getWritableProperties(type);
        return properties.isEmpty() ? null : convertToBean(value, type, properties);
    }
}
