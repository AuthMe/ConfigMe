package ch.jalu.configme.properties.types;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * Property type for lists.
 *
 * @param <E> the type of the elements in the list
 */
public class ListPropertyType<E> extends CollectionPropertyType<E, List<E>> {

    public ListPropertyType(@NotNull PropertyType<E> entryType) {
        super(entryType);
    }

    @Override
    protected @NotNull Collector<E, ?, List<E>> resultCollector() {
        // Note: Collectors#toList creates an ArrayList, but the Javadoc makes no guarantees about what type of List
        // will actually be returned, so we'll explicitly use an ArrayList here.
        return Collectors.toCollection(ArrayList::new);
    }
}
