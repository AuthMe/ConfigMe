package ch.jalu.configme.neo.beanmapper;

import ch.jalu.configme.neo.resource.PropertyReader;
import ch.jalu.configme.neo.utils.TypeInformation;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeMap;

public class MapperImpl implements Mapper {

    private final Map<String, Collection<BeanPropertyDescription>> classProperties = new HashMap<>();
    private BeanDescriptionFactory beanDescriptionFactory = new BeanDescriptionFactoryImpl();
    private ValueTransformer valueTransformer = StandardTransformers.getDefaultValueTransformer();

    @Override
    public <T> T convertToBean(PropertyReader reader, String path, Class<T> clazz) {
        Object value = reader.getObject(path);
        if (value == null) {
            return null;
        }

        return (T) convertToValueForField(new TypeInformation(clazz), value);
    }

    @Override
    public Object toExportValue(Object object) {
        return transformValueToExport(object);
    }

    protected Object transformValueToExport(Object value) {
        if (value instanceof Collection<?>) {
            List<Object> result = new ArrayList<>();
            for (Object entry : (Collection) value) {
                result.add(transformValueToExport(entry));
            }
            return result;
        }

        if (value instanceof Map<?, ?>) {
            Map<String, Object> result = new LinkedHashMap<>();
            for (Map.Entry<String, ?> entry : ((Map<String, ?>) value).entrySet()) {
                result.put(entry.getKey(), transformValueToExport(entry.getValue()));
            }
            return result;
        }

        if (value instanceof Optional<?>) {
            Optional<?> optional = (Optional<?>) value;
            return optional.map(this::transformValueToExport).orElse(null);
        }

        Object simpleValue = valueTransformer.toExportValue(value);
        if (simpleValue != null) {
            return simpleValue;
        } else if (value == null) {
            return null;
        }

        Map<String, Object> mappedBean = new LinkedHashMap<>();
        for (BeanPropertyDescription property : getWritableProperties(value.getClass())) {
            mappedBean.put(property.getName(), transformValueToExport(property.getValue(value)));
        }
        return mappedBean;
    }

    // TODO: typeInformation will have to be a context with more stuff later on.
    protected Object convertToValueForField(TypeInformation typeInformation, Object value) {
        Class<?> rawClass = typeInformation.getSafeToWriteClass();
        Objects.requireNonNull(rawClass, "Cannot determine type"); // TODO: check this behavior

        Object transformedValue = valueTransformer.value(rawClass, value);
        if (transformedValue != null) {
            return transformedValue;
        }


        if (Collection.class.isAssignableFrom(rawClass)) {
            return createCollection(typeInformation, value);
        } else if (Map.class.isAssignableFrom(rawClass)) {
            return processMap(typeInformation, value);
        } else if (Optional.class.isAssignableFrom(rawClass)) {
            return processOptional(typeInformation, value);
        }
        return convertToBean(typeInformation, value);
    }

    protected Object processOptional(TypeInformation typeInformation, Object value) {
        Object result = convertToValueForField(typeInformation.getGenericType(0), value);
        return Optional.ofNullable(result);
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
            throw new ConfigMeMapperException("Unsupported collection type '" + collectionType + "'");
        }
    }

    protected Map createMapForResults(TypeInformation typeInformation) {
        Class<?> mapType = typeInformation.getSafeToWriteClass();
        if (mapType.isAssignableFrom(LinkedHashMap.class)) {
            return new LinkedHashMap();
        } else if (mapType.isAssignableFrom(TreeMap.class)) {
            return new TreeMap();
        } else {
            throw new ConfigMeMapperException("Unsupported map type '" + mapType + "'");
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
        Collection<BeanPropertyDescription> properties = getWritableProperties(type.getSafeToWriteClass());
        // Check that we have properties (or else we don't have a bean) and that the provided value is a Map
        // so we can execute the mapping process.
        if (properties.isEmpty() || !(value instanceof Map<?, ?>)) {
            return null;
        }

        Map<?, ?> entries = (Map<?, ?>) value;
        Object bean = createBean(type.getSafeToWriteClass());
        for (BeanPropertyDescription property : properties) {
            Object result = convertToValueForField(
                property.getTypeInformation(),
                entries.get(property.getName()));
            if (result != null) {
                property.setValue(bean, result);
            } else if (property.getValue(bean) == null) {
                return null;
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
    protected Collection<BeanPropertyDescription> getWritableProperties(Class<?> clazz) {
        return classProperties.computeIfAbsent(clazz.getCanonicalName(),
            s -> beanDescriptionFactory.findAllWritableProperties(clazz));
    }
}
