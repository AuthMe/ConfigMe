package ch.jalu.configme.beanmapper.propertydescription;

import ch.jalu.configme.internal.record.RecordComponent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Factory which analyzes a class and returns all writable properties.
 * <p>
 * Default implementation: {@link BeanDescriptionFactoryImpl}.
 */
public interface BeanDescriptionFactory {

    /**
     * Collects all properties for the given bean type; this is done based on the class's fields.
     * An exception is thrown if the properties are not configured in a valid manner.
     *
     * @param clazz the bean type
     * @return the properties of the given bean type
     */
    @NotNull List<BeanFieldPropertyDescription> collectProperties(@NotNull Class<?> clazz);

    /**
     * Collects all properties for the given type that is a record; this is done based on the class's record components.
     * An exception is thrown if the properties are not configured in a valid manner.
     *
     * @param clazz the bean type (must be a Java record)
     * @param components the class's record components
     * @return the properties of the given bean type
     */
    @NotNull List<BeanFieldPropertyDescription> collectPropertiesForRecord(@NotNull Class<?> clazz,
                                                                           RecordComponent @NotNull [] components);

}
