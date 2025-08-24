package ch.jalu.configme.beanmapper.definition.properties;

import ch.jalu.configme.beanmapper.definition.BeanDefinition;
import ch.jalu.typeresolver.TypeInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a property within a bean class, as used by {@link BeanDefinition}.
 * Bean property definitions are provided by {@link BeanPropertyExtractor}.
 * <p>
 * Default implementation of this interface is {@link BeanFieldPropertyDefinition}.
 */
public interface BeanPropertyDefinition {

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
