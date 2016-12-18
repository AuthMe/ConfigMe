package ch.jalu.configme.beanmapper.transformer;

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

    /**
     * Constructs a transformer for the specified source and result type. If other types are encountered the
     * {@link #safeTransform(Class, Object)} will not be called. To define a transformer that converts Strings to
     * Integers, pass {@link String} and {@link Integer}. To convert anything to a String, you should pass
     * {@link Object} and {@link String} as types.
     * <p>
     * Note that primitive types are not included, e.g. a {@code TypedTransformer<Boolean, Object>} will skip
     * values where {@code value.getClass() == boolean.class}.
     *
     * @param sourceType the type the source object must be of to be considered by this transformer
     * @param resultType the type the target type needs to extend or be equal to in order to be considered
     *                   by this transformer
     */
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

    /**
     * Transforms the given value to the provided type. This method is only called if {@code type} is equal to
     * {@link R} or a subclass thereof, and if {@code value} is an object of type {@link S}. The object is never null.
     *
     * @param type the desired type of the conversion result
     * @param value the value to convert
     * @return the converted value, or null if not applicable
     */
    @Nullable
    protected abstract R safeTransform(Class<? extends R> type, S value);

}
