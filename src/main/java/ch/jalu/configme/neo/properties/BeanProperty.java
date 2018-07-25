package ch.jalu.configme.neo.properties;

import ch.jalu.configme.neo.propertytype.PropertyType;

public class BeanProperty<T> extends BaseProperty<T> {

    public BeanProperty(String path, T defaultValue, PropertyType<T> propertyType) {
        super(path, defaultValue, propertyType);
    }
}
