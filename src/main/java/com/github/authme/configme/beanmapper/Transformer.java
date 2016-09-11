package com.github.authme.configme.beanmapper;

import javax.annotation.Nullable;

/**
 * Transforms a value into the requested type.
 */
public interface Transformer {

    @Nullable
    Object transform(Class<?> type, @Nullable Object value);

}
