package ch.jalu.configme.beanmapper;

import java.util.Collection;

/**
 * Factory which analyzes a class and returns all writable properties.
 */
public interface BeanDescriptionFactory {

    /**
     * Returns all properties on the given class which should be considered while creating a bean of the
     * given type. This is usually all properties which can be read from and written to.
     *
     * @param clazz the class whose properties should be returned
     * @return the relevant properties on the class
     */
    Collection<BeanPropertyDescription> findAllWritableProperties(Class<?> clazz);

}
