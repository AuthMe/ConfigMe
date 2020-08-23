package ch.jalu.configme.beanmapper;

import ch.jalu.configme.beanmapper.leafvaluehandler.LeafValueHandler;
import ch.jalu.configme.beanmapper.leafvaluehandler.StandardLeafValueHandlers;
import ch.jalu.configme.beanmapper.propertydescription.BeanDescriptionFactory;
import ch.jalu.configme.beanmapper.propertydescription.BeanDescriptionFactoryImpl;
import ch.jalu.configme.beanmapper.propertydescription.BeanPropertyDescription;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Map;

/**
 * Java implementation of {@link BaseMapperImpl}.
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
public class JavaMapperImpl extends BaseMapperImpl {

    public JavaMapperImpl() {
        super();
    }

    public JavaMapperImpl(BeanDescriptionFactory beanDescriptionFactory, LeafValueHandler leafValueHandler) {
        super(beanDescriptionFactory, leafValueHandler);
    }

    /**
     * Converts the provided value to the requested JavaBeans class if possible.
     *
     * @param context mapping context (incl. desired type)
     * @param value the value from the property resource
     * @return the converted value, or null if not possible
     */
    @Override
    @Nullable
    protected Object createBean(@NotNull MappingContext context, Object value) {
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
    protected Object createBeanMatchingType(MappingContext mappingContext) {
        // clazz is never null given the only path that leads to this method already performs that check
        final Class<?> clazz = mappingContext.getTypeInformation().getSafeToWriteClass();
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (ReflectiveOperationException e) {
            throw new ConfigMeMapperException(mappingContext, "Could not create object of type '"
                + clazz.getName() + "'. It is required to have a default constructor", e);
        }
    }
}
