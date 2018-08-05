package ch.jalu.configme.beanmapper;

import ch.jalu.configme.utils.TypeInformation;

import javax.annotation.Nullable;

/**
 * Represents a bean property, as used by {@link Mapper}.
 */
public interface BeanPropertyDescription {

    /**
     * @return the name of the property in the configuration file
     */
    String getName();

    /**
     * @return property type
     */
    TypeInformation getTypeInformation();

    /**
     * Sets the given value on the provided bean for this property. The value should correspond
     * to the {@link #getTypeInformation() property type}.
     *
     * @param bean the bean to set the property on
     * @param value the value to set
     */
    void setValue(Object bean, Object value);

    /**
     * Returns the value of the property for the given bean.
     *
     * @param bean the bean to read the property from
     * @return the value of the property (can be null)
     */
    @Nullable
    Object getValue(Object bean);
}
