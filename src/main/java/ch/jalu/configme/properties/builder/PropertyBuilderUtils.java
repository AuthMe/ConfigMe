package ch.jalu.configme.properties.builder;

import org.jetbrains.annotations.Nullable;

/**
 * Utilities for property builders.
 */
final class PropertyBuilderUtils {

    /**
     * Method name to reference in an error message, for property builders that have an
     * array, collection or map as their value.
     */
    static final String ADD_TO_DEFAULT_VALUE_METHOD = "addToDefaultValue";

    private PropertyBuilderUtils() {
    }

    /**
     * Verifies that the given path is not null, throwing an exception if it is.
     *
     * @param path the path to check
     */
    static void requireNonNullPath(@Nullable String path) {
        if (path == null) {
            throw new IllegalStateException("The path of the property must be defined");
        }
    }

    /**
     * Throws an exception referring to the method {@code addToDefaultValue} if the provided parameter indicates that
     * the default value collection/map is not empty.
     *
     * @param isEmpty whether the default value (collection/map) is currently empty
     */
    static void verifyDefaultValueIsEmpty(boolean isEmpty) {
        if (!isEmpty) {
            throw new IllegalStateException("Default values have already been defined! Use "
                + ADD_TO_DEFAULT_VALUE_METHOD + " to add entries individually");
        }
    }
}
