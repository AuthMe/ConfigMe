package ch.jalu.configme.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Collection utils.
 */
public final class CollectionUtils {

    private CollectionUtils() {
    }

    /**
     * Gets all elements from a list starting from the given index.
     *
     * @param list the List
     * @param start the start index
     * @param <T> the list type
     * @return the sublist of all elements from index {@code start} and on; empty list
     *         if the start index exceeds the list's size
     */
    public static <T> List<T> getRange(List<T> list, int start) {
        if (start >= list.size()) {
            return new ArrayList<>();
        }
        return list.subList(start, list.size());
    }

    /**
     * Returns all entries that are the same at the beginning of both given lists.
     *
     * @param list1 first list
     * @param list2 second list
     * @param <T> type of the lists
     * @return list of all equal entries at the start of both lists
     */
    public static <T> List<T> filterCommonStart(List<T> list1, List<T> list2) {
        List<T> commonStart = new ArrayList<>();
        int minSize = Math.min(list1.size(), list2.size());
        int i = 0;
        while (i < minSize && Objects.equals(list1.get(i), list2.get(i))) {
            commonStart.add(list1.get(i));
            ++i;
        }
        return commonStart;
    }

}
