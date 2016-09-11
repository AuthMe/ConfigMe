package com.github.authme.configme.beanmapper;

import javax.annotation.Nullable;

/**
 * Typed implementation of {@link Transformer} for convenient extension.
 */
public abstract class TypedTransformer<T, R> implements Transformer {

    private final Class<T> inputType;
    private final Class<R> resultType;

    public TypedTransformer(Class<T> inputType, Class<R> resultType) {
        this.inputType = inputType;
        this.resultType = resultType;
    }

    @Override
    public Object transform(Class<?> type, Object value) {
        if (resultType.isAssignableFrom(type) && inputType.isInstance(value)) {
            return safeTransform((Class) type, (T) value);
        }
        return null;
    }

    @Nullable
    protected abstract R safeTransform(Class<? extends R> type, T value);

}
