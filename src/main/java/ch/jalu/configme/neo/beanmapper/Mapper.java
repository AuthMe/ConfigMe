package ch.jalu.configme.neo.beanmapper;

import ch.jalu.configme.neo.resource.PropertyReader;

public interface Mapper {

    <T> T convertToBean(PropertyReader reader, String path, Class<T> clazz);


}
