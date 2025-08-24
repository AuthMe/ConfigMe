package ch.jalu.configme.beanmapper.definition;

import ch.jalu.configme.beanmapper.definition.properties.BeanPropertyDescription;
import ch.jalu.configme.properties.convertresult.ConvertErrorRecorder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Definition (description) of a given bean type. A definition describes the properties of the bean and allows
 * to instantiate new beans of the given type. Objects implementing this interface are stateless.
 */
public interface BeanDefinition {

    /**
     * Returns the properties of the bean.
     *
     * @return the bean's properties
     */
    @NotNull List<BeanPropertyDescription> getProperties();

    /**
     * Creates a new bean with the given property values. The provided property values must be in the same order as
     * returned by this object's {@link #getProperties()}.
     * Null is returned if the bean cannot be created, e.g. because a property value was null and it is not supported
     * by this bean type.
     *
     * @param propertyValues the values to set to the bean (can contain null entries)
     * @param errorRecorder error recorder for errors if the bean can be created, but the values weren't fully valid
     * @return the bean, if possible, otherwise null
     */
    @Nullable Object create(@NotNull List<Object> propertyValues, @NotNull ConvertErrorRecorder errorRecorder);

}
