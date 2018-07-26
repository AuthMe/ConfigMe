package ch.jalu.configme.neo.beanmapper;

import ch.jalu.configme.neo.utils.TypeInformation;

import javax.annotation.Nullable;

public interface BeanProperty { // TODO: Naming clash -.-

    String getName();

    TypeInformation getTypeInformation();

    void setValue(Object bean, Object value);

    @Nullable
    Object getValue(Object bean);
}
