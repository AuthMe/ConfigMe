package ch.jalu.configme.properties.types;

import ch.jalu.configme.properties.convertresult.ConvertErrorRecorder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Property types for maps with strings as keys and any value type. The produced maps keep insertion order (as given
 * in the property resource). Maps produced by this type never have a null key or a null value.
 *
 * @param <V> the type of values in the map
 */
public class MapPropertyType<V> implements PropertyType<Map<String, V>> {

    private final PropertyType<V> valueType;

    /**
     * Constructor.
     *
     * @param valueType property type to handle the map's values
     */
    public MapPropertyType(@NotNull PropertyType<V> valueType) {
        this.valueType = valueType;
    }

    @Override
    public @Nullable Map<String, V> convert(@Nullable Object object, @NotNull ConvertErrorRecorder errorRecorder) {
        if (!(object instanceof Map<?, ?>)) {
            return null;
        }

        Map<?, ?> rawMap = (Map<?, ?>) object;
        Map<String, V> map = createResultMap();

        for (Map.Entry<?, ?> entry : rawMap.entrySet()) {
            String key = convertKeyToString(entry.getKey());
            V value = valueType.convert(entry.getValue(), errorRecorder);

            if (key != null && value != null) {
                V previous = map.put(key, value);
                if (previous != null) {
                    errorRecorder.setHasError("Duplicate key detected: '" + key + "'");
                }
            } else {
                errorRecorder.setHasError("Key or value could not be converted for key '" + entry.getKey() + "'");
            }
        }
        return map;
    }

    @Override
    public @NotNull Map<String, Object> toExportValue(@NotNull Map<String, V> value) {
        Map<String, Object> exportMap = new LinkedHashMap<>(value.size());
        for (Map.Entry<String, V> entry : value.entrySet()) {
            exportMap.put(entry.getKey(), valueType.toExportValue(entry.getValue()));
        }
        return exportMap;
    }

    public final @NotNull PropertyType<V> getValueType() {
        return valueType;
    }

    /**
     * @return new map to which entries are added when converting
     */
    protected @NotNull Map<String, V> createResultMap() {
        return new LinkedHashMap<>();
    }

    /**
     * Converts the given key value from the property reader to a String to be used as key. Returns null if
     * the value is invalid and has no appropriate representation.
     *
     * @param key the key to convert
     * @return string key, or null if not applicable
     */
    protected @Nullable String convertKeyToString(@Nullable Object key) {
        return key == null ? null : key.toString();
    }
}
