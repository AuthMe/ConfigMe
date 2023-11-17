package ch.jalu.configme.beanmapper.propertydescription;

import ch.jalu.typeresolver.TypeInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a property within a bean class, as used in
 * {@link ch.jalu.configme.beanmapper.instantiation.BeanInstantiationService}.
 * There, for instance, there is a {@link BeanDescriptionFactory} field responsible for creating bean descriptions.
 * <p>
 * Default implementation is {@link BeanFieldPropertyDescription}.
 */
public interface BeanPropertyDescription {

    /**
     * @return the name of the property in the configuration file
     */
    @NotNull String getName();

    /**
     * @return property type
     */
    @NotNull TypeInfo getTypeInformation();

    /**
     * Returns the value of the property for the given bean.
     *
     * @param bean the bean to read the property from
     * @return the value of the property (can be null)
     */
    @Nullable Object getValue(@NotNull Object bean);

    /**
     * @return the comments associated with this property
     */
    @NotNull BeanPropertyComments getComments();

}
