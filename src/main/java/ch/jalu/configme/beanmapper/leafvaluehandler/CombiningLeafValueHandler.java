package ch.jalu.configme.beanmapper.leafvaluehandler;

import ch.jalu.configme.utils.TypeInformation;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.function.Function;

/**
 * Implementation of {@link LeafValueHandler} which takes various implementations thereof and
 * returns the first non-null result of the underlying leaf value handlers.
 */
public class CombiningLeafValueHandler implements LeafValueHandler {

    private final Collection<LeafValueHandler> handlers;

    /**
     * Constructor.
     *
     * @param handlers the handlers to delegate each call to (in the given order)
     */
    public CombiningLeafValueHandler(LeafValueHandler... handlers) {
        this(Arrays.asList(handlers));
    }

    /**
     * Constructor.
     *
     * @param handlers the handlers to delegate each call to (in the iteration order of the collection)
     */
    public CombiningLeafValueHandler(Collection<LeafValueHandler> handlers) {
        this.handlers = Collections.unmodifiableCollection(handlers);
    }

    @Override
    public Object convert(TypeInformation typeInformation, Object value) {
        return getFirstNonNull(t -> t.convert(typeInformation, value));
    }

    @Override
    public Object toExportValue(Object value) {
        return getFirstNonNull(t -> t.toExportValue(value));
    }

    protected final Collection<LeafValueHandler> getHandlers() {
        return handlers;
    }

    private Object getFirstNonNull(Function<LeafValueHandler, Object> callback) {
        return handlers.stream()
            .map(callback)
            .filter(Objects::nonNull)
            .findFirst().orElse(null);
    }
}
