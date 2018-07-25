package ch.jalu.configme.neo.beanmapper;

import ch.jalu.configme.beanmapper.ConfigMeMapperException;
import ch.jalu.configme.neo.resource.PropertyReader;
import ch.jalu.configme.neo.utils.TypeInformation;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

public class Mapper {

    private final Map<String, Collection<BeanProperty>> classProperties = new HashMap<>();
    private BeanDescriptionFactory beanDescriptionFactory = new BeanDescriptionFactory();
    private ValueTransformer valueTransformer = new ValueTransformer();

    @Nullable
    public <T> T convertToBean(PropertyReader reader, String path, Class<T> clazz) {
        Object value = reader.getObject(path);
        if (value == null) {
            return null;
        }

        return (T) convertToValueForField(new TypeInformation(clazz), value);
    }

    // TODO: typeInformation will have to be a context with more stuff later on.
    private Object convertToValueForField(TypeInformation typeInformation, Object value) {
        Class<?> rawClass = typeInformation.getSafeToWriteClass();
        Objects.requireNonNull(rawClass, "Cannot determine type"); // TODO: check this behavior

        Object transformedValue = valueTransformer.value(rawClass, value);
        if (transformedValue != null) {
            return transformedValue;
        }


        if (Collection.class.isAssignableFrom(rawClass)) {
            return createCollection(typeInformation, value);
        }

        if (Map.class.isAssignableFrom(rawClass)) {
            return processMap(typeInformation, value);
        }
        return convertToBean(typeInformation, value);
    }

    @Nullable
    protected Collection createCollection(TypeInformation collectionType, Object value) {
        if (!(value instanceof Iterable<?>)) {
            return null;
        }

        TypeInformation entryType = collectionType.getGenericType(0);
        Collection result = createCollectionForResults(collectionType);
        for (Object entry : (Iterable) value) {
            result.add(convertToValueForField(entryType, entry));
        }
        return result;
    }

    @Nullable
    protected Map processMap(TypeInformation mapType, Object value) {
        if (value instanceof Map<?, ?>) {
            Map<String, ?> entries = (Map<String, ?>) value;
            if (mapType.getGenericTypeAsClass(0) != String.class) {
                throw new ConfigMeMapperException("The key type of maps may only be of String type");
            }
            Map result = createMapForResults(mapType);
            for (Map.Entry<String, ?> entry : entries.entrySet()) {
                Object mappedValue = convertToValueForField(mapType.getGenericType(1), entry.getValue());
                if (mappedValue != null) {
                    result.put(entry.getKey(), mappedValue);
                }
            }
            return result;
        }
        return null;
    }

    protected Collection createCollectionForResults(TypeInformation typeInformation) {
        Class<?> collectionType = typeInformation.getSafeToWriteClass();
        if (collectionType.isAssignableFrom(ArrayList.class)) {
            return new ArrayList();
        } else if (collectionType.isAssignableFrom(LinkedHashSet.class)) {
            return new LinkedHashSet();
        } else {
            throw new IllegalStateException("Unsupported collection type '" + collectionType + "'");
        }
    }

    protected Map createMapForResults(TypeInformation typeInformation) {
        Class<?> mapType = typeInformation.getSafeToWriteClass();
        if (mapType.isAssignableFrom(LinkedHashMap.class)) {
            return new LinkedHashMap();
        } else if (mapType.isAssignableFrom(TreeMap.class)) {
            return new TreeMap();
        } else {
            throw new IllegalStateException("Unsupported map type '" + mapType + "'");
        }
    }

    /**
     * Converts the provided value to the requested JavaBeans class if possible.
     *
     * @param type type information
     * @param value the value from the property resource
     * @return the converted value, or null if not possible
     */
    @Nullable
    protected Object convertToBean(TypeInformation type, Object value) {
        Collection<BeanProperty> properties = getWritableProperties(type.getSafeToWriteClass());
        // Check that we have properties (or else we don't have a bean) and that the provided value is a Map
        // so we can execute the mapping process.
        if (properties.isEmpty() || !(value instanceof Map<?, ?>)) {
            return null;
        }

        Map<?, ?> entries = (Map<?, ?>) value;
        Object bean = createBean(type.getSafeToWriteClass());
        for (BeanProperty property : properties) {
            Object result = convertToValueForField(
                property.getTypeInformation(),
                entries.get(property.getName()));
            if (result != null) {
                property.setValue(bean, result);
            } else if (property.getValue(bean) == null) {
                // TODO: Refine error handling
                throw new IllegalStateException("For property '" + property + "' no value could be found");
            }
        }
        return bean;
    }

    protected <T> T createBean(Class<T> clazz) {
        try {
            return clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new ConfigMeMapperException("Could not create object of type '" + clazz.getName()
                + "'. It is required to have a default constructor.", e);
        }
    }

    /**
     * Returns the properties of the given bean class that need to be considered when constructing objects.
     *
     * @param clazz the class to get the bean properties from
     * @return relevant properties
     */
    public Collection<BeanProperty> getWritableProperties(Class<?> clazz) {
        return classProperties.computeIfAbsent(clazz.getCanonicalName(),
            s -> beanDescriptionFactory.collectWritableFields(clazz));
    }
}
