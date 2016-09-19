package com.github.authme.configme.beanmapper;

import javax.annotation.Nullable;
import java.lang.reflect.Type;

/**
 * Typed implementation of {@link Transformer} for convenient extension.
 *
 * @param <S> the source type
 * @param <R> the result type
 */
public abstract class TypedTransformer<S, R> implements Transformer {

    private final Class<S> sourceType;
    private final Class<R> resultType;

    public TypedTransformer(Class<S> sourceType, Class<R> resultType) {
        this.sourceType = sourceType;
        this.resultType = resultType;
    }

    @Override
    public Object transform(Class<?> type, Type genericType, Object value) {
        if (resultType.isAssignableFrom(type) && sourceType.isInstance(value)) {
            return safeTransform((Class) type, (S) value);
        }
        return null;
    }

    @Nullable
    protected abstract R safeTransform(Class<? extends R> type, S value);

}
