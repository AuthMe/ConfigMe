package ch.jalu.configme.beanmapper;

import ch.jalu.configme.beanmapper.context.ExportContext;
import ch.jalu.configme.beanmapper.context.ExportContextImpl;
import ch.jalu.configme.beanmapper.context.MappingContext;
import ch.jalu.configme.beanmapper.context.MappingContextImpl;
import ch.jalu.configme.beanmapper.leafvaluehandler.LeafValueHandler;
import ch.jalu.configme.beanmapper.leafvaluehandler.LeafValueHandlerImpl;
import ch.jalu.configme.beanmapper.leafvaluehandler.MapperLeafType;
import ch.jalu.configme.beanmapper.propertydescription.BeanDescriptionFactory;
import ch.jalu.configme.beanmapper.propertydescription.BeanDescriptionFactoryImpl;
import ch.jalu.configme.beanmapper.propertydescription.BeanPropertyComments;
import ch.jalu.configme.beanmapper.propertydescription.BeanPropertyDescription;
import ch.jalu.configme.properties.convertresult.ConvertErrorRecorder;
import ch.jalu.configme.properties.convertresult.ValueWithComments;
import ch.jalu.typeresolver.TypeInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import static ch.jalu.configme.utils.PathUtils.OPTIONAL_SPECIFIER;
import static ch.jalu.configme.utils.PathUtils.pathSpecifierForIndex;
import static ch.jalu.configme.utils.PathUtils.pathSpecifierForMapKey;

