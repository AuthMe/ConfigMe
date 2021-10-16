package ch.jalu.configme.beanmapper.leafvaluehandler;

import ch.jalu.configme.utils.TypeInformation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Common parent of simple leaf value handlers which only need the required type as {@link Class}
 * in order to perform their conversion.
 */
public abstract class AbstractLeafValueHandler implements LeafValueHandler {

    @Override
    public Object convert(@NotNull TypeInformation typeInformation, Object value) {
        return convert(typeInformation.getSafeToWriteClass(), value);
    }

    protected abstract @Nullable Object convert(Class<?> clazz, Object value);

}
