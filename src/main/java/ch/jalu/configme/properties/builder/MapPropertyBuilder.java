package ch.jalu.configme.properties.builder;

import ch.jalu.configme.properties.MapProperty;
import ch.jalu.configme.properties.Property;
import ch.jalu.configme.properties.types.PropertyType;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * Builder for map properties whose keys are strings and whose value type is any arbitrary property type.
 *
 * @param <V> the type of the values in the map
 * @param <M> the map type
 * @param <P> the property type
 */
public class MapPropertyBuilder<V, M extends Map<String, V>, P extends Property<M>> {

    private String path;
    private final M defaultValue;
    private final BiFunction<String, M, P> createPropertyFunction;

    /**
     * Constructor. Prefer using the static method {@link #mapBuilder} when possible.
     *
     * @param createPropertyFunction function taking path and default value and returning a property
     * @param defaultValue empty, mutable map that contains the default values (added by builder methods)
     */
    public MapPropertyBuilder(@NotNull BiFunction<String, M, P> createPropertyFunction, @NotNull M defaultValue) {
        this.createPropertyFunction = createPropertyFunction;
        this.defaultValue = defaultValue;
    }

    /**
     * Creates a new map property builder to construct an object of ConfigMe's standard MapProperty implementation.
     *
     * @param valueType the type the values in the map have
     * @param <V> the type of the values in the map
     * @return this instance
     */
    public static <V> @NotNull MapPropertyBuilder<V, Map<String, V>, MapProperty<V>> mapBuilder(
                                                                                   @NotNull PropertyType<V> valueType) {
        return new MapPropertyBuilder<>(
            (path, defVal) -> new MapProperty<>(path, defVal, valueType),
            new LinkedHashMap<>());
    }

    /**
     * Sets the path of the property to create.
     *
     * @param path the property path to set
     * @return this instance
     */
    public @NotNull MapPropertyBuilder<V, M, P> path(@NotNull String path) {
        this.path = path;
        return this;
    }

    /**
     * Sets the given map's entries as the default value of the map property that this builder will create. This method
     * throws an exception if entries have already been added to the default value; use either this method once to
     * define all entries, or use {@link #addToDefaultValue} to add entries to the default value individually. It is not
     * recommended to mix both methods.
     *
     * @param defaultValue the map whose entries should be used as default values
     * @return this instance
     */
    public @NotNull MapPropertyBuilder<V, M, P> defaultValue(@NotNull Map<String, V> defaultValue) {
        PropertyBuilderUtils.verifyDefaultValueIsEmpty(this.defaultValue.isEmpty());
        this.defaultValue.putAll(defaultValue);
        return this;
    }

    /**
     * Adds the given (key, value) pair to the map that serves as default value of the property.
     *
     * @param key the key of the entry to add
     * @param value the value of the entry to add
     * @return this instance
     */
    public @NotNull MapPropertyBuilder<V, M, P> addToDefaultValue(@NotNull String key, @NotNull V value) {
        defaultValue.put(key, value);
        return this;
    }

    /**
     * Adds the given map entry to the map that serves as default value of the property.
     *
     * @param entry the entry to add to the default value
     * @return this instance
     */
    public @NotNull MapPropertyBuilder<V, M, P> addToDefaultValue(@NotNull Map.Entry<String, V> entry) {
        return addToDefaultValue(entry.getKey(), entry.getValue());
    }

    /**
     * Constructs the property and returns it.
     *
     * @return the property
     */
    public @NotNull P build() {
        PropertyBuilderUtils.requireNonNullPath(path);
        return createPropertyFunction.apply(path, defaultValue);
    }
}
