package ch.jalu.configme.beanmapper.instantiation;

import ch.jalu.configme.beanmapper.propertydescription.BeanPropertyDescription;
import ch.jalu.configme.properties.convertresult.ConvertErrorRecorder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Creation method for a given bean type. A bean instantiation returns the properties that are needed to create it
 * and allows to create beans of the given type. Objects implementing this interface are stateless.
 */
public interface BeanInstantiation {

    /**
     * Returns the properties of the bean.
     *
     * @return the bean's properties
     */
    @NotNull List<BeanPropertyDescription> getProperties();

    /**
     * Creates a new bean with the given property values. The provided property values must be in the same order as
     * returned by this instantiation's {@link #getProperties()}.
     * Null is returned if the bean cannot be created, e.g. because a property value was null and it is not supported
     * by this instantiation.
     *
     * @param propertyValues the values to set to the bean (can contain null entries)
     * @param errorRecorder error recorder for errors if the bean can be created, but the values weren't fully valid
     * @return the bean, if possible, otherwise null
     */
    @Nullable Object create(@NotNull List<Object> propertyValues, @NotNull ConvertErrorRecorder errorRecorder);

}
