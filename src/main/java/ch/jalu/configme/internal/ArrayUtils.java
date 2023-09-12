package ch.jalu.configme.internal;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;

/**
 * Array utils.
 */
public final class ArrayUtils {

    private ArrayUtils() {
    }

    /**
     * Creates an array of the given size whose component type is the specified class. An exception is thrown
     * if the component type is a primitive type, or void.
     *
     * @param component the component class the array must have
     * @param size the size the array must have
     * @param <T> type of array elements
     * @return array of the given type and size
     */
    @SuppressWarnings("unchecked")
    public static <T> T @NotNull [] createArrayForReferenceType(@NotNull Class<T> component, int size) {
        if (component.isPrimitive()) {
            throw new IllegalArgumentException("The component type may not be a primitive type, but got: " + component);
        }
        return (T[]) Array.newInstance(component, size);
    }
}
