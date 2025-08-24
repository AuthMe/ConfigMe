package ch.jalu.configme.beanmapper.definition.properties;

import ch.jalu.typeresolver.TypeInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a property within a bean class, as used in {@link ch.jalu.configme.beanmapper.MapperImpl}.
 * There, for instance, there is a {@link BeanPropertyExtractor} field responsible for creating bean descriptions.
 * <p>
 * Default implementation is {@link BeanFieldPropertyDefinition}.
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
     * Sets the given value on the provided bean for this property. The value should correspond
     * to the {@link #getTypeInformation() property type}.
     *
     * @param bean the bean to set the property on
     * @param value the value to set
     */
    void setValue(@NotNull Object bean, @NotNull Object value);

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
