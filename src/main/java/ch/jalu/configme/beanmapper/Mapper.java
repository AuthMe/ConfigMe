package ch.jalu.configme.beanmapper;

import ch.jalu.configme.beanmapper.transformer.Transformer;
import ch.jalu.configme.beanmapper.transformer.Transformers;
import ch.jalu.configme.resource.PropertyResource;

import javax.annotation.Nullable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static ch.jalu.configme.beanmapper.MapperUtils.getGenericClassesSafely;
import static ch.jalu.configme.beanmapper.MapperUtils.invokeDefaultConstructor;

/**
 * Maps a section of a property resource to the provided JavaBean class. The mapping is based on the bean's properties,
 * whose names must correspond with the names in the property resource. For example, if a JavaBean class has a property
 * {@code length} and should be mapped from the property resource's value at path {@code definition}, the mapper will
 * look up {@code definition.length} to get the value for the JavaBean property.
 * <p>
 * Classes must be JavaBeans. These are simple classes with private fields, accompanied with getters and setters.
 * <b>The mapper only considers properties which have both a getter and a setter method.</b> Any Java class without
 * at least one property with both a getter <i>and</i> a setter is not considered as a JavaBean class. Such classes can
 * be supported by implementing a custom {@link Transformer} that performs the conversion from the property resource to
 * an object of the class' type.
 * <p>
 * <b>Recursion:</b> the mapping of values to a JavaBean is performed recursively, i.e. a JavaBean may have other
 * JavaBeans as fields at any arbitrary "depth."
 * <p>
 * <b>Collections</b> are only supported if they are explicitly typed, i.e. a field of {@code List&lt;String>}
 * is supported but {@code List&lt;?>} and {@code List&lt;T extends Number>} are not supported. Specifically, you may
 * only declare fields of type {@link List} or {@link Set}, or a parent type ({@link Collection} or {@link Iterable}).
 * Fields of type <b>Map</b> are supported also, with similar limitations. Additionally, maps may only have
 * {@code String} as key type, but no restrictions are imposed on the value type.
 * <p>
 * JavaBeans may have <b>optional fields</b>. If the mapper cannot map the property resource value to the corresponding
 * field, it only treats it as a failure if the field's value is {@code null}. If the field has a default value assigned
 * to it on initialization, the default value remains and the mapping process continues. A JavaBean field whose value is
 * {@code null} signifies a failure and stops the mapping process immediately.
 */
public class Mapper {

    private final MappingErrorHandler errorHandler;
    private final Transformer[] transformers;
    private final BeanDescriptionFactory beanDescriptionFactory;
    private final Map<Class<?>, Collection<BeanPropertyDescription>> classProperties = new HashMap<>();

    /**
     * Creates a new JavaBean mapper with the default configuration.
     */
    public Mapper() {
        this(MappingErrorHandler.Impl.SILENT, new BeanDescriptionFactory(), Transformers.getDefaultTransformers());
    }

    /**
     * Creates a new JavaBean mapper with the given elements.
     *
     * @param mappingErrorHandler handler to use for mapping errors
     * @param beanDescriptionFactory factory to get bean property descriptions for classes
     * @param transformers the transformers to use for mapping values
     * @see Transformers#getDefaultTransformers
     */
    public Mapper(MappingErrorHandler mappingErrorHandler, BeanDescriptionFactory beanDescriptionFactory,
                  Transformer... transformers) {
        this.errorHandler = mappingErrorHandler;
        this.beanDescriptionFactory = beanDescriptionFactory;
        this.transformers = transformers;
    }

    /**
     * Converts the value in the property resource at the given path to the provided beans class.
     *
     * @param path the path to convert from
     * @param resource the property resource to read from
     * @param clazz the JavaBean class
     * @param <T> the bean type
     * @return the converted bean, or null if not possible
     */
    @Nullable
    @SuppressWarnings("unchecked")
    public <T> T convertToBean(String path, PropertyResource resource, Class<T> clazz) {
        return (T) getPropertyValue(clazz, null, resource.getObject(path), MappingContext.root());
    }