/**
 * Implementation of {@link Mapper}.
 * <p>
 * Maps a section of a property resource to the provided JavaBean class. The mapping is based on the bean's properties,
 * whose names must correspond with the names in the property resource. For example, if a JavaBean class has a property
 * {@code length} and should be mapped from the property resource's value at path {@code definition}, the mapper will
 * look up {@code definition.length} to get the value of the JavaBean property.
 * <p>
 * Classes must be JavaBeans. These are simple classes with private fields, accompanied by getters and setters.
 * <b>The mapper only considers properties which have both a getter and a setter method.</b> Any Java class without
 * at least one property with both a getter <i>and</i> a setter is not considered as a JavaBean class. Such classes can
 * be supported by implementing a custom {@link MapperLeafType} that performs the conversion from the value coming
 * from the property reader to an object of the class's type.
 * <p>
 * <b>Recursion:</b> the mapping of values to a JavaBean is performed recursively, i.e. a JavaBean may have other
 * JavaBeans as fields and generic types at any arbitrary "depth".
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
        this(new BeanDescriptionFactoryImpl(),
             new LeafValueHandlerImpl(LeafValueHandlerImpl.createDefaultLeafTypes()));
    }

    public MapperImpl(@NotNull BeanDescriptionFactory beanDescriptionFactory,
                      @NotNull LeafValueHandler leafValueHandler) {
        this.beanDescriptionFactory = beanDescriptionFactory;
        this.leafValueHandler = leafValueHandler;
    }

    protected final @NotNull BeanDescriptionFactory getBeanDescriptionFactory() {
        return beanDescriptionFactory;
    }

    protected final @NotNull LeafValueHandler getLeafValueHandler() {
        return leafValueHandler;
    }

    protected @NotNull MappingContext createRootMappingContext(@NotNull TypeInfo beanType,
                                                               @NotNull ConvertErrorRecorder errorRecorder) {
        return MappingContextImpl.createRoot(beanType, errorRecorder);
    }

    protected @NotNull ExportContext createRootExportContext() {
        return ExportContextImpl.createRoot();
    }


    // ---------
    // Export
    // ---------

    @Override
    public @Nullable Object toExportValue(@NotNull Object value) {
        return toExportValue(value, createRootExportContext());
    }

    /**
     * Transforms the given value to an object suitable for the export to a configuration file.
     *
     * @param value the value to transform
     * @param exportContext export context
     * @return export value to use
     */
    protected @Nullable Object toExportValue(@Nullable Object value, @NotNull ExportContext exportContext) {
        // Step 1: attempt simple value transformation
        Object exportValue = leafValueHandler.toExportValue(value, exportContext);
        if (exportValue != null || value == null) {
            return unwrapReturnNull(exportValue);
        }

        // Step 2: handle special cases like Collection
        exportValue = createExportValueForSpecialTypes(value, exportContext);
        if (exportValue != null) {
            return unwrapReturnNull(exportValue);
        }

        // Step 3: treat as bean
        Map<String, Object> mappedBean = new LinkedHashMap<>();
        for (BeanPropertyDescription property : beanDescriptionFactory.getAllProperties(value.getClass())) {
            Object exportValueOfProperty = toExportValue(property.getValue(value), exportContext);
            if (exportValueOfProperty != null) {
                BeanPropertyComments propComments = property.getComments();
                if (exportContext.shouldInclude(propComments)) {
                    exportContext.registerComment(propComments);
                    exportValueOfProperty = new ValueWithComments(exportValueOfProperty,
                        propComments.getComments(), propComments.getUuid());
                }
                mappedBean.put(property.getName(), exportValueOfProperty);
            }
        }
        return mappedBean;
    }

    /**
     * Handles values of types which need special handling (such as Optional). Null means the value is not
     * a special type and that the export value should be built differently. Use {@link #RETURN_NULL} to
     * signal that null should be used as the export value of the provided value.
     *
     * @param value the value to convert
     * @param exportContext export context
     * @return the export value to use or {@link #RETURN_NULL}, or null if not applicable
     */
    protected @Nullable Object createExportValueForSpecialTypes(@Nullable Object value,
                                                                @NotNull ExportContext exportContext) {
        if (value instanceof Iterable<?>) {
            int index = 0;
            List<Object> result = new ArrayList<>();
            for (Object entry : (Iterable<?>) value) {
                ExportContext entryContext = exportContext.createChildContext(pathSpecifierForIndex(index));
                result.add(toExportValue(entry, entryContext));
                ++index;
            }
            return result;
        }

        if (value instanceof Map<?, ?>) {
            Map<Object, Object> result = new LinkedHashMap<>();
            for (Map.Entry<?, ?> entry : ((Map<?, ?>) value).entrySet()) {
                ExportContext entryContext = exportContext.createChildContext(pathSpecifierForMapKey(entry));
                result.put(entry.getKey(), toExportValue(entry.getValue(), entryContext));
            }
            return result;
        }

        if (value instanceof Optional<?>) {
            Optional<?> optional = (Optional<?>) value;
            return optional
                .map(v -> toExportValue(v, exportContext.createChildContext(OPTIONAL_SPECIFIER)))
                .orElse(RETURN_NULL);
        }

        return null;
    }

    protected static @Nullable Object unwrapReturnNull(@Nullable Object o) {
        return o == RETURN_NULL ? null : o;
    }

    // ---------
    // Bean mapping
    // ---------

    @Override
    public @Nullable Object convertToBean(@Nullable Object value, @NotNull TypeInfo targetType,
                                          @NotNull ConvertErrorRecorder errorRecorder) {
        if (value == null) {
            return null;
        }

        return convertValueForType(createRootMappingContext(targetType, errorRecorder), value);
    }

    /**
     * Main method for converting a value to another type.
     *
     * @param context the mapping context
     * @param value the value to convert from
     * @return object whose type matches the one in the mapping context, or null if not applicable
     */
    protected @Nullable Object convertValueForType(@NotNull MappingContext context, @Nullable Object value) {
        // Step 1: check if the value is a leaf
        Object result = leafValueHandler.convert(value, context);
        if (result != null) {
            return result;
        }

        // Step 2: check if we have a special type like List that is handled separately
        result = convertSpecialTypes(context, value);
        if (result != null) {
            return result;
        }

        // Step 3: last possibility - assume it's a bean and try to map values to its structure
        return createBean(context, value);
    }

    /**
     * Converts types in the bean mapping process which require special handling.
     *
     * @param context the mapping context
     * @param value the value to convert from
     * @return object whose type matches the one in the mapping context, or null if not applicable
     */
    protected @Nullable Object convertSpecialTypes(@NotNull MappingContext context, @Nullable Object value) {
        final Class<?> rawClass = context.getTargetTypeAsClassOrThrow();
        if (Iterable.class.isAssignableFrom(rawClass)) {
            return convertToCollection(context, value);
        } else if (Map.class.isAssignableFrom(rawClass)) {
            return convertToMap(context, value);
        } else if (Optional.class.isAssignableFrom(rawClass)) {
            return convertOptional(context, value);
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
    @SuppressWarnings({"unchecked", "rawtypes"})
    protected @Nullable Collection<?> convertToCollection(@NotNull MappingContext context, @Nullable Object value) {
        if (value instanceof Iterable<?>) {
            TypeInfo entryType = context.getTargetTypeArgumentOrThrow(0);
            Collection result = createCollectionMatchingType(context);

            int index = 0;
            for (Object entry : (Iterable<?>) value) {
                MappingContext entryContext = context.createChild(pathSpecifierForIndex(index), entryType);
                Object convertedEntry = convertValueForType(entryContext, entry);
                if (convertedEntry == null) {
                    context.registerError("Cannot convert value at index " + index);
                } else {
                    result.add(convertedEntry);
                }
                ++index;
            }
            return result;
        }
        return null;
    }

    /**
     * Creates a Collection of a type which can be assigned to the provided type.
     *
     * @param mappingContext the current mapping context with a collection type
     * @return Collection of matching type
     */
    protected @NotNull Collection<?> createCollectionMatchingType(@NotNull MappingContext mappingContext) {
        Class<?> collectionType = mappingContext.getTargetTypeAsClassOrThrow();
        if (collectionType.isAssignableFrom(ArrayList.class)) {
            return new ArrayList<>();
        } else if (collectionType.isAssignableFrom(LinkedHashSet.class)) {
            return new LinkedHashSet<>();
        } else {
            throw new ConfigMeMapperException(mappingContext, "Unsupported collection type '" + collectionType + "'");
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
    @SuppressWarnings({"unchecked", "rawtypes"})
    protected @Nullable Map<?, ?> convertToMap(@NotNull MappingContext context, @Nullable Object value) {
        if (value instanceof Map<?, ?>) {
            if (context.getTargetTypeArgumentOrThrow(0).toClass() != String.class) {
                throw new ConfigMeMapperException(context, "The key type of maps may only be of String type");
            }
            TypeInfo mapValueType = context.getTargetTypeArgumentOrThrow(1);

            Map<String, ?> entries = (Map<String, ?>) value;
            Map result = createMapMatchingType(context);
            for (Map.Entry<String, ?> entry : entries.entrySet()) {
                MappingContext entryContext = context.createChild(pathSpecifierForMapKey(entry), mapValueType);
                Object mappedValue = convertValueForType(entryContext, entry.getValue());
                if (mappedValue == null) {
                    context.registerError("Cannot map value for key " + entry.getKey());
                } else {
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
     * @param mappingContext the current mapping context with a map type
     * @return Map of matching type
     */
    protected @NotNull Map<?, ?> createMapMatchingType(@NotNull MappingContext mappingContext) {
        Class<?> mapType = mappingContext.getTargetTypeAsClassOrThrow();
        if (mapType.isAssignableFrom(LinkedHashMap.class)) {
            return new LinkedHashMap<>();
        } else if (mapType.isAssignableFrom(TreeMap.class)) {
            return new TreeMap<>();
        } else {
            throw new ConfigMeMapperException(mappingContext, "Unsupported map type '" + mapType + "'");
        }
    }

    // -- Optional

    // Return value is never null, but if someone wants to override this, it's fine for it to be null
    protected @Nullable Object convertOptional(@NotNull MappingContext context, @Nullable Object value) {
        MappingContext childContext = context.createChild(OPTIONAL_SPECIFIER, context.getTargetTypeArgumentOrThrow(0));
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
    protected @Nullable Object createBean(@NotNull MappingContext context, @Nullable Object value) {
        // Ensure that the value is a map so we can map it to a bean
        if (!(value instanceof Map<?, ?>)) {
            return null;
        }

        Collection<BeanPropertyDescription> properties =
            beanDescriptionFactory.getAllProperties(context.getTargetTypeAsClassOrThrow());
        // Check that we have properties (or else we don't have a bean)
        if (properties.isEmpty()) {
            return null;
        }

        Map<?, ?> entries = (Map<?, ?>) value;
        Object bean = createBeanMatchingType(context);
        for (BeanPropertyDescription property : properties) {
            Object result = convertValueForType(
                context.createChild(property.getName(), property.getTypeInformation()),
                entries.get(property.getName()));
            if (result == null) {
                if (property.getValue(bean) == null) {
                    return null; // We do not support beans with a null value
                }
                context.registerError("No value found, fallback to field default value");
            } else {
                property.setValue(bean, result);
            }
        }
        return bean;
    }

    /**
     * Creates an object matching the given type information.
     *
     * @param mappingContext current mapping context
     * @return new instance of the given type
     */
    protected @NotNull Object createBeanMatchingType(@NotNull MappingContext mappingContext) {
        // clazz is never null given the only path that leads to this method already performs that check
        final Class<?> clazz = mappingContext.getTargetTypeAsClassOrThrow();
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (ReflectiveOperationException e) {
            throw new ConfigMeMapperException(mappingContext, "Could not create object of type '"
                + clazz.getName() + "'. It is required to have a default constructor", e);
        }
    }
}
