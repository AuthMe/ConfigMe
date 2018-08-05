package ch.jalu.configme.beanmapper;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.function.Function;

/**
 * Implementation of {@link ValueTransformer} which takes various implementations thereof and
 * returns the first non-null result of the underlying value transformers.
 */
public class CombiningValueTransformer implements ValueTransformer {

    final Collection<ValueTransformer> transformers;

    /**
     * Constructor.
     *
     * @param transformers the transformers to delegate each call to (in the given order)
     */
    public CombiningValueTransformer(ValueTransformer... transformers) {
        this(Arrays.asList(transformers));
    }

    /**
     * Constructor.
     *
     * @param transformers the transformers to delegate each call to (in the iteration order of the collection)
     */
    public CombiningValueTransformer(Collection<ValueTransformer> transformers) {
        this.transformers = Collections.unmodifiableCollection(transformers);
    }

    @Override
    public Object value(Class<?> clazz, Object value) {
        return getFirstNonNull(t -> t.value(clazz, value));
    }

    @Override
    public Object toExportValue(Object value) {
        return getFirstNonNull(t -> t.toExportValue(value));
    }

    private Object getFirstNonNull(Function<ValueTransformer, Object> callback) {
        return transformers.stream()
            .map(callback)
            .filter(Objects::nonNull)
            .findFirst().orElse(null);
    }
}
