package ch.jalu.configme.properties;

import ch.jalu.configme.properties.convertresult.ConvertErrorRecorder;
import ch.jalu.configme.properties.types.PropertyType;
import ch.jalu.configme.resource.PropertyReader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Property for an immutable map whose keys is of type String and whose values can be configured.
 * The map retains the order of the elements.
 *
 * @param <V> the value type of the map
 */
public class MapProperty<V> extends BaseProperty<Map<String, V>> {

    private final PropertyType<V> valueType;

    /**
     * Constructor.
     *
     * @param path the path of the property
     * @param defaultValue the default value of the property
     * @param valueType the property type of the values
     */
    public MapProperty(@NotNull String path, @NotNull Map<String, V> defaultValue, @NotNull PropertyType<V> valueType) {
        super(path, Collections.unmodifiableMap(defaultValue));
        Objects.requireNonNull(valueType, "valueType");
        this.valueType = valueType;
    }

    @Override
    protected @Nullable Map<String, V> getFromReader(@NotNull PropertyReader reader,
                                                     @NotNull ConvertErrorRecorder errorRecorder) {
        Object rawObject = reader.getObject(getPath());

        if (!(rawObject instanceof Map<?, ?>)) {
            return null;
        }

        Map<?, ?> rawMap = (Map<?, ?>) rawObject;
        Map<String, V> map = new LinkedHashMap<>();

        for (Map.Entry<?, ?> entry : rawMap.entrySet()) {
            String path = entry.getKey().toString();
            V value = valueType.convert(entry.getValue(), errorRecorder);

            if (value != null) {
                map.put(path, value);
            }
        }

        return postProcessMap(map);
    }

    @Override
    public @NotNull Object toExportValue(@NotNull Map<String, V> value) {
        Map<String, Object> exportMap = new LinkedHashMap<>();

        for (Map.Entry<String, V> entry : value.entrySet()) {
            exportMap.put(entry.getKey(), valueType.toExportValue(entry.getValue()));
        }

        return exportMap;
    }

    /* Allows to modify the map once its fully built based on the values in the property reader. */
    protected @NotNull Map<String, V> postProcessMap(@NotNull Map<String, V> constructedMap) {
        return Collections.unmodifiableMap(constructedMap);
    }
}
