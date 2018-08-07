package ch.jalu.configme.beanmapper.leafvaluehandler;

import ch.jalu.configme.utils.TypeInformation;

/**
 * Common parent of simple leaf value handlers which only need the required type as {@link Class}
 * in order to perform their conversion.
 */
public abstract class AbstractLeafValueHandler implements LeafValueHandler {

    @Override
    public Object convert(TypeInformation typeInformation, Object value) {
        return convert(typeInformation.getSafeToWriteClass(), value);
    }

    protected abstract Object convert(Class<?> clazz, Object value);

}
