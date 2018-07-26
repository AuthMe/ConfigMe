package ch.jalu.configme.neo.beanmapper;

import java.util.Collection;

public interface BeanDescriptionFactory {

    Collection<BeanProperty> collectWritableFields(Class<?> clazz);

}
