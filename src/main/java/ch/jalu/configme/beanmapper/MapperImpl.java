package ch.jalu.configme.beanmapper;

import ch.jalu.configme.resource.PropertyReader;
import ch.jalu.configme.utils.TypeInformation;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class MapperImpl implements Mapper {

    // ---------
    // Fields and general configurable methods
    // ---------

    private final Map<String, Collection<BeanPropertyDescription>> classProperties = new HashMap<>();
    private BeanDescriptionFactory beanDescriptionFactory = new BeanDescriptionFactoryImpl();
    private ValueTransformer valueTransformer = StandardTransformers.getDefaultValueTransformer();

    protected final BeanDescriptionFactory getBeanDescriptionFactory() {
        return beanDescriptionFactory;
    }

    protected void setBeanDescriptionFactory(BeanDescriptionFactory beanDescriptionFactory) {
        this.beanDescriptionFactory = beanDescriptionFactory;
    }

    protected final ValueTransformer getValueTransformer() {
        return valueTransformer;
    }

    protected void setValueTransformer(ValueTransformer valueTransformer) {
        this.valueTransformer = valueTransformer;
    }

    protected MappingContext createRootMappingContext(String path, TypeInformation beanType) {
        return MappingContextImpl.createRoot(path, beanType);
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


    // ---------
    // Export
    // ---------

    @Override
    public Object toExportValue(Object value) {
        // Step 1: attempt simple value transformation
        Object simpleValue = valueTransformer.toExportValue(value);
        if (simpleValue != null) {
            return simpleValue;
        } else if (value == null) {
            return null;
        }

        // Step 2: handle special cases like Collection
        simpleValue = createExportValueForSpecialTypes(value);
        if (simpleValue != null) {
            return simpleValue;
        }

        // Step 3: treat as bean
        Map<String, Object> mappedBean = new LinkedHashMap<>();
        for (BeanPropertyDescription property : getWritableProperties(value.getClass())) {
            mappedBean.put(property.getName(), toExportValue(property.getValue(value)));
        }
        return mappedBean;
    }

    protected Object createExportValueForSpecialTypes(Object value) {
        if (value instanceof Collection<?>) {
            return ((Collection<?>) value).stream()
                .map(this::toExportValue)
                .collect(Collectors.toList());
        }

        if (value instanceof Map<?, ?>) {
            Map<Object, Object> result = new LinkedHashMap<>();
            for (Map.Entry<?, ?> entry : ((Map<?, ?>) value).entrySet()) {
                result.put(entry.getKey(), toExportValue(entry.getValue()));
            }
            return result;
        }

        if (value instanceof Optional<?>) {
            Optional<?> optional = (Optional<?>) value;
            return optional.map(this::toExportValue).orElse(null);
        }

        return null;
    }


    // ---------
    // Bean mapping
    // ---------

    @Override
    public Object convertToBean(PropertyReader reader, String path, TypeInformation beanType) {
        Object value = reader.getObject(path);
        if (value == null) {
            return null;
        }

        return convertValueForType(createRootMappingContext(path, beanType), value);
    }

    protected Object convertValueForType(MappingContext context, Object value) {
        Class<?> rawClass = context.getTypeInformation().getSafeToWriteClass();
        if (rawClass == null) {
            throw new ConfigMeMapperException(context, "Cannot determine required type");
        }

        // Step 1: check if a value transformer can perform a simple conversion
        Object result = valueTransformer.value(rawClass, value);
        if (result != null) {
            return result;
        }

        // Step 2: check if we have a special type like List that is handled separately
        result = handleSpecialTypes(context, value);
        if (result != null) {
            return result;
        }

        // Step 3: last possibility - assume it's a bean and try to map values to its structure
        return createBean(context, value);
    }

    protected Object handleSpecialTypes(MappingContext context, Object value) {
        final Class<?> rawClass = context.getTypeInformation().getSafeToWriteClass();
        if (Collection.class.isAssignableFrom(rawClass)) {
            return createCollection(context, value);
        } else if (Map.class.isAssignableFrom(rawClass)) {
            return createMap(context, value);
        } else if (Optional.class.isAssignableFrom(rawClass)) {
            return createOptional(context, value);
        }
        return null;
    }

    // -- Collection

    @Nullable
    protected Collection createCollection(MappingContext context, Object value) {
        if (value instanceof Iterable<?>) {
            TypeInformation entryType = context.getGenericTypeInfoOrFail(0);
            Collection result = createCollectionMatchingType(context.getTypeInformation());

            int index = 0;
            for (Object entry : (Iterable) value) {
                result.add(convertValueForType(context.createChild("[" + index + "]", entryType), entry));
            }
            return result;
        }
        return null;
    }

    protected Collection createCollectionMatchingType(TypeInformation typeInformation) {
        Class<?> collectionType = typeInformation.getSafeToWriteClass();
        if (collectionType.isAssignableFrom(ArrayList.class)) {
            return new ArrayList();
        } else if (collectionType.isAssignableFrom(LinkedHashSet.class)) {
            return new LinkedHashSet();
        } else {
            throw new ConfigMeMapperException("Unsupported collection type '" + collectionType + "'");
        }
    }

    // -- Map

    @Nullable
    protected Map createMap(MappingContext context, Object value) {
        if (value instanceof Map<?, ?>) {
            if (context.getGenericTypeInfoOrFail(0).getSafeToWriteClass() != String.class) {
                throw new ConfigMeMapperException(context, "The key type of maps may only be of String type");
            }
            TypeInformation mapValueType = context.getGenericTypeInfoOrFail(1);

            Map<String, ?> entries = (Map<String, ?>) value;
            Map result = createMapMatchingType(context.getTypeInformation());
            for (Map.Entry<String, ?> entry : entries.entrySet()) {
                Object mappedValue = convertValueForType(
                    context.createChild("[k=" + entry.getKey() + "]", mapValueType), entry.getValue());
                if (mappedValue != null) {
                    result.put(entry.getKey(), mappedValue);
                }
            }
            return result;
        }
        return null;
    }

    protected Map createMapMatchingType(TypeInformation typeInformation) {
        Class<?> mapType = typeInformation.getSafeToWriteClass();
        if (mapType.isAssignableFrom(LinkedHashMap.class)) {
            return new LinkedHashMap();
        } else if (mapType.isAssignableFrom(TreeMap.class)) {
            return new TreeMap();
        } else {
            throw new ConfigMeMapperException("Unsupported map type '" + mapType + "'");
        }
    }

    // -- Optional

    protected Object createOptional(MappingContext context, Object value) {
        MappingContext childContext = context.createChild("[v]", context.getGenericTypeInfoOrFail(0));
        Object result = convertValueForType(childContext, value);
        return Optional.ofNullable(result);
    }

    // -- Bean

    /**
     * Converts the provided value to the requested JavaBeans class if possible.
     *
     * @param context mapping context (incl. desired type)
     * @param value the value from the property resource
     * @return the converted value, or null if not possible
     */
    @Nullable
    protected Object createBean(MappingContext context, Object value) {
        // Ensure that the value is a map so we can map it to a bean
        if (!(value instanceof Map<?, ?>)) {
            return null;
        }

        Collection<BeanPropertyDescription> properties = getWritableProperties(
            context.getTypeInformation().getSafeToWriteClass());
        // Check that we have properties (or else we don't have a bean)
        if (properties.isEmpty()) {
            return null;
        }

        Map<?, ?> entries = (Map<?, ?>) value;
        Object bean = createBeanMatchingType(context.getTypeInformation());
        for (BeanPropertyDescription property : properties) {
            Object result = convertValueForType(
                context.createChild(property.getName(), property.getTypeInformation()),
                entries.get(property.getName()));
            if (result != null) {
                property.setValue(bean, result);
            } else if (property.getValue(bean) == null) {
                return null;
            }
        }
        return bean;
    }

    /**
     * Creates an object matching the given type information.
     *
     * @param typeInformation the required type
     * @return new instance of the given type
     */
    protected Object createBeanMatchingType(TypeInformation typeInformation) {
        // clazz is never null given the only path that leads to this method already performs that check
        final Class<?> clazz = typeInformation.getSafeToWriteClass();
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (ReflectiveOperationException e) {
            throw new ConfigMeMapperException("Could not create object of type '" + clazz.getName()
                + "'. It is required to have a default constructor.", e);
        }
    }
}
