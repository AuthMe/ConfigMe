package ch.jalu.configme.beanmapper.migratingbeanmapper;

import ch.jalu.configme.beanmapper.MapperImpl;
import ch.jalu.configme.beanmapper.context.MappingContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;

/**
 * Extension of the bean mapper representing a simple migration where a property is changed from
 * a single value to a collection. This mapper wraps a single value into a collection whenever a
 * collection should be constructed. Example for issue #117.
 */
public class SingleValueToCollectionMapper extends MapperImpl {

    @Override
    protected @Nullable Collection<?> convertToCollection(@NotNull MappingContext context, @Nullable Object value) {
        if (!(value instanceof Iterable)) {
            Collection<?> coll = super.convertToCollection(context, Collections.singleton(value));
            // Register error to trigger a rewrite with the proper structure
            context.registerError("Found single value where a collection is expected");
            return isCollectionWithOneElement(coll) ? coll : null;
        }
        return super.convertToCollection(context, value);
    }

    private static boolean isCollectionWithOneElement(Collection<?> coll) {
        return coll != null && coll.size() == 1;
    }
}
