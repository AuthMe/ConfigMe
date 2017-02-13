package ch.jalu.configme.beanmapper.transformer;

import ch.jalu.configme.utils.TypeInformation;

import javax.annotation.Nullable;

/**
 * Transformers convert a value read from a property resource to the requested type
 * to set into a JavaBean.
 *
 * @see ch.jalu.configme.beanmapper.Mapper
 */
public interface Transformer {

    /**
     * Transforms the provided value to the requested type, if possible.
     * <p>
     * The contract is that a transformer <i>must</i> return an object of type {@code T}
     * for a given {@code type} of {@code TypeInformation<T>}. This is not forced in the
     * signature to allow handling primitive types, as well as to avoid casting nightmares.
     *
     * @param type the type to map to
     * @param value the value to transform from
     * @return the value of the given type, or null if not possible
     */
    @Nullable
    Object transform(TypeInformation<?> type, @Nullable Object value);

}
