package ch.jalu.configme.neo.beanmapper;

import java.util.Collection;

public interface BeanDescriptionFactory {

    Collection<BeanPropertyDescription> collectWritableFields(Class<?> clazz);

}
