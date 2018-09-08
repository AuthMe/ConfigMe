package ch.jalu.configme.beanmapper;

import ch.jalu.configme.beanmapper.leafvaluehandler.LeafValueHandler;
import ch.jalu.configme.beanmapper.leafvaluehandler.StandardLeafValueHandlers;
import ch.jalu.configme.beanmapper.propertydescription.BeanDescriptionFactory;
import ch.jalu.configme.beanmapper.propertydescription.BeanDescriptionFactoryImpl;
import ch.jalu.configme.beanmapper.propertydescription.BeanPropertyDescription;
import ch.jalu.configme.utils.TypeInformation;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * Implementation of {@link Mapper}.
 * <p>
 * Maps a section of a property resource to the provided JavaBean class. The mapping is based on the bean's properties,
 * whose names must correspond with the names in the property resource. For example, if a JavaBean class has a property
 * {@code length} and should be mapped from the property resource's value at path {@code definition}, the mapper will
 * look up {@code definition.length} to get the value of the JavaBean property.
 * <p>
 * Classes must be JavaBeans. These are simple classes with private fields, accompanied with getters and setters.
 * <b>The mapper only considers properties which have both a getter and a setter method.</b> Any Java class without
 * at least one property with both a getter <i>and</i> a setter is not considered as a JavaBean class. Such classes can
 * be supported by implementing a custom {@link LeafValueHandler} that performs the conversion from the value coming
 * from the property reader to an object of the class' type.
 * <p>
 * <b>Recursion:</b> the mapping of values to a JavaBean is performed recursively, i.e. a JavaBean may have other
 * JavaBeans as fields and generic types at any arbitrary "depth."
 * <p>
 * <b>Collections</b> are only supported if they are explicitly typed, i.e. a field of {@code List<String>}
 * is supported but {@code List<?>} and {@code List<T extends Number>} are not supported. Specifically, you may
 * only declare fields of type {@link java.util.List} or {@link java.util.Set}, or a parent type ({@link Collection}
 * or {@link Iterable}).
 * Fields of type <b>Map</b> are supported also, with similar limitations. Additionally, maps may only have
 * {@code String} as key type, but no restrictions are imposed on the value type.
 * <p>
 * JavaBeans may have <b>optional fields</b>. If the mapper cannot map the property resource value to the corresponding
 * field, it only treats it as a failure if the field's value is {@code null}. If the field has a default value assigned
 * to it on initialization, the default value remains and the mapping process continues. A JavaBean field whose value is
 * {@code null} signifies a failure and stops the mapping process immediately.
 */
public class MapperImpl implements Mapper {

    /** Marker object to signal that null is meant to be used as value. */
    public static final Object RETURN_NULL = new Object();

    // ---------
    // Fields and general configurable methods
    // ---------

    private final BeanDescriptionFactory beanDescriptionFactory;
    private final LeafValueHandler leafValueHandler;

    public MapperImpl() {
        this(new BeanDescriptionFactoryImpl(), StandardLeafValueHandlers.getDefaultLeafValueHandler());
    }

    public MapperImpl(BeanDescriptionFactory beanDescriptionFactory, LeafValueHandler leafValueHandler) {
        this.beanDescriptionFactory = beanDescriptionFactory;
        this.leafValueHandler = leafValueHandler;
    }

    protected final BeanDescriptionFactory getBeanDescriptionFactory() {
        return beanDescriptionFactory;
    }

    protected final LeafValueHandler getLeafValueHandler() {
        return leafValueHandler;
    }

    protected MappingContext createRootMappingContext(TypeInformation beanType) {
        return MappingContextImpl.createRoot(beanType);
    }


    // ---------
    // Export
    // ---------

    @Override
    public Object toExportValue(Object value) {
        // Step 1: attempt simple value transformation
        Object simpleValue = leafValueHandler.toExportValue(value);
        if (simpleValue != null || value == null) {
            return unwrapReturnNull(simpleValue);
        }

        // Step 2: handle special cases like Collection
        simpleValue = createExportValueForSpecialTypes(value);
        if (simpleValue != null) {
            return unwrapReturnNull(simpleValue);
        }

        // Step 3: treat as bean
        Map<String, Object> mappedBean = new LinkedHashMap<>();
        for (BeanPropertyDescription property : beanDescriptionFactory.getAllProperties(value.getClass())) {
            Object exportValueOfProperty = toExportValue(property.getValue(value));
            if (exportValueOfProperty != null) {
                mappedBean.put(property.getName(), exportValueOfProperty);
            }
        }
        return mappedBean;
    }

    /**
     * Handles values of types which need special handling (such as Optional). Null means the value is not
     * a special type and that the export value should be built differently. Use {@link #RETURN_NULL} to $
     * signal that null should be used as the export value of the provided value.
     *
     * @param value the value to convert
     * @return the export value to use or {@link #RETURN_NULL}, or null if not applicable
     */
    @Nullable
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
            return optional.map(this::toExportValue).orElse(RETURN_NULL);
        }

        return null;
    }

    protected static Object unwrapReturnNull(Object o) {
        return o == RETURN_NULL ? null : o;
    }

    // ---------
    // Bean mapping
    // ---------

    @Nullable
    @Override
    public Object convertToBean(Object value, TypeInformation beanType) {
        if (value == null) {
            return null;
        }

        return convertValueForType(createRootMappingContext(beanType), value);
    }

    /**
     * Main method for converting a value to another type.
     *
     * @param context the mapping context
     * @param value the value to convert from
     * @return object whose type matches the one in the mapping context, or null if not applicable
     */
    @Nullable
    protected Object convertValueForType(MappingContext context, Object value) {
        Class<?> rawClass = context.getTypeInformation().getSafeToWriteClass();
        if (rawClass == null) {
            throw new ConfigMeMapperException(context, "Cannot determine required type");
        }

        // Step 1: check if a value transformer can perform a simple conversion
        Object result = leafValueHandler.convert(context.getTypeInformation(), value);
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

    /**
     * Handles special types in the bean mapping process which require special handling.
     *
     * @param context the mapping context
     * @param value the value to convert from
     * @return object whose type matches the one in the mapping context, or null if not applicable
     */
    @Nullable
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

    /**
     * Handles the creation of Collection properties.
     *
     * @param context the mapping context
     * @param value the value to map from
     * @return Collection property from the value, or null if not applicable
     */
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

    /**
     * Creates a Collection of a type which can be assigned to the provided type.
     *
     * @param typeInformation the required collection type
     * @return Collection of matching type
     */
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

    /**
     * Handles the creation of a Map property.
     *
     * @param context mapping context
     * @param value value to map from
     * @return Map property, or null if not applicable
     */
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

    /**
     * Creates a Map of a type which can be assigned to the provided type.
     *
     * @param typeInformation the required map type
     * @return Map of matching type
     */
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

        Collection<BeanPropertyDescription> properties = beanDescriptionFactory.getAllProperties(
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
