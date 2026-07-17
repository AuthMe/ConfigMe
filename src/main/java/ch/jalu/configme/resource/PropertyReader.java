package ch.jalu.configme.resource;

import ch.jalu.configme.properties.BooleanProperty;
import ch.jalu.configme.properties.DoubleProperty;
import ch.jalu.configme.properties.IntegerProperty;
import ch.jalu.configme.properties.StringProperty;
import ch.jalu.configme.properties.convertresult.PropertyValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * A property reader provides values from a resource (e.g. a YAML file) based on whose data the values of properties
 * are determined. Property readers typically provide a snapshot of the file's contents, i.e. their values are not
 * updated if the underlying file changes.
 */
public interface PropertyReader {

    /**
     * Returns whether a value is present for the given path. When applicable,
     * {@link ch.jalu.configme.properties.Property#determineValue(PropertyReader)} should be favored over
     * calling this method as it may make more type-aware checks. This method simply returns whether <i>some value</i>
     * exists under the given path.
     *
     * @param path the path to check
     * @return true if there is a value, false otherwise
     */
    boolean contains(@NotNull String path);

    /**
     * Returns the object at the given path, or null if absent.
     *
     * @param path the path to retrieve the value for
     * @return the value, or null if there is none
     */
    @Nullable Object getValue(@NotNull String path);

    /**
     * Returns the object at the given path, or null if absent.
     *
     * @param path the path to retrieve the value for
     * @return the value, or null if there is none
     * @deprecated Use {@link #getValue}
     */
    @Deprecated
    default @Nullable Object getObject(@NotNull String path) {
        return getValue(path);
    }

    /**
     * Returns the value of the given path as a String if available.
     *
     * @param path the path to retrieve a String for
     * @return the value as a String, or null if not applicable or unavailable
     * @deprecated read the value with a {@link StringProperty},
     *             or call {@link #getValue} and perform your own casts
     */
    @Deprecated
    default @Nullable String getString(@NotNull String path) {
        StringProperty strProperty = new StringProperty(path, "");
        PropertyValue<String> value = strProperty.determineValue(this);
        return value.isValidInResource() ? value.getValue() : null;
    }

    /**
     * Returns the value of the given path as an integer if available.
     *
     * @param path the path to retrieve an integer for
     * @return the value as integer, or null if not applicable or unavailable
     * @deprecated read the value with an {@link IntegerProperty},
     *             or call {@link #getValue} and perform your own casts
     */
    @Deprecated
    default @Nullable Integer getInt(@NotNull String path) {
        IntegerProperty intProperty = new IntegerProperty(path, 0);
        PropertyValue<Integer> value = intProperty.determineValue(this);
        return value.isValidInResource() ? value.getValue() : null;
    }

    /**
     * Returns the value of the given path as a double if available.
     *
     * @param path the path to retrieve a double for
     * @return the value as a double, or null if not applicable or unavailable
     * @deprecated read the value with an {@link DoubleProperty},
     *             or call {@link #getValue} and perform your own casts
     */
    @Deprecated
    default @Nullable Double getDouble(@NotNull String path) {
        DoubleProperty doubleProperty = new DoubleProperty(path, 0);
        PropertyValue<Double> value = doubleProperty.determineValue(this);
        return value.isValidInResource() ? value.getValue() : null;
    }

    /**
     * Returns the value of the given path as a boolean if available.
     *
     * @param path the path to retrieve a boolean for
     * @return the value as a boolean, or null if not applicable or unavailable
     * @deprecated read the value with a {@link BooleanProperty},
     *             or call {@link #getValue} and perform your own casts
     */
    @Deprecated
    default @Nullable Boolean getBoolean(@NotNull String path) {
        BooleanProperty boolProperty = new BooleanProperty(path, true);
        PropertyValue<Boolean> value = boolProperty.determineValue(this);
        return value.isValidInResource() ? value.getValue() : null;
    }

    /**
     * Returns the value of the given path as a list if available.
     *
     * @param path the path to retrieve a list for
     * @return the value as a list, or null if not applicable or unavailable
     * @deprecated read the value with a {@link ch.jalu.configme.properties.ListProperty ListProperty},
     *             or call {@link #getValue} and perform your own casts
     */
    @Deprecated
    default @Nullable List<?> getList(@NotNull String path) {
        Object value = getValue(path);
        return value instanceof List<?> ? (List<?>) value : null;
    }

}
