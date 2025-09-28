package ch.jalu.configme.properties;

import ch.jalu.configme.properties.types.MapPropertyType;
import ch.jalu.configme.properties.types.PropertyType;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Map;

/**
 * Property for a map with String keys and a configurable value type. The map retains the order of the elements.
 * Maps produced by this property are guaranteed to never have a null key or null value.
 *
 * @param <V> the value type of the map
 */
public class MapProperty<V> extends TypeBasedProperty<Map<String, V>> {

    /**
     * Constructor. Builds a {@link MapProperty} with an empty map as default value.
     *
     * @param path the path of the property
     * @param valueType the property type of the values
     */
    public MapProperty(@NotNull String path, @NotNull PropertyType<V> valueType) {
        super(path, new MapPropertyType<>(valueType), Collections.emptyMap());
    }

    /**
     * Constructor.
     *
     * @param path the path of the property
     * @param valueType the property type of the values
     * @param defaultValue the default value of the property
     */
    public MapProperty(@NotNull String path, @NotNull PropertyType<V> valueType, @NotNull Map<String, V> defaultValue) {
        super(path, new MapPropertyType<>(valueType), defaultValue);
    }

    /**
     * Constructor. Use {@link #withMapType}.
     *
     * @param mapType the map type
     * @param path the path of the property
     * @param defaultValue the default value of the property
     */
    // Constructor arguments are usually (path, type, defaultValue), but this is not possible here because there
    // are other constructors with the same argument order.
    protected MapProperty(@NotNull PropertyType<Map<String, V>> mapType, @NotNull String path,
                          @NotNull Map<String, V> defaultValue) {
        super(path, mapType, defaultValue);
    }

    /**
     * Creates a new map property with the given path and type. An empty map is set as default value.
     *
     * @param path the path of the property
     * @param mapType the map type
     * @param <V> the type of the values in the map
     * @return a new map property
     */
    public static <V> MapProperty<V> withMapType(@NotNull String path, @NotNull PropertyType<Map<String, V>> mapType) {
        return new MapProperty<>(mapType, path, Collections.emptyMap());
    }

    /**
     * Creates a new map property with the given path, type and default value.
     *
     * @param path the path of the property
     * @param mapType the map type
     * @param defaultValue the default value of the property
     * @param <V> the type of the values in the map
     * @return a new map property
     */
    public static <V> MapProperty<V> withMapType(@NotNull String path, @NotNull PropertyType<Map<String, V>> mapType,
                                                 @NotNull Map<String, V> defaultValue) {
        return new MapProperty<>(mapType, path, defaultValue);
    }
}