    /**
     * Returns a value of type {@code clazz} based on the provided {@code value} if possible.
     *
     * @param clazz the desired type to return
     * @param genericType the generic type if applicable
     * @param value the value to convert from
     * @param context the mapping context
     * @return the converted value, or null if not possible
     */
    @Nullable
    protected Object getPropertyValue(Class<?> clazz, @Nullable Type genericType, @Nullable Object value,
                                      MappingContext context) {
        Object result;
        if ((result = processCollection(clazz, genericType, value, context)) != null) {
            return result;
        } else if ((result = processMap(clazz, genericType, value, context)) != null) {
            return result;
        } else if ((result = processTransformers(clazz, genericType, value)) != null) {
            return result;
        }
        return convertToBean(clazz, value, context);
    }

    // Handles List and Set fields
    @Nullable
    protected Collection<?> processCollection(Class<?> clazz, Type genericType, Object value, MappingContext context) {
        if (Iterable.class.isAssignableFrom(clazz) && value instanceof Iterable<?>) {
            Class<?> collectionType = MapperUtils.getGenericClassSafely(genericType);
            List<Object> list = new ArrayList<>();
            for (Object o : (Iterable<?>) value) {
                Object mappedValue = getPropertyValue(collectionType, null, o, context.createChild(clazz));
                if (mappedValue != null) {
                    list.add(mappedValue);
                }
            }

            if (clazz.isAssignableFrom(List.class)) {
                return list;
            } else if (clazz.isAssignableFrom(Set.class)) {
                return new LinkedHashSet<>(list);
            } else {
                throw new ConfigMeMapperException("Unsupported collection type '" + clazz
                    + "' encountered. Only List and Set are supported by default");
            }
        }
        return null;
    }

    // Handles Map fields
    @Nullable
    protected Map processMap(Class<?> clazz, Type genericType, Object value, MappingContext context) {
        if (Map.class.isAssignableFrom(clazz) && value instanceof Map<?, ?>) {
            Map<String, ?> entries = (Map<String, ?>) value;
            Class<?>[] mapTypes = getGenericClassesSafely(genericType);
            if (mapTypes[0] != String.class) {
                throw new ConfigMeMapperException("The key type of maps may only be of String type");
            }
            Map result = new LinkedHashMap<>();
            for (Map.Entry<String, ?> entry : entries.entrySet()) {
                Object mappedValue = getPropertyValue(mapTypes[1], null, entry.getValue(), context.createChild(clazz));
                if (mappedValue != null) {
                    result.put(entry.getKey(), mappedValue);
                }
            }
            return result;
        }
        return null;
    }

    // Passes value to Transformers
    @Nullable
    protected Object processTransformers(Class<?> type, Type genericType, Object value) {
        Object result;
        for (Transformer transformer : transformers) {
            result = transformer.transform(type, genericType, value);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    /**
     * Converts the provided value to the requested JavaBeans class if possible.
     *
     * @param <T> the JavaBean type
     * @param clazz the JavaBean class
     * @param value the value from the property resource
     * @param context the mapping context
     * @return the converted value, or null if not possible
     */
    @Nullable
    protected <T> T convertToBean(Class<T> clazz, Object value, MappingContext context) {
        Collection<BeanPropertyDescription> properties = getWritableProperties(clazz);
        // Check that we have properties (or else we don't have a bean) and that the provided value is a Map
        // so we can execute the mapping process.
        if (properties.isEmpty() || !(value instanceof Map<?, ?>)) {
            return null;
        }

        Map<?, ?> entries = (Map<?, ?>) value;
        T bean = invokeDefaultConstructor(clazz);
        for (BeanPropertyDescription property : properties) {
            Object result = getPropertyValue(
                property.getType(),
                property.getGenericType(),
                entries.get(property.getName()),
                context.createChild(clazz));
            if (result != null) {
                property.setValue(bean, result);
            } else if (property.getValue(bean) == null) {
                errorHandler.handleError(clazz, context);
                return null;
            }
        }
        return bean;
    }

    public Collection<BeanPropertyDescription> getWritableProperties(Class<?> clazz) {
        Collection<BeanPropertyDescription> properties = classProperties.get(clazz);
        if (properties == null) {
            properties = beanDescriptionFactory.collectWritableFields(clazz);
            classProperties.put(clazz, properties);
        }
        return properties;
    }
}
